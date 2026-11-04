package sokoban.controller;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import sokoban.dto.BoxType;
import sokoban.dto.GameSnapshot;
import sokoban.dto.TileType;
import sokoban.level.CargaDeNivelException;
import sokoban.level.CargadorDeNivel;
import sokoban.level.CargarNivelPorTxt;
import sokoban.model.Game;
import sokoban.model.Tablero;
import sokoban.model.event.GameListener;

public class PruebaControlador {

    private static int pasaron = 0;
    private static int fallaron = 0;

    static class VistaFalsa implements VistaJuego, GameListener {
        final List<String> eventos = new ArrayList<>();
        GameSnapshot ultimaFoto;
        int nivelMostrado;
        boolean hayAnterior;
        boolean haySiguiente;

        @Override
        public void onGameChanged(GameSnapshot snapshot) {
            ultimaFoto = snapshot;
            eventos.add("foto");
        }

        @Override
        public void mostrarError(int numeroDeNivel, String motivo) {
            eventos.add("error:" + numeroDeNivel);
        }

        @Override
        public void actualizarNavegacion(int nivelActual, boolean hayNivelAnterior, boolean hayNivelSiguiente) {
            nivelMostrado = nivelActual;
            hayAnterior = hayNivelAnterior;
            haySiguiente = hayNivelSiguiente;
            eventos.add("nav:" + nivelActual);
        }

        @Override
        public void mostrarNivelCompletado(GameSnapshot resumen) {
            eventos.add("completado:" + resumen.level());
        }

        @Override
        public void mostrarJuegoTerminado() {
            eventos.add("fin");
        }
    }

    static class CargadorRoto implements CargadorDeNivel {
        @Override
        public Tablero cargarNivel(int numeroDeNivel) throws CargaDeNivelException {
            throw new CargaDeNivelException("archivo inválido");
        }

        @Override
        public boolean existeNivel(int numeroDeNivel) {
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        CargadorDeNivel cargador = new CargarNivelPorTxt(Path.of("levels"));

        System.out.println("Controlador:");
        iniciaYNotificaALaVista(cargador);
        navegaEntreNiveles(cargador);
        ganarAvanzaAlSiguienteNivel(cargador);
        ganarElUltimoNivelTerminaElJuego(cargador);
        unNivelInvalidoNoCambiaElNivelActual();
        sinNivelCargadoLasFlechasNoFallan();
        sinVistaFallaConMensajeClaro(cargador);
        laFotoDescribeLosTiposDelNivel(cargador);

        System.out.printf("%n%d pasaron, %d fallaron%n", pasaron, fallaron);
        System.exit(fallaron == 0 ? 0 : 1);
    }

    private static Controlador crear(CargadorDeNivel cargador, VistaFalsa vista, int nivel) {
        Game game = new Game();
        game.addListener(vista);
        Controlador controlador = new Controlador(cargador, game, nivel);
        controlador.setVista(vista);
        return controlador;
    }

    static void iniciaYNotificaALaVista(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(cargador, vista, 1);
        verificar("antes de iniciar la vista no recibió nada", vista.eventos.isEmpty());
        c.iniciar();
        verificar("iniciar notifica la foto y luego la navegación", vista.eventos.equals(List.of("foto", "nav:1")));
        verificar("la foto entregada es la del nivel 1", vista.ultimaFoto.board().countGoals() == 2);
        verificar("en el nivel 1 no hay anterior y sí siguiente", !vista.hayAnterior && vista.haySiguiente);
    }

    static void navegaEntreNiveles(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(cargador, vista, 1);
        c.iniciar();
        c.anteriorNivel();
        verificar("en el nivel 1 anterior no hace nada", c.getNivelActual() == 1);
        c.siguienteNivel();
        verificar("siguiente pasa al nivel 2", c.getNivelActual() == 2 && vista.nivelMostrado == 2);
        verificar("en el nivel 2 hay anterior", vista.hayAnterior);
        c.siguienteNivel();
        verificar("en el último nivel no hay siguiente", !vista.haySiguiente);
        c.anteriorNivel();
        verificar("anterior vuelve al nivel 2", c.getNivelActual() == 2 && vista.nivelMostrado == 2);
        c.moverArriba();
        verificar("mover cuenta en la foto", vista.ultimaFoto.moves() == 1);
        c.recargarNivel();
        verificar("reiniciar pone los contadores en cero", c.getNivelActual() == 2 && vista.ultimaFoto.moves() == 0);
        c.moverArriba();
        c.deshacer();
        verificar("deshacer llega al modelo y notifica la foto", vista.ultimaFoto.undoUses() == 1);
    }

    static void ganarAvanzaAlSiguienteNivel(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(cargador, vista, 1);
        c.iniciar();
        jugar(c, "DLLUDLLU");
        verificar("ganar muestra el resumen", vista.eventos.contains("completado:1"));
        verificar("ganar pasa al nivel 2", c.getNivelActual() == 2 && vista.ultimaFoto.level() == 2);
    }

    static void ganarElUltimoNivelTerminaElJuego(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(cargador, vista, 3);
        c.iniciar();
        jugar(c, "ULURRLDRRRRDRULUURRDDRDL");
        verificar("ganar el último nivel termina el juego", vista.eventos.contains("fin"));
        verificar("el nivel actual no cambia al terminar", c.getNivelActual() == 3);
    }

    static void unNivelInvalidoNoCambiaElNivelActual() {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(new CargadorRoto(), vista, 1);
        c.siguienteNivel();
        verificar("el nivel inválido informa el error a la vista", vista.eventos.get(0).equals("error:2"));
        verificar("el nivel actual no cambia tras el error", c.getNivelActual() == 1);
    }

    static void sinNivelCargadoLasFlechasNoFallan() {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(new CargadorRoto(), vista, 1);
        c.iniciar();
        c.moverDerecha();
        verificar("sin nivel cargado las flechas se ignoran", !vista.eventos.contains("foto"));
    }

    static void sinVistaFallaConMensajeClaro(CargadorDeNivel cargador) {
        try {
            new Controlador(cargador, new Game(), 1).iniciar();
            verificar("sin vista registrada se lanza IllegalStateException", false);
        } catch (IllegalStateException e) {
            verificar("sin vista registrada se lanza IllegalStateException", true);
        }
    }

    static void laFotoDescribeLosTiposDelNivel(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        crear(cargador, vista, 3).iniciar();
        List<TileType> tipos = vista.ultimaFoto.board().tiles().stream().flatMap(List::stream)
                .map(celda -> celda.type()).toList();
        verificar("el nivel 3 tiene cerrojo y muro", tipos.contains(TileType.LOCK) && tipos.contains(TileType.DOOR));
        verificar("el nivel 3 tiene 2 destinos", vista.ultimaFoto.board().countGoals() == 2);
        List<BoxType> cajas = vista.ultimaFoto.board().boxes().stream().map(caja -> caja.type()).toList();
        verificar("cajas del nivel 3: llave, frágil y normal", cajas.size() == 3
                && cajas.containsAll(List.of(BoxType.KEY, BoxType.FRAGILE, BoxType.NORMAL)));
    }

    private static void jugar(Controlador controlador, String jugadas) {
        for (char jugada : jugadas.toCharArray()) {
            switch (jugada) {
                case 'U' -> controlador.moverArriba();
                case 'D' -> controlador.moverAbajo();
                case 'L' -> controlador.moverIzquierda();
                default -> controlador.moverDerecha();
            }
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
