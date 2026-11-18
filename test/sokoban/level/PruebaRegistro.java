package sokoban.level;

import sokoban.level.factory.CreadorCaja;
import sokoban.level.factory.CreadorCajaNormal;
import sokoban.level.factory.CreadorMartillo;
import sokoban.level.factory.CreadorMuro;
import sokoban.level.factory.CreadorTerreno;
import sokoban.level.factory.CreadorTerrenoDestino;
import sokoban.level.factory.CreadorTerrenoNormal;
import sokoban.level.factory.RegistroDeCreadores;
import sokoban.dto.TileData;
import sokoban.dto.TipoPowerUp;
import sokoban.dto.TileType;
import sokoban.model.Posicion;
import sokoban.model.Tablero;
import sokoban.model.box.Caja;
import sokoban.model.box.CajaFragil;
import sokoban.model.box.CajaLlave;
import sokoban.model.box.CajaNormal;
import sokoban.model.terrain.Door;
import sokoban.model.terrain.Lock;
import sokoban.model.terrain.Muro;
import sokoban.model.terrain.Terreno;
import sokoban.model.terrain.TerrenoDestino;
import sokoban.model.terrain.TerrenoNormal;
import sokoban.model.terrain.TerrenoResbaladizo;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Pruebas sin dependencias externas (no hace falta JUnit) del paquete de fábricas.
 *
 * <p>Compilar y ejecutar desde la raíz del proyecto (ver README):</p>
 * <pre>
 *   javac -encoding UTF-8 -d out $(find src test -name "*.java")
 *   java -cp out sokoban.level.PruebaRegistro
 * </pre>
 * Termina con código 0 si todo pasa y 1 si algo falla.
 */
public class PruebaRegistro {

    private static int pasaron = 0;
    private static int fallaron = 0;

    // ======================================================================
    //  Ejemplo de EXTENSIÓN: un terreno nuevo que el juego original no tiene.
    //  Para sumarlo bastan estas dos clases + una línea de registro. No se
    //  modifica CargarNivelPorTxt, ni RegistroDeCreadores, ni ningún creador.
    // ======================================================================

    /** Terreno nuevo: no se puede pisar (como una trampa que bloquea el paso). */
    static class Trampa extends Terreno {
        @Override
        public boolean esTransitable() {
            return false;
        }

        @Override
        public TileData toData() {
            return TileData.of(TileType.WALL);
        }
    }

    /** Caja nueva: se agrega con su creador, sin tocar los creadores de terreno. */
    static class CreadorCajaNueva implements CreadorCaja {
        @Override
        public boolean reconoce(char simbolo) {
            return simbolo == '%';
        }

        @Override
        public Caja crear(char simbolo, Posicion posicion) {
            return new CajaNormal(posicion);
        }
    }

    /** Su creador: reconoce el símbolo '^'. */
    static class CreadorTrampa implements CreadorTerreno {
        @Override
        public boolean reconoce(char simbolo) {
            return simbolo == '^';
        }

        @Override
        public Terreno crear(char simbolo) {
            return new Trampa();
        }
    }

    public static void main(String[] args) throws Exception {
        todosLosSimbolosProducenElTipoEsperado();
        losParametrosSalenDelSimbolo();
        cadaLlamadaDevuelveUnaInstanciaNueva();
        simboloDesconocidoIndicaFilaYColumna();
        dosCreadoresParaElMismoSimboloSeDetectan();
        unRegistroMinimoNoConoceLosOtrosTipos();
        unTerrenoNuevoSeAgregaSinTocarElCargador();
        unaCajaNuevaSeAgregaSinTocarLosTerrenos();
        losNivelesInvalidosSeRechazan();
        losPoderesSeCreanConSusCreadores();
        losNivelesDeEjemploSeCargan();

        System.out.printf("%n%d pruebas OK, %d fallidas%n", pasaron, fallaron);
        System.exit(fallaron == 0 ? 0 : 1);
    }

