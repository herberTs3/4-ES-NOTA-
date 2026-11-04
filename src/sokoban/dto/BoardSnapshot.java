package sokoban.dto;

import java.util.List;

public record BoardSnapshot(int rows, int columns, List<List<TileData>> tiles, List<BoxData> boxes,
                            CellPosition player) {

    public BoardSnapshot {
        tiles = tiles.stream().map(List::copyOf).toList();
        boxes = List.copyOf(boxes);
    }

    public TileData tileAt(int row, int column) {
        return tiles.get(row).get(column);
    }

    public int countGoals() {
        return (int) tiles.stream().flatMap(List::stream).filter(tile -> tile.type() == TileType.GOAL).count();
    }
}
