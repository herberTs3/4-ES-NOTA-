package sokoban.model;

import java.util.List;
import java.util.Set;

import sokoban.dto.EstadoPowerUp;
import sokoban.model.box.Caja;

public final class BoardMemento {

    private final Posicion playerPosition;
    private final List<Caja> boxes;
    private final boolean[][] openTerrains;
    private final int moves;
    private final int pushes;
    private final Set<Posicion> brokenWalls;
    private final List<PowerUpEnMapa> powerUps;
    private final List<EstadoPowerUp> activePowerUps;

    BoardMemento(Posicion playerPosition, List<Caja> boxes, boolean[][] openTerrains, int moves, int pushes,
                 Set<Posicion> brokenWalls, List<PowerUpEnMapa> powerUps, List<EstadoPowerUp> activePowerUps) {
        this.playerPosition = playerPosition;
        this.boxes = List.copyOf(boxes);
        this.openTerrains = openTerrains;
        this.moves = moves;
        this.pushes = pushes;
        this.brokenWalls = Set.copyOf(brokenWalls);
        this.powerUps = List.copyOf(powerUps);
        this.activePowerUps = List.copyOf(activePowerUps);
    }

    Posicion playerPosition() {
        return playerPosition;
    }

    List<Caja> boxes() {
        return boxes;
    }

    boolean isOpen(int row, int column) {
        return openTerrains[row][column];
    }

    int moves() {
        return moves;
    }

    int pushes() {
        return pushes;
    }

    Set<Posicion> brokenWalls() {
        return brokenWalls;
    }

    List<PowerUpEnMapa> powerUps() {
        return powerUps;
    }

    List<EstadoPowerUp> activePowerUps() {
        return activePowerUps;
    }
}