    // ---------------------------------------------------------------- pruebas

    /** Cada símbolo del formato debe mapear a su terreno y a su caja (o a ninguna). */
    static void todosLosSimbolosProducenElTipoEsperado() throws Exception {
        RegistroDeCreadores r = RegistroDeCreadores.porDefecto();
        Posicion p = new Posicion(0, 0);

        // símbolo, clase de terreno esperada, clase de caja esperada (null = sin caja)
        Object[][] tabla = {
                {'#', Muro.class, null},
                {' ', TerrenoNormal.class, null},
                {'-', TerrenoNormal.class, null},
                {'_', TerrenoNormal.class, null},
                {'.', TerrenoDestino.class, null},
                {'~', TerrenoResbaladizo.class, null},
                {'a', Lock.class, null},
                {'A', Door.class, null},
                {'@', TerrenoNormal.class, null},
                {'+', TerrenoDestino.class, null},
                {'$', TerrenoNormal.class, CajaNormal.class},
                {'*', TerrenoDestino.class, CajaNormal.class},
                {'K', TerrenoNormal.class, CajaLlave.class},
                {'L', TerrenoNormal.class, CajaLlave.class},
                {'H', TerrenoNormal.class, null},
                {'F', TerrenoNormal.class, null},
                {'5', TerrenoNormal.class, CajaFragil.class},
        };
        for (Object[] fila : tabla) {
            char s = (Character) fila[0];
            Class<?> terreno = (Class<?>) fila[1];
            Class<?> caja = (Class<?>) fila[2];

            verificar("terreno de '" + s + "' es " + terreno.getSimpleName(),
                    r.crearTerreno(s).getClass() == terreno);

            Optional<Caja> c = r.crearCaja(s, p);
            verificar("caja de '" + s + "' es " + (caja == null ? "ninguna" : caja.getSimpleName()),
                    caja == null ? c.isEmpty() : (c.isPresent() && c.get().getClass() == caja));
        }
    }

    /** La letra del cerrojo y los movimientos de la frágil vienen del símbolo. */
    static void losParametrosSalenDelSimbolo() throws Exception {
        RegistroDeCreadores r = RegistroDeCreadores.porDefecto();
        Posicion p = new Posicion(2, 3);

        verificar("el cerrojo 'c' conserva su letra", ((Lock) r.crearTerreno('c')).getLetter() == 'c');
        verificar("la 'K' no es un muro", r.crearTerreno('K').getClass() == TerrenoNormal.class);
        verificar("la caja L abre el cerrojo b", ((CajaLlave) r.crearCaja('L', p).orElseThrow()).getLetra() == 'b');
        verificar("caja frágil '7' tiene 7 movimientos",
                ((CajaFragil) r.crearCaja('7', p).orElseThrow()).getMovimientosRestantes() == 7);
        verificar("la caja nace en la posición pedida",
                r.crearCaja('$', p).orElseThrow().getPosicion().equals(p));
    }

    /** Los terrenos con estado no pueden compartirse entre celdas. */
    static void cadaLlamadaDevuelveUnaInstanciaNueva() throws Exception {
        RegistroDeCreadores r = RegistroDeCreadores.porDefecto();
        Door uno = (Door) r.crearTerreno('A');
        Door dos = (Door) r.crearTerreno('A');
        uno.onLockOpened();
        verificar("cada celda recibe su propio muro", uno != dos && !dos.isOpen());
    }

    /** El mensaje debe permitir ubicar el error en el archivo (filas y columnas desde 1). */
    static void simboloDesconocidoIndicaFilaYColumna() {
        String mensaje = cargarYObtenerError(RegistroDeCreadores.porDefecto(),
                List.of("#####", "#@$?#", "#####"));
        verificar("error indica fila 2, columna 4 (mensaje: " + mensaje + ")",
                mensaje != null && mensaje.contains("fila 2, columna 4") && mensaje.contains("'?'"));
    }

