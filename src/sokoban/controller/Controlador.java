package sokoban.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sokoban.audio.MusicPlayer;
import sokoban.dto.LevelOption;
import sokoban.level.CargaDeNivelException;
import sokoban.level.CargadorDeNivel;
import sokoban.model.Direction;
import sokoban.model.Game;
import sokoban.model.LevelProgress;

public class Controlador {

    private static final int PRIMER_NIVEL = 1;

    private final CargadorDeNivel cargador;
    private final Game game;
    private final MusicPlayer musica;
    private final LevelProgress progreso = new LevelProgress();
    private VistaJuego vista;
    private int nivelActual = PRIMER_NIVEL;

    public Controlador(CargadorDeNivel cargador, Game game, MusicPlayer musica) {
        this.cargador = Objects.requireNonNull(cargador, "cargador");
        this.game = Objects.requireNonNull(game, "game");
        this.musica = Objects.requireNonNull(musica, "musica");
    }

    public void setVista(VistaJuego vista) {
        this.vista = Objects.requireNonNull(vista, "vista");
    }

    public void iniciar() {
        if (vista == null) {
            throw new IllegalStateException("Falta registrar la vista (setVista) antes de usar el controlador");
        }
        abrirMenu();
    }

    public void abrirMenu() {
        musica.play();
        vista.mostrarMenu();
    }

    public void abrirSelector() {
        vista.mostrarSelector(opcionesDeNivel());
    }

    public void abrirCreditos() {
        vista.mostrarCreditos();
    }

    public void jugar() {
        int siguiente = progreso.nextLevel();
        cargarNivel(cargador.existeNivel(siguiente) ? siguiente : PRIMER_NIVEL);
    }

    public void seleccionarNivel(int numeroDeNivel) {
        if (progreso.isUnlocked(numeroDeNivel)) {
            cargarNivel(numeroDeNivel);
        }
    }

    public void moverArriba() {
        mover(Direction.UP);
    }

    public void moverAbajo() {
        mover(Direction.DOWN);
    }

    public void moverIzquierda() {
        mover(Direction.LEFT);
    }

    public void moverDerecha() {
        mover(Direction.RIGHT);
    }

    public void romperMuroArriba() {
        ejecutar(() -> game.romperMuro(Direction.UP));
    }

    public void romperMuroAbajo() {
        ejecutar(() -> game.romperMuro(Direction.DOWN));
    }

    public void romperMuroIzquierda() {
        ejecutar(() -> game.romperMuro(Direction.LEFT));
    }

    public void romperMuroDerecha() {
        ejecutar(() -> game.romperMuro(Direction.RIGHT));
    }

    public void jalarArriba() {
        ejecutar(() -> game.jalarCaja(Direction.UP));
    }

    public void jalarAbajo() {
        ejecutar(() -> game.jalarCaja(Direction.DOWN));
    }

    public void jalarIzquierda() {
        ejecutar(() -> game.jalarCaja(Direction.LEFT));
    }

    public void jalarDerecha() {
        ejecutar(() -> game.jalarCaja(Direction.RIGHT));
    }

    public void deshacer() {
        game.undo();
    }

    public void recargarNivel() {
        cargarNivel(nivelActual);
    }

    public int getNivelActual() {
        return nivelActual;
    }

    private List<LevelOption> opcionesDeNivel() {
        List<LevelOption> opciones = new ArrayList<>();
        for (int nivel = PRIMER_NIVEL; cargador.existeNivel(nivel); nivel++) {
            opciones.add(new LevelOption(nivel, progreso.isUnlocked(nivel), progreso.isCompleted(nivel)));
        }
        return opciones;
    }

    private void mover(Direction direccion) {
        ejecutar(() -> game.move(direccion));
    }

    private void ejecutar(Runnable accion) {
        accion.run();
        if (game.isLevelCompleted()) {
            terminarNivel();
        }
    }

    private void terminarNivel() {
        progreso.complete(nivelActual);
        vista.mostrarNivelCompletado(game.snapshot());
        if (cargador.existeNivel(nivelActual + 1)) {
            cargarNivel(nivelActual + 1);
        } else {
            vista.mostrarJuegoTerminado();
            abrirMenu();
        }
    }

    private void cargarNivel(int numeroDeNivel) {
        try {
            game.startLevel(cargador.cargarNivel(numeroDeNivel), numeroDeNivel);
            nivelActual = numeroDeNivel;
            musica.stop();
            vista.mostrarJuego();
        } catch (CargaDeNivelException e) {
            vista.mostrarError(numeroDeNivel, e.getMessage());
        }
    }
}
