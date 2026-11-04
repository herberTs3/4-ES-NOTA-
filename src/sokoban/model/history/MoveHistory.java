package sokoban.model.history;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;

import sokoban.model.BoardMemento;

public class MoveHistory {

    public static final int MAX_HISTORY = 15;
    public static final int UNDO_STEP = 5;
    public static final int MAX_UNDO_USES = 3;

    private final Deque<BoardMemento> mementos = new ArrayDeque<>();
    private int consecutiveUses;
    private int totalUses;

    public void record(BoardMemento memento) {
        if (mementos.size() == MAX_HISTORY) {
            mementos.removeFirst();
        }
        mementos.addLast(Objects.requireNonNull(memento));
        consecutiveUses = 0;
    }

    public Optional<BoardMemento> undo() {
        if (!canUndo()) {
            return Optional.empty();
        }
        BoardMemento restored = null;
        for (int step = 0; step < UNDO_STEP && !mementos.isEmpty(); step++) {
            restored = mementos.removeLast();
        }
        consecutiveUses++;
        totalUses++;
        return Optional.of(restored);
    }

    public boolean canUndo() {
        return !mementos.isEmpty() && consecutiveUses < MAX_UNDO_USES;
    }

    public int remainingUses() {
        return MAX_UNDO_USES - consecutiveUses;
    }

    public int totalUses() {
        return totalUses;
    }

    public void clear() {
        mementos.clear();
        consecutiveUses = 0;
        totalUses = 0;
    }
}
