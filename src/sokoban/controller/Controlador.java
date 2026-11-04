package sokoban.controller;

import java.util.Objects;

import sokoban.level.CargaDeNivelException;
import sokoban.level.CargadorDeNivel;
import sokoban.model.Direction;
import sokoban.model.Game;

public class Controlador {

    private static final int PRIMER_NIVEL = 1;

    private final CargadorDeNivel cargador;
    private final Game game;
    private VistaJuego vista;
    private int nivelActual;

    public Controlador(CargadorDeNivel cargador, Game game, int nivelInicial) {
        this.cargador = Objects.requireNonNull(cargador, "cargador");
        this.game = Objects.requireNonNull(game, "game");
        this.nivelActual = nivelInicial;
    }

    public void setVista(VistaJuego vista) {
        this.vista = Objects.requireNonNull(vista, "vista");
    }

    public void iniciar() {
        cargarNivel(nivelActual);
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

    public void siguienteNivel() {
        if (hayNivelSiguiente()) {
            cargarNivel(nivelActual + 1);
        } else {
            vista.mostrarJuegoTerminado();
        }
    }

    public void anteriorNivel() {
        if (hayNivelAnterior()) {
            cargarNivel(nivelActual - 1);
        }
    }

    public void recargarNivel() {
        cargarNivel(nivelActual);
    }

    public int getNivelActual() {
        return nivelActual;
    }

    public boolean hayNivelAnterior() {
        return nivelActual > PRIMER_NIVEL;
    }

    public boolean hayNivelSiguiente() {
        return cargador.existeNivel(nivelActual + 1);
    }

    private void mover(Direction direccion) {
        game.move(direccion);
        if (game.isLevelCompleted()) {
            vista.mostrarNivelCompletado(game.snapshot());
            siguienteNivel();
        }
    }

    private void cargarNivel(int numeroDeNivel) {
        if (vista == null) {
            throw new IllegalStateException("Falta registrar la vista (setVista) antes de usar el controlador");
        }
        try {
            game.startLevel(cargador.cargarNivel(numeroDeNivel), numeroDeNivel);
            nivelActual = numeroDeNivel;
        } catch (CargaDeNivelException e) {
            vista.mostrarError(numeroDeNivel, e.getMessage());
        }
        vista.actualizarNavegacion(nivelActual, hayNivelAnterior(), hayNivelSiguiente());
    }
}
