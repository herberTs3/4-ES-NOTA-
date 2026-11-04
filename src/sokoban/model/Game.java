package sokoban.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sokoban.dto.GameSnapshot;
import sokoban.dto.GameStatus;
import sokoban.model.event.GameListener;

public class Game {

    private final List<GameListener> listeners = new ArrayList<>();
    private Tablero board;
    private int level;
    private int moves;
    private int pushes;
    private GameStatus status = GameStatus.NO_LEVEL;

    public void addListener(GameListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public void startLevel(Tablero newBoard, int levelNumber) {
        board = Objects.requireNonNull(newBoard);
        level = levelNumber;
        moves = 0;
        pushes = 0;
        status = GameStatus.PLAYING;
        notifyListeners();
    }

    public void move(Direction direction) {
        if (status != GameStatus.PLAYING) {
            return;
        }
        MoveResult result = board.moverJugador(direction);
        if (result == MoveResult.BLOCKED) {
            return;
        }
        moves++;
        if (result == MoveResult.PUSHED) {
            pushes++;
        }
        status = nextStatus();
        notifyListeners();
    }

    public boolean isLevelCompleted() {
        return status == GameStatus.LEVEL_COMPLETED;
    }

    public GameSnapshot snapshot() {
        return new GameSnapshot(level, moves, pushes, status, board == null ? null : board.snapshot());
    }

    private GameStatus nextStatus() {
        if (board.estaResuelto()) {
            return GameStatus.LEVEL_COMPLETED;
        }
        return board.tieneCajasSuficientes() ? GameStatus.PLAYING : GameStatus.UNSOLVABLE;
    }

    private void notifyListeners() {
        GameSnapshot snapshot = snapshot();
        for (GameListener listener : listeners) {
            listener.onGameChanged(snapshot);
        }
    }
}
