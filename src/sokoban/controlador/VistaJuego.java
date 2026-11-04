package sokoban.controlador;

import sokoban.modelo.Tablero;

/**
 * Lo que el {@link Controlador} necesita de una vista. El controlador depende de esta interfaz y no de
 * Swing, de modo que se puede cambiar la vista (o probar el controlador con una vista falsa).
 */
public interface VistaJuego {

    /** Se cargó un nivel: mostrarlo. */
    void mostrarTablero(Tablero tablero);

    /** No se pudo cargar el nivel pedido; el nivel actual no cambió. */
    void mostrarError(int numeroDeNivel, String motivo);

    /** Se actualizó el nivel actual o cambió la posibilidad de retroceder. */
    void actualizarNavegacion(int nivelActual, boolean hayNivelAnterior);
}
