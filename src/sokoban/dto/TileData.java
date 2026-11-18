package sokoban.dto;

public record TileData(TileType type, char label, boolean open) {

    private static final char NO_LABEL = ' ';

    public static TileData of(TileType type) {
        return new TileData(type, NO_LABEL, false);
    }
}
