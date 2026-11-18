package sokoban.model.puntaje;

/** Datos del tablero original: se capturan una sola vez al iniciar el nivel. */
public record CaracteristicasNivel(int celdasJugables, int cajas, int puertas, int hielo, int powerUps) {
}
