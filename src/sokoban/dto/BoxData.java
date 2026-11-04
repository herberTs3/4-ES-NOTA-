package sokoban.dto;

public record BoxData(BoxType type, CellPosition position, int resistance, boolean inPlace) {
}
