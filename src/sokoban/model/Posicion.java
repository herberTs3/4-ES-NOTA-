package sokoban.model;

import sokoban.dto.CellPosition;

public record Posicion(int fila, int columna) {

    public Posicion next(Direction direction) {
        return new Posicion(fila + direction.deltaRow(), columna + direction.deltaColumn());
    }

    public CellPosition toData() {
        return new CellPosition(fila, columna);
    }
}
