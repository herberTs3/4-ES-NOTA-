package sokoban.controller;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import sokoban.audio.MusicPlayer;
import sokoban.dto.BoxType;
import sokoban.dto.GameSnapshot;
import sokoban.dto.LevelOption;
import sokoban.dto.TileType;
import sokoban.level.CargaDeNivelException;
import sokoban.level.CargadorDeNivel;
import sokoban.level.CargarNivelPorTxt;
import sokoban.model.Game;
import sokoban.model.Tablero;
import sokoban.model.event.GameListener;

public class PruebaControlador {

    private static final String SOLUCION_NIVEL_1 = "DLLUDLLU";
    private static final String SOLUCION_NIVEL_2 = "URDDRR";
    private static final String SOLUCION_NIVEL_3 = "ULURRLDRRRRDRULUURRDDRDL";

    private static int pasaron = 0;
    private static int fallaron = 0;

    static class VistaFalsa implements VistaJuego, GameListener {
        final List<String> eventos = new ArrayList<>();
        List<LevelOption> opciones = List.of();
        GameSnapshot ultimaFoto;

        @Override
        public void onGameChanged(GameSnapshot snapshot) {
            ultimaFoto = snapshot;
            eventos.add("foto");
        }

        @Override
        public void mostrarMenu() {
            eventos.add("menu");
        }

        @Override
        public void mostrarSelector(List<LevelOption> nuevasOpciones) {
            opciones = nuevasOpciones;
            eventos.add("selector");
        }

        @Override
        public void mostrarCreditos() {
            eventos.add("creditos");
        }

        @Override
        public void mostrarJuego() {
            eventos.add("juego");
        }