    /** Dos creadores para el mismo símbolo es una configuración incoherente: debe fallar, no elegir uno. */
    static void dosCreadoresParaElMismoSimboloSeDetectan() throws Exception {
        RegistroDeCreadores r = new RegistroDeCreadores()
                .registrarTerreno(new CreadorMuro())
                .registrarTerreno(new CreadorMuro());
        try {
            r.crearTerreno('#');
            verificar("la ambigüedad se detecta", false);
        } catch (IllegalStateException e) {
            verificar("la ambigüedad se detecta (" + e.getMessage() + ")",
                    e.getMessage().contains("CreadorMuro"));
        }
    }

    /** El registro es configurable: uno mínimo solo entiende lo que se le registró. */
    static void unRegistroMinimoNoConoceLosOtrosTipos() {
        RegistroDeCreadores minimo = new RegistroDeCreadores()
                .registrarTerreno(new CreadorMuro())
                .registrarTerreno(new CreadorTerrenoNormal())
                .registrarTerreno(new CreadorTerrenoDestino())
                .registrarCaja(new CreadorCajaNormal());

        String mensaje = cargarYObtenerError(minimo, List.of("#####", "#@$~#", "#####"));
        verificar("registro mínimo rechaza '~' (resbaladizo)", mensaje != null && mensaje.contains("'~'"));
        verificar("registro mínimo acepta un nivel simple",
                cargarYObtenerError(minimo, List.of("#####", "#@$.#", "#####")) == null);
    }

    /** La demostración principal: un tipo nuevo, cero cambios en el cargador. */
    static void unTerrenoNuevoSeAgregaSinTocarElCargador() throws Exception {
        List<String> nivel = List.of("######", "#@$.^#", "######");

        // Con el registro estándar, '^' no existe todavía.
        verificar("sin registrar, '^' es un símbolo desconocido",
                cargarYObtenerError(RegistroDeCreadores.porDefecto(), nivel) != null);

        // Registrándolo, el MISMO cargador lo entiende.
        RegistroDeCreadores ampliado = RegistroDeCreadores.porDefecto().registrarTerreno(new CreadorTrampa());
        Tablero t = new CargarNivelPorTxt(Path.of("."), ampliado).cargarDesdeLineas(nivel);
        verificar("registrado, '^' produce el terreno nuevo", t.getTerreno(1, 4).getClass() == Trampa.class);
        verificar("y el resto del nivel sigue igual", t.getTerreno(1, 3).esDestino());
    }

    static void unaCajaNuevaSeAgregaSinTocarLosTerrenos() throws Exception {
        List<String> nivel = List.of("#####", "#@%.#", "#####");
        verificar("sin registrar, '%' es un símbolo desconocido",
                cargarYObtenerError(RegistroDeCreadores.porDefecto(), nivel) != null);
        RegistroDeCreadores ampliado = RegistroDeCreadores.porDefecto().registrarCaja(new CreadorCajaNueva());
        verificar("registrada, '%' carga sobre piso", cargarYObtenerError(ampliado, nivel) == null);
    }

    static void losPoderesSeCreanConSusCreadores() {
        RegistroDeCreadores r = RegistroDeCreadores.porDefecto();
        Posicion p = new Posicion(1, 2);
        verificar("P01: H crea un martillo en su posición", r.crearPowerUp('H', p)
                .map(poder -> poder.tipo() == TipoPowerUp.MARTILLO && poder.posicion().equals(p)).orElse(false));
        verificar("P01: F crea una superfuerza", r.crearPowerUp('F', p)
                .map(poder -> poder.tipo() == TipoPowerUp.SUPERFUERZA).orElse(false));
        verificar("P17: una caja no crea power-up", r.crearPowerUp('$', p).isEmpty());
        RegistroDeCreadores minimo = new RegistroDeCreadores()
                .registrarTerreno(new CreadorMuro())
                .registrarTerreno(new CreadorTerrenoNormal())
                .registrarTerreno(new CreadorTerrenoDestino())
                .registrarCaja(new CreadorCajaNormal());
        String mensaje = cargarYObtenerError(minimo, List.of("######", "#@H$.#", "######"));
        verificar("P15: sin su creador, H es un símbolo desconocido", mensaje != null && mensaje.contains("'H'"));
        RegistroDeCreadores doble = new RegistroDeCreadores()
                .registrarPowerUp(new CreadorMartillo())
                .registrarPowerUp(new CreadorMartillo());
        try {
            doble.crearPowerUp('H', p);
            verificar("P16: dos creadores para H se detectan", false);
        } catch (IllegalStateException e) {
            verificar("P16: dos creadores para H se detectan", true);
        }
    }

