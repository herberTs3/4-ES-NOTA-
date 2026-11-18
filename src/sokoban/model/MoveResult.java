package sokoban.model;

public enum MoveResult {
    BLOCKED, MOVED, PUSHED, PULLED, WALL_BROKEN;

    public boolean desplazaAlJugador() {
        return this != BLOCKED && this != WALL_BROKEN;
    }
}