        @Override
        public void mostrarError(int numeroDeNivel, String motivo) {
            eventos.add("error:" + numeroDeNivel);
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

    static class MusicaFalsa implements MusicPlayer {
        boolean sonando;

        @Override
        public void play() {
            sonando = true;
        }

        @Override
        public void stop() {
            sonando = false;
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

    public static void main(String[] args) {
        CargadorDeNivel cargador = new CargarNivelPorTxt(Path.of("levels"));

        System.out.println("Controlador:");
        iniciaEnElMenu(cargador);
        laMusicaSuenaSoloEnElMenu(cargador);
        navegaEntrePantallas(cargador);
        losNivelesArrancanBloqueados(cargador);
        ganarDesbloqueaYAvanza(cargador);
        unNivelCompletadoSePuedeRepetir(cargador);
        unNivelInvalidoInformaElError();
        sinNivelCargadoLasFlechasNoFallan();
        sinVistaFallaConMensajeClaro(cargador);
        laFotoDescribeLosTiposDelNivel(cargador);

        System.out.printf("%n%d pasaron, %d fallaron%n", pasaron, fallaron);
        System.exit(fallaron == 0 ? 0 : 1);
    }

    private static Controlador crear(CargadorDeNivel cargador, VistaFalsa vista) {
        return crear(cargador, vista, new MusicaFalsa());
    }

    private static Controlador crear(CargadorDeNivel cargador, VistaFalsa vista, MusicPlayer musica) {
        Game game = new Game();
        game.addListener(vista);
        Controlador controlador = new Controlador(cargador, game, musica);
        controlador.setVista(vista);
        return controlador;
    }

    static void iniciaEnElMenu(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(cargador, vista);
        verificar("antes de iniciar la vista no recibió nada", vista.eventos.isEmpty());
        c.iniciar();
        verificar("iniciar muestra el menú", vista.eventos.equals(List.of("menu")));
    }

    static void laMusicaSuenaSoloEnElMenu(CargadorDeNivel cargador) {
        MusicaFalsa musica = new MusicaFalsa();
        Controlador c = crear(cargador, new VistaFalsa(), musica);
        c.iniciar();
        verificar("la música suena en el menú", musica.sonando);
        c.jugar();
        verificar("la música se detiene al jugar", !musica.sonando);
        c.abrirMenu();
        verificar("la música vuelve al menú", musica.sonando);
    }

    static void navegaEntrePantallas(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(cargador, vista);
        c.abrirCreditos();
        verificar("abre los créditos", vista.eventos.contains("creditos"));
        c.abrirSelector();
        verificar("el selector lista los 6 niveles", vista.opciones.size() == 6);
        c.jugar();
        verificar("jugar arranca en el nivel 1", vista.ultimaFoto.level() == 1 && vista.eventos.contains("juego"));
        c.moverArriba();
        c.deshacer();
        verificar("deshacer llega al modelo y notifica la foto", vista.ultimaFoto.undoUses() == 1);
        c.recargarNivel();
        verificar("reiniciar pone los contadores en cero", c.getNivelActual() == 1 && vista.ultimaFoto.moves() == 0);
        c.abrirMenu();
        verificar("vuelve al menú", vista.eventos.get(vista.eventos.size() - 1).equals("menu"));
    }

    static void losNivelesArrancanBloqueados(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(cargador, vista);
        c.abrirSelector();
        verificar("solo el nivel 1 arranca desbloqueado", vista.opciones.get(0).unlocked()
                && vista.opciones.stream().skip(1).noneMatch(LevelOption::unlocked));
        c.seleccionarNivel(3);
        verificar("un nivel bloqueado no se abre", vista.ultimaFoto == null && !vista.eventos.contains("juego"));
    }

    static void ganarDesbloqueaYAvanza(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(cargador, vista);
        c.jugar();
        jugar(c, SOLUCION_NIVEL_1);
        verificar("ganar muestra el resumen", vista.eventos.contains("completado:1"));
        verificar("ganar pasa al nivel 2", c.getNivelActual() == 2 && vista.ultimaFoto.level() == 2);
        c.abrirSelector();
        verificar("el nivel 1 queda completado", vista.opciones.get(0).completed());
        verificar("el nivel 2 queda desbloqueado", vista.opciones.get(1).unlocked());
        verificar("el nivel 3 sigue bloqueado", !vista.opciones.get(2).unlocked());
        c.seleccionarNivel(3);
        verificar("no se puede saltear el nivel 2", c.getNivelActual() == 2);
    }

    static void unNivelCompletadoSePuedeRepetir(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(cargador, vista);
        c.jugar();
        jugar(c, SOLUCION_NIVEL_1);
        jugar(c, SOLUCION_NIVEL_2);
        verificar("avanza hasta el nivel 3", c.getNivelActual() == 3);
        c.seleccionarNivel(1);
        verificar("un nivel completado se puede repetir", c.getNivelActual() == 1);
        jugar(c, SOLUCION_NIVEL_1);
        c.abrirSelector();
        verificar("repetir no pierde el progreso", vista.opciones.get(2).unlocked());
        c.jugar();
        verificar("jugar sigue en el nivel pendiente", c.getNivelActual() == 3);
        jugar(c, SOLUCION_NIVEL_3);
        verificar("ganar el nivel 3 abre el nivel 4", c.getNivelActual() == 4);
    }

    static void unNivelInvalidoInformaElError() {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(new CargadorRoto(), vista);
        c.jugar();
        verificar("el nivel inválido informa el error a la vista", vista.eventos.equals(List.of("error:1")));
    }

    static void sinNivelCargadoLasFlechasNoFallan() {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(new CargadorRoto(), vista);
        c.jugar();
        c.moverDerecha();
        verificar("sin nivel cargado las flechas se ignoran", !vista.eventos.contains("foto"));
    }

    static void sinVistaFallaConMensajeClaro(CargadorDeNivel cargador) {
        try {
            new Controlador(cargador, new Game(), new MusicaFalsa()).iniciar();
            verificar("sin vista registrada se lanza IllegalStateException", false);
        } catch (IllegalStateException e) {
            verificar("sin vista registrada se lanza IllegalStateException", true);
        }
    }

    static void laFotoDescribeLosTiposDelNivel(CargadorDeNivel cargador) {
        VistaFalsa vista = new VistaFalsa();
        Controlador c = crear(cargador, vista);
        c.jugar();
        jugar(c, SOLUCION_NIVEL_1);
        jugar(c, SOLUCION_NIVEL_2);
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
