package sokoban.controller;

import sokoban.dto.GameSnapshot;

public interface VistaJuego {

    void mostrarError(int numeroDeNivel, String motivo);

    void actualizarNavegacion(int nivelActual, boolean hayNivelAnterior, boolean hayNivelSiguiente);

    void mostrarNivelCompletado(GameSnapshot resumen);

    void mostrarJuegoTerminado();
}
