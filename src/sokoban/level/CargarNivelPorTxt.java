package sokoban.level;

import sokoban.level.factory.RegistroDeCreadores;
import sokoban.model.Jugador;
import sokoban.model.Posicion;
import sokoban.model.PowerUpEnMapa;
import sokoban.model.Tablero;
import sokoban.model.box.Caja;
import sokoban.model.terrain.Terreno;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Carga niveles desde archivos de texto: un carácter por celda.
 *
 * <pre>
 *  #      Muro
 *  ' '    Piso (si queda fuera del área alcanzable por el jugador, se carga como Muro)
 *  - _    Piso explícito (nunca se convierte en Muro)
 *  .      Destino
 *  ~      Terreno resbaladizo
 *  a-e    Cerrojo; abre los muros con la misma letra en mayúscula
 *  A-E    Muro cerrado vinculado al cerrojo de su letra
 *  @      Jugador
 *  +      Jugador sobre un destino
 *  $      Caja normal
 *  *      Caja normal sobre un destino
 *  K-O    Caja llave; K abre el cerrojo a, L el b, M el c, N el d y O el e
 *  1-9    Caja frágil; el dígito es la cantidad de empujes que aguanta
 *  H      Power-up martillo sobre piso
 *  F      Power-up superfuerza sobre piso
 * </pre>
 *
 * Las líneas que empiezan con ';' son comentarios. Las filas más cortas se completan con espacios.
 * Los archivos se buscan como {@code nivel1.txt}, {@code nivel2.txt}, ... dentro del directorio dado.
 *
 * <h3>Rol en el patrón Factory Method</h3>
 * <p>Esta clase es el <b>cliente</b>: lee el archivo y, para cada celda, le pide al
 * {@link RegistroDeCreadores} que construya el terreno y la caja. <b>No contiene ningún
 * {@code new} de terrenos o cajas ni conoce sus clases concretas</b>; la tabla "símbolo → tipo"
 * ya no está acá sino en los creadores del paquete {@code sokoban.level.factory}. Por eso
 * agregar un tipo nuevo no obliga a modificar esta clase.</p>
 *
 * <p>Lo que sí queda a cargo del cargador, porque no es "fabricar objetos" sino interpretar el
 * formato:</p>
 * <ul>
 *   <li>Parsear el archivo (comentarios, BOM, filas cortas, bordes vacíos).</li>
 *   <li>Decidir si un espacio es piso o exterior (ver {@link #marcarAlcanzables}).</li>
 *   <li>Ubicar al jugador. Es único por nivel y no es ni terreno ni caja, así que no justifica
 *       una familia de creadores.</li>
 *   <li>Validar el nivel completo (cantidad de cajas, destinos, etc.).</li>
 * </ul>
 */
public class CargarNivelPorTxt implements CargadorDeNivel {

    private static final String PATRON_ARCHIVO = "nivel%d.txt";
    private static final int[][] VECINOS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    /** Símbolo que se usa para tratar como muro al espacio en blanco del exterior. */
    private static final char SIMBOLO_MURO = '#';
    private static final char PRIMERA_LLAVE = 'K';
    private static final char PRIMER_CERROJO = 'a';
    private static final char ULTIMO_CERROJO = 'e';
    private static final String SIMBOLOS_DESTINO = ".+*";

    private final Path directorio;
    private final RegistroDeCreadores registro;

    /** Usa los creadores estándar del juego ({@link RegistroDeCreadores#porDefecto()}). */
    public CargarNivelPorTxt(Path directorio) {
        this(directorio, RegistroDeCreadores.porDefecto());
    }

    /**
     * Permite inyectar un registro propio, por ejemplo con tipos de terreno adicionales o con
     * un subconjunto de los estándar.
     *
     * @param directorio carpeta donde están los {@code nivelN.txt}
     * @param registro   creadores que se usarán para interpretar los símbolos
     */
    public CargarNivelPorTxt(Path directorio, RegistroDeCreadores registro) {
        this.directorio = Objects.requireNonNull(directorio, "directorio");
        this.registro = Objects.requireNonNull(registro, "registro");
    }

    @Override
    public Tablero cargarNivel(int numeroDeNivel) throws CargaDeNivelException {
        return cargarDesdeArchivo(directorio.resolve(String.format(PATRON_ARCHIVO, numeroDeNivel)));
    }

    @Override
    public boolean existeNivel(int numeroDeNivel) {
        return Files.isRegularFile(directorio.resolve(String.format(PATRON_ARCHIVO, numeroDeNivel)));
    }

    public Tablero cargarDesdeArchivo(Path archivo) throws CargaDeNivelException {
        List<String> lineas;
        try {
            lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new CargaDeNivelException("No existe el archivo " + archivo, e);
        } catch (IOException e) {
            throw new CargaDeNivelException("No se pudo leer " + archivo + ": " + e.getMessage(), e);
        }
        try {
            return cargarDesdeLineas(lineas);
        } catch (CargaDeNivelException e) {
            throw new CargaDeNivelException(archivo.getFileName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Construye el tablero a partir de las líneas ya leídas. Es público para poder probar la
     * carga sin tocar el disco.
     */
    public Tablero cargarDesdeLineas(List<String> lineas) throws CargaDeNivelException {
        char[][] mapa = construirMapa(lineas);
        Posicion posicionJugador = ubicarJugador(mapa);
        boolean[][] alcanzable = marcarAlcanzables(mapa, posicionJugador);

        int filas = mapa.length;
        int columnas = mapa[0].length;
        Terreno[][] cuadricula = new Terreno[filas][columnas];
        List<Caja> cajas = new ArrayList<>();
        List<PowerUpEnMapa> powerUps = new ArrayList<>();
        int destinos = 0;
        int cajasParaDestino = 0;

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                char simbolo = mapa[f][c];

                // Un espacio fuera del área alcanzable es el exterior del nivel: se trata como muro.
                // Se resuelve ANTES de pedir el terreno para que los creadores no necesiten
                // conocer el contexto del tablero.
                char simboloTerreno = (simbolo == ' ' && !alcanzable[f][c]) ? SIMBOLO_MURO : simbolo;

                // Capa 1: terreno (obligatorio en todas las celdas).
                Terreno terreno = crearTerreno(simboloTerreno, f, c);
                cuadricula[f][c] = terreno;
                if (terreno.esDestino()) {
                    destinos++;
                }

                // Capa 2: caja (opcional; la mayoría de las celdas no tienen).
                Optional<Caja> caja = registro.crearCaja(simbolo, new Posicion(f, c));
                if (caja.isPresent()) {
                    cajas.add(caja.get());
                    if (caja.get().requiereDestino()) {
                        cajasParaDestino++;
                    }
                }
                registro.crearPowerUp(simbolo, new Posicion(f, c)).ifPresent(powerUps::add);
            }
        }

        validarCajasYDestinos(cajas.size(), cajasParaDestino, destinos);
        validarBordes(alcanzable);
        validarDestinosAlcanzables(mapa, alcanzable);
        validarCerrojos(mapa);
        return new Tablero(cuadricula, new Jugador(posicionJugador), cajas, powerUps,
                marcarMurosProtegidos(mapa, alcanzable));
    }

    private boolean[][] marcarMurosProtegidos(char[][] mapa, boolean[][] alcanzable) {
        int filas = mapa.length;
        int columnas = mapa[0].length;
        boolean[][] protegidos = new boolean[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                boolean enBorde = f == 0 || c == 0 || f == filas - 1 || c == columnas - 1;
                protegidos[f][c] = enBorde || esExterior(mapa, alcanzable, f, c) || tocaExterior(mapa, alcanzable, f, c);
            }
        }
        return protegidos;
    }

    private boolean esExterior(char[][] mapa, boolean[][] alcanzable, int fila, int columna) {
        return mapa[fila][columna] == ' ' && !alcanzable[fila][columna];
    }

    private boolean tocaExterior(char[][] mapa, boolean[][] alcanzable, int fila, int columna) {
        for (int[] d : VECINOS) {
            int f = fila + d[0];
            int c = columna + d[1];
            boolean dentro = f >= 0 && f < mapa.length && c >= 0 && c < mapa[0].length;
            if (dentro && esExterior(mapa, alcanzable, f, c)) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------- validación

    private void validarCajasYDestinos(int cajas, int cajasParaDestino, int destinos) throws CargaDeNivelException {
        if (cajas == 0) {
            throw new CargaDeNivelException("el nivel no tiene cajas");
        }
        if (destinos == 0) {
            throw new CargaDeNivelException("el nivel no tiene destinos (.)");
        }
        if (cajasParaDestino < destinos) {
            throw new CargaDeNivelException("hay " + destinos + " destinos y solo " + cajasParaDestino
                    + " cajas para llevar a ellos");
        }
    }

    private void validarBordes(boolean[][] alcanzable) throws CargaDeNivelException {
        int ultimaFila = alcanzable.length - 1;
        int ultimaColumna = alcanzable[0].length - 1;
        for (int f = 0; f <= ultimaFila; f++) {
            for (int c = 0; c <= ultimaColumna; c++) {
                boolean enBorde = f == 0 || f == ultimaFila || c == 0 || c == ultimaColumna;
                if (enBorde && alcanzable[f][c]) {
                    throw new CargaDeNivelException("el nivel no está cerrado por paredes");
                }
            }
        }
    }

    private void validarDestinosAlcanzables(char[][] mapa, boolean[][] alcanzable) throws CargaDeNivelException {
        for (int f = 0; f < mapa.length; f++) {
            for (int c = 0; c < mapa[f].length; c++) {
                if (SIMBOLOS_DESTINO.indexOf(mapa[f][c]) >= 0 && !alcanzable[f][c]) {
                    throw new CargaDeNivelException(
                            String.format("fila %d, columna %d: destino inalcanzable", f + 1, c + 1));
                }
            }
        }
    }

    private void validarCerrojos(char[][] mapa) throws CargaDeNivelException {
        for (char letra = PRIMER_CERROJO; letra <= ULTIMO_CERROJO; letra++) {
            char muro = Character.toUpperCase(letra);
            char llave = (char) (PRIMERA_LLAVE + letra - PRIMER_CERROJO);
            int cerrojos = contarSimbolo(mapa, letra);
            int llaves = contarSimbolo(mapa, llave);
            if ((cerrojos > 0) != (contarSimbolo(mapa, muro) > 0)) {
                throw new CargaDeNivelException("el cerrojo " + letra + " y el muro " + muro + " deben aparecer juntos");
            }
            if ((cerrojos > 0) != (llaves > 0)) {
                throw new CargaDeNivelException("el cerrojo " + letra + " y la llave " + llave + " deben aparecer juntos");
            }
            if (llaves > cerrojos) {
                throw new CargaDeNivelException("hay " + llaves + " llaves " + llave + " y solo " + cerrojos + " cerrojos");
            }
        }
    }

    private int contarSimbolo(char[][] mapa, char simbolo) {
        int total = 0;
        for (char[] fila : mapa) {
            for (char actual : fila) {
                if (actual == simbolo) {
                    total++;
                }
            }
        }
        return total;
    }

    // ---------------------------------------------------------------- lectura

    /** Quita comentarios y filas vacías de los bordes, y rellena las filas cortas con espacios. */
    private char[][] construirMapa(List<String> lineas) throws CargaDeNivelException {
        List<String> filas = new ArrayList<>();
        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (i == 0 && linea.startsWith("\uFEFF")) {
                linea = linea.substring(1); // BOM que agregan algunos editores de Windows
            }
            if (!linea.startsWith(";")) {
                filas.add(linea.stripTrailing());
            }
        }
        while (!filas.isEmpty() && filas.get(0).isEmpty()) {
            filas.remove(0);
        }
        while (!filas.isEmpty() && filas.get(filas.size() - 1).isEmpty()) {
            filas.remove(filas.size() - 1);
        }
        if (filas.isEmpty()) {
            throw new CargaDeNivelException("el archivo no contiene ningún tablero");
        }

        int columnas = filas.stream().mapToInt(String::length).max().getAsInt();
        char[][] mapa = new char[filas.size()][columnas];
        for (int f = 0; f < mapa.length; f++) {
            Arrays.fill(mapa[f], ' ');
            filas.get(f).getChars(0, filas.get(f).length(), mapa[f], 0);
        }
        return mapa;
    }

    private Posicion ubicarJugador(char[][] mapa) throws CargaDeNivelException {
        Posicion encontrada = null;
        int cantidad = 0;
        for (int f = 0; f < mapa.length; f++) {
            for (int c = 0; c < mapa[f].length; c++) {
                if (mapa[f][c] == '@' || mapa[f][c] == '+') {
                    cantidad++;
                    if (encontrada == null) {
                        encontrada = new Posicion(f, c);
                    }
                }
            }
        }
        if (cantidad != 1) {
            throw new CargaDeNivelException(
                    "debe haber exactamente un jugador (@ o +) y se encontraron " + cantidad);
        }
        return encontrada;
    }

    /**
     * Recorre desde el jugador todo lo que no sea muro. Los espacios que no se alcanzan son
     * el exterior del nivel: así los archivos con formas irregulares no muestran piso fuera de los muros.
     */
    private boolean[][] marcarAlcanzables(char[][] mapa, Posicion inicio) {
        int filas = mapa.length;
        int columnas = mapa[0].length;
        boolean[][] visitado = new boolean[filas][columnas];
        Deque<Posicion> pendientes = new ArrayDeque<>();
        visitado[inicio.fila()][inicio.columna()] = true;
        pendientes.add(inicio);

        while (!pendientes.isEmpty()) {
            Posicion actual = pendientes.poll();
            for (int[] d : VECINOS) {
                int f = actual.fila() + d[0];
                int c = actual.columna() + d[1];
                if (f < 0 || f >= filas || c < 0 || c >= columnas || visitado[f][c] || mapa[f][c] == SIMBOLO_MURO) {
                    continue;
                }
                visitado[f][c] = true;
                pendientes.add(new Posicion(f, c));
            }
        }
        return visitado;
    }

    // ------------------------------------------------------------ construcción

    /**
     * Delega en el registro y, si el símbolo no existe, agrega la ubicación al mensaje para
     * que el autor del nivel sepa exactamente qué celda corregir (filas y columnas desde 1).
     */
    private Terreno crearTerreno(char simbolo, int fila, int columna) throws CargaDeNivelException {
        try {
            return registro.crearTerreno(simbolo);
        } catch (CargaDeNivelException e) {
            throw new CargaDeNivelException(
                    String.format("fila %d, columna %d: %s", fila + 1, columna + 1, e.getMessage()), e);
        }
    }
}
