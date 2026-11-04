package sokoban.controlador;

import sokoban.carga.CargaDeNivelException;
import sokoban.carga.CargadorDeNivel;
import sokoban.carga.CargarNivelPorTxt;
import sokoban.modelo.Tablero;
import sokoban.modelo.caja.VisitanteCaja;
import sokoban.modelo.terreno.VisitanteTerreno;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Pruebas sin dependencias externas del Controlador, con una vista falsa (sin Swing).
 * Ejecutar desde la raíz del proyecto: {@code java -cp out sokoban.controlador.PruebaControlador}
 */
public class PruebaControlador {

    private static int pasaron = 0;
    private static int fallaron = 0;

    /** Vista falsa: registra lo que el controlador le pide. */
    static class VistaFalsa implements VistaJuego {
        final List<String> eventos = new ArrayList<>();
        Tablero ultimoTablero;
        int nivelMostrado;
        boolean hayAnterior;

        @Override
        public void mostrarTablero(Tablero tablero) {
            ultimoTablero = tablero;
            eventos.add("tablero");
        }

        @Override
        public void mostrarError(int numeroDeNivel, String motivo) {
            eventos.add("error:" + numeroDeNivel);
        }

        @Override
        public void actualizarNavegacion(int nivelActual, boolean hayNivelAnterior) {
            nivelMostrado = nivelActual;
            hayAnterior = hayNivelAnterior;
            eventos.add("nav:" + nivelActual);
        }
    }

    public static void main(String[] args) throws Exception {
        CargadorDeNivel real = new CargarNivelPorTxt(Path.of("niveles"));
        // Cargador que solo conoce los niveles 1 a 3 (el real lanza excepción para los demás).
        CargadorDeNivel cargador = real;

        System.out.println("Controlador:");
        iniciaYNotificaALaVista(cargador);
        navegaEntreNiveles(cargador);
        unNivelInexistenteNoCambiaElNivelActual(cargador);
        sinVistaFallaConMensajeClaro(cargador);
        elDobleDespachoDistingueLosTipos(cargador);

        System.out.printf("%n%d pasaron, %d fallaron%n", pasaron, fallaron);
        System.exit(fallaron == 0 ? 0 : 1);
    }

    static void iniciaYNotificaALaVista(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = new Controlador(cargador, 1);
        c.setVista(vista);
        verificar("antes de iniciar la vista no recibió nada", vista.eventos.isEmpty());
        c.iniciar();
        verificar("iniciar muestra el tablero y luego actualiza la navegación",
                vista.eventos.equals(List.of("tablero", "nav:1")));
        verificar("el tablero entregado es el del nivel 1", vista.ultimoTablero.contarDestinos() == 2);
        verificar("en el nivel 1 no hay nivel anterior", !vista.hayAnterior && !c.hayNivelAnterior());
    }

    static void navegaEntreNiveles(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = new Controlador(cargador, 1);
        c.setVista(vista);
        c.iniciar();
        c.siguienteNivel();
        verificar("siguiente pasa al nivel 2", c.getNivelActual() == 2 && vista.nivelMostrado == 2);
        verificar("en el nivel 2 hay anterior", vista.hayAnterior);
        c.siguienteNivel();
        c.anteriorNivel();
        verificar("anterior vuelve al nivel 2", c.getNivelActual() == 2 && vista.nivelMostrado == 2);
        Tablero antes = vista.ultimoTablero;
        c.recargarNivel();
        verificar("recargar entrega un tablero nuevo del mismo nivel",
                c.getNivelActual() == 2 && vista.ultimoTablero != antes);
    }

    static void unNivelInexistenteNoCambiaElNivelActual(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = new Controlador(cargador, 3);
        c.setVista(vista);
        c.iniciar();
        vista.eventos.clear();
        c.siguienteNivel(); // no existe nivel4.txt
        verificar("el nivel inexistente informa el error a la vista",
                vista.eventos.equals(List.of("error:4", "nav:3")));
        verificar("el nivel actual no cambia tras el error", c.getNivelActual() == 3);
    }

    static void sinVistaFallaConMensajeClaro(CargadorDeNivel cargador) {
        try {
            new Controlador(cargador, 1).iniciar();
            verificar("sin vista registrada se lanza IllegalStateException", false);
        } catch (IllegalStateException e) {
            verificar("sin vista registrada se lanza IllegalStateException", true);
        }
    }

    /** Cada terreno y cada caja llama al método de visita que le corresponde (reemplaza a instanceof). */
    static void elDobleDespachoDistingueLosTipos(CargadorDeNivel cargador) throws CargaDeNivelException {
        Tablero t = cargador.cargarNivel(3);
        List<String> terrenos = new ArrayList<>();
        VisitanteTerreno vt = new VisitanteTerreno() {
            public void visitarMuro() { terrenos.add("muro"); }
            public void visitarTerrenoNormal() { terrenos.add("normal"); }
            public void visitarTerrenoDestino() { terrenos.add("destino"); }
            public void visitarTerrenoResbaladizo() { terrenos.add("hielo"); }
            public void visitarTerrenoCerrojo(int clave, boolean abierto) { terrenos.add("cerrojo" + clave + abierto); }
            public void visitarOtro() { terrenos.add("otro"); }
        };
        for (int f = 0; f < t.getFilas(); f++) {
            for (int c = 0; c < t.getColumnas(); c++) {
                t.getTerreno(f, c).aceptar(vt);
            }
        }
        verificar("el nivel 3 tiene un cerrojo A (clave 1) cerrado", terrenos.contains("cerrojo1false"));
        verificar("el nivel 3 tiene 2 destinos", terrenos.stream().filter("destino"::equals).count() == 2);
        verificar("ningún terreno cae en 'otro'", !terrenos.contains("otro"));

        List<String> cajas = new ArrayList<>();
        VisitanteCaja vc = new VisitanteCaja() {
            public void visitarCajaNormal() { cajas.add("normal"); }
            public void visitarCajaLlave(int clave) { cajas.add("llave" + clave); }
            public void visitarCajaFragil(int movimientos) { cajas.add("fragil" + movimientos); }
        };
        t.getCajas().forEach(caja -> caja.aceptar(vc));
        verificar("cajas del nivel 3: llave 1, frágil 3 y normal",
                cajas.containsAll(List.of("llave1", "fragil3", "normal")) && cajas.size() == 3);
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
