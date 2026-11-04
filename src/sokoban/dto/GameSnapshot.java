package sokoban.dto;

public record GameSnapshot(int level, int moves, int pushes, GameStatus status, BoardSnapshot board) {
}
