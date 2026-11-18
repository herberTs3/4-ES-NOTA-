package sokoban.model.puntaje;

/** Estadisticas de una partida terminada, independientes de la vista. */
public record EstadisticasNivel(int nivel, int movimientos, int empujes, int usosUndo) {
}
