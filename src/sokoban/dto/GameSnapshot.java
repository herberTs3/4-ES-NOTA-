package sokoban.dto;

import java.util.List;

public record GameSnapshot(int level, int moves, int pushes, int undoUses, int remainingUndos, boolean canUndo,
                           GameStatus status, BoardSnapshot board,
                           List<PowerUpData> powerUpsEnMapa, List<EstadoPowerUp> powerUpsActivos) {

    public GameSnapshot {
        powerUpsEnMapa = List.copyOf(powerUpsEnMapa);
        powerUpsActivos = List.copyOf(powerUpsActivos);
    }
}