    static void losNivelesInvalidosSeRechazan() {
        RegistroDeCreadores r = RegistroDeCreadores.porDefecto();
        verificar("menos cajas que destinos",
                cargarYObtenerError(r, List.of("#####", "#@$.#", "#  .#", "#####")) != null);
        verificar("una caja de repuesto se acepta",
                cargarYObtenerError(r, List.of("######", "#@$$.#", "#3  .#", "######")) == null);
        verificar("muro sin cerrojo",
                cargarYObtenerError(r, List.of("#####", "#@$.#", "# A #", "#####")) != null);
        verificar("cerrojo sin muro",
                cargarYObtenerError(r, List.of("#####", "#@$.#", "#Ka #", "#####")) != null);
        verificar("cerrojo sin caja llave",
                cargarYObtenerError(r, List.of("#####", "#@$.#", "#aA #", "#####")) != null);
        verificar("caja llave sin cerrojo",
                cargarYObtenerError(r, List.of("#####", "#@$.#", "# K #", "#####")) != null);
        verificar("nivel sin paredes", cargarYObtenerError(r, List.of("@$.")) != null);
        verificar("destino encerrado",
                cargarYObtenerError(r, List.of("#######", "#@$ # #", "#   #.#", "#.$ # #", "#######")) != null);
    }

    /** Prueba de integración con los archivos reales (requiere ejecutar desde la raíz del proyecto). */
    static void losNivelesDeEjemploSeCargan() throws Exception {
        CargarNivelPorTxt cargador = new CargarNivelPorTxt(Path.of("levels"));
        for (int n = 1; n <= 6; n++) {
            Tablero t = cargador.cargarNivel(n);
            verificar("nivel" + n + ".txt carga (" + t.getFilas() + "x" + t.getColumnas() + ", "
                    + t.getCajas().size() + " cajas)", !t.getCajas().isEmpty());
        }
        Tablero nivel3 = cargador.cargarNivel(3);
        verificar("nivel3 tiene su cerrojo", contarTerrenos(nivel3, Lock.class) == 1);
        verificar("nivel3 tiene su muro", contarTerrenos(nivel3, Door.class) == 1);
        verificar("nivel2 tiene terreno resbaladizo", contarTerrenos(cargador.cargarNivel(2), TerrenoResbaladizo.class) > 0);
    }

    // ---------------------------------------------------------------- utilidades

    private static int contarTerrenos(Tablero tablero, Class<? extends Terreno> tipo) {
        int total = 0;
        for (int f = 0; f < tablero.getFilas(); f++) {
            for (int c = 0; c < tablero.getColumnas(); c++) {
                if (tablero.getTerreno(f, c).getClass() == tipo) {
                    total++;
                }
            }
        }
        return total;
    }

    /** @return el mensaje de error de la carga, o {@code null} si el nivel cargó bien */
    private static String cargarYObtenerError(RegistroDeCreadores registro, List<String> lineas) {
        try {
            new CargarNivelPorTxt(Path.of("."), registro).cargarDesdeLineas(lineas);
            return null;
        } catch (CargaDeNivelException e) {
            return e.getMessage();
        }
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            pasaron++;
            System.out.println("  OK   " + descripcion);
        } else {
            fallaron++;
            System.out.println("  FALLA " + descripcion);
        }
    }
}
