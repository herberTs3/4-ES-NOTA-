package sokoban.model.terrain;

import sokoban.dto.TileData;
import sokoban.dto.TileType;

public class Door extends Terreno implements LockListener {

    private final char letter;
    private boolean open;

    public Door(char letter) {
        this.letter = letter;
    }

    public boolean isOpen() {
        return open;
    }

    @Override
    public void onLockOpened() {
        open = true;
    }

    @Override
    public boolean esTransitable() {
        return open;
    }

    @Override
    public void suscribirA(Lock lock) {
        if (Character.toLowerCase(letter) == lock.getLetter()) {
            lock.addListener(this);
        }
    }

    @Override
    public TileData toData() {
        return new TileData(TileType.DOOR, letter, open);
    }
}
