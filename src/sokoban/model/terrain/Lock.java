package sokoban.model.terrain;

import java.util.ArrayList;
import java.util.List;

import sokoban.dto.TileData;
import sokoban.dto.TileType;
import sokoban.model.box.Caja;

public class Lock extends Terreno {

    private final List<LockListener> listeners = new ArrayList<>();
    private final char letter;
    private boolean open;

    public Lock(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void restoreOpen(boolean open) {
        this.open = open;
    }

    public void addListener(LockListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean esTransitable() {
        return true;
    }

    @Override
    public boolean esCerrojo() {
        return true;
    }

    @Override
    public void alEntrarCaja(Caja caja) {
        if (caja.abreCerrojos() && !open) {
            open = true;
            listeners.forEach(LockListener::onLockOpened);
        }
    }

    @Override
    public void vincular(List<Terreno> terrenos) {
        terrenos.forEach(terreno -> terreno.suscribirA(this));
    }

    @Override
    public TileData toData() {
        return new TileData(TileType.LOCK, letter, open);
    }
}
