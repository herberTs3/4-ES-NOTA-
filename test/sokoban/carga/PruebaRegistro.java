package sokoban.carga;

import sokoban.carga.fabrica.CreadorCajaNormal;
import sokoban.carga.fabrica.CreadorMuro;
import sokoban.carga.fabrica.CreadorTerreno;
import sokoban.carga.fabrica.CreadorTerrenoDestino;
import sokoban.carga.fabrica.CreadorTerrenoNormal;
import sokoban.carga.fabrica.RegistroDeCreadores;
import sokoban.modelo.Posicion;
import sokoban.modelo.Tablero;
import sokoban.modelo.caja.Caja;
import sokoban.modelo.caja.CajaFragil;
import sokoban.modelo.caja.CajaLlave;
import sokoban.modelo.caja.CajaNormal;
import sokoban.modelo.terreno.Muro;
import sokoban.modelo.terreno.Terreno;
import sokoban.modelo.terreno.TerrenoCerrojo;
import sokoban.modelo.terreno.TerrenoDestino;
import sokoban.modelo.terreno.TerrenoNormal;
import sokoban.modelo.terreno.TerrenoResbaladizo;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Pruebas sin dependencias externas (no hace falta JUnit) del paquete de fábricas.
 *
 * <p>Compilar y ejecutar desde la raíz del proyecto (ver README):</p>
 * <pre>
 *   javac -encoding UTF-8 -d out $(find src test -name "*.java")
 *   java -cp out sokoban.carga.PruebaRegistro
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
                {'A', TerrenoCerrojo.class, null},
                {'@', TerrenoNormal.class, null},
                {'+', TerrenoDestino.class, null},
                {'$', TerrenoNormal.class, CajaNormal.class},
                {'*', TerrenoDestino.class, CajaNormal.class},
                {'a', TerrenoNormal.class, CajaLlave.class},
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

    /** La clave del cerrojo y de la caja llave, y los movimientos de la frágil, vienen del símbolo. */
    static void losParametrosSalenDelSimbolo() throws Exception {
        RegistroDeCreadores r = RegistroDeCreadores.porDefecto();
        Posicion p = new Posicion(2, 3);

        verificar("cerrojo 'A' tiene clave 1", ((TerrenoCerrojo) r.crearTerreno('A')).getClave() == 1);
        verificar("cerrojo 'C' tiene clave 3", ((TerrenoCerrojo) r.crearTerreno('C')).getClave() == 3);
        verificar("cerrojo 'Z' tiene clave 26", ((TerrenoCerrojo) r.crearTerreno('Z')).getClave() == 26);
        verificar("caja llave 'c' tiene clave 3 (abre el cerrojo 'C')",
                ((CajaLlave) r.crearCaja('c', p).orElseThrow()).getClave() == 3);
        verificar("caja frágil '7' tiene 7 movimientos",
                ((CajaFragil) r.crearCaja('7', p).orElseThrow()).getMovimientosRestantes() == 7);
        verificar("la caja nace en la posición pedida",
                r.crearCaja('$', p).orElseThrow().getPosicion().equals(p));
    }

    /** Los terrenos con estado no pueden compartirse entre celdas. */
    static void cadaLlamadaDevuelveUnaInstanciaNueva() throws Exception {
        RegistroDeCreadores r = RegistroDeCreadores.porDefecto();
        TerrenoCerrojo uno = (TerrenoCerrojo) r.crearTerreno('A');
        TerrenoCerrojo dos = (TerrenoCerrojo) r.crearTerreno('A');
        uno.abrir();
        verificar("abrir un cerrojo no abre otro con la misma clave", uno != dos && !dos.estaAbierto());
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

    /** Prueba de integración con los archivos reales (requiere ejecutar desde la raíz del proyecto). */
    static void losNivelesDeEjemploSeCargan() throws Exception {
        CargarNivelPorTxt cargador = new CargarNivelPorTxt(Path.of("niveles"));
        for (int n = 1; n <= 3; n++) {
            Tablero t = cargador.cargarNivel(n);
            verificar("nivel" + n + ".txt carga (" + t.getFilas() + "x" + t.getColumnas() + ", "
                    + t.getCajas().size() + " cajas)", !t.getCajas().isEmpty());
        }
        Tablero nivel3 = cargador.cargarNivel(3);
        verificar("nivel3 tiene su cerrojo", contarTerrenos(nivel3, TerrenoCerrojo.class) == 1);
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
