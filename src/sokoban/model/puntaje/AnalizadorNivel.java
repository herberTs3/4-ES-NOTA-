package sokoban.model.puntaje;

import java.util.List;
import java.util.Objects;

import sokoban.dto.BoardSnapshot;
import sokoban.dto.PowerUpData;
import sokoban.dto.TileType;

/** Analiza el estado inicial del nivel sin modificar el tablero. */
public class AnalizadorNivel {

    public CaracteristicasNivel analizar(BoardSnapshot tablero, List<PowerUpData> powerUps) {
        Objects.requireNonNull(tablero, "tablero");
        Objects.requireNonNull(powerUps, "powerUps");
        int celdasJugables = 0;
        int puertas = 0;
        int hielo = 0;

        for (int fila = 0; fila < tablero.rows(); fila++) {
            for (int columna = 0; columna < tablero.columns(); columna++) {
                TileType tipo = tablero.tileAt(fila, columna).type();
                // Incluye las puertas: pueden abrirse durante la partida.
                if (tipo != TileType.WALL) {
                    celdasJugables++;
                }
                if (tipo == TileType.DOOR) {
                    puertas++;
                } else if (tipo == TileType.ICE) {
                    hielo++;
                }
            }
        }
        return new CaracteristicasNivel(celdasJugables, tablero.boxes().size(), puertas, hielo, powerUps.size());
    }
}
