package sokoban.model;

import java.util.List;

import sokoban.model.box.Caja;

public final class BoardMemento {

    private final Posicion playerPosition;
    private final List<Caja> boxes;
    private final boolean[][] openTerrains;

    BoardMemento(Posicion playerPosition, List<Caja> boxes, boolean[][] openTerrains) {
        this.playerPosition = playerPosition;
        this.boxes = List.copyOf(boxes);
        this.openTerrains = openTerrains;
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
}
