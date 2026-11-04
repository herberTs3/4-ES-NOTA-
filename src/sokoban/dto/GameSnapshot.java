package sokoban.dto;

public record GameSnapshot(int level, int moves, int pushes, int undoUses, int remainingUndos, boolean canUndo,
                           GameStatus status, BoardSnapshot board) {
}
