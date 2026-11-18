package sokoban.model.terrain;

import java.util.List;

import sokoban.dto.TileData;
import sokoban.model.box.Caja;

public abstract class Terreno {

    public abstract boolean esTransitable();

    public abstract TileData toData();

    public boolean esDestino() {
        return false;
    }

    public boolean esMuro() {
        return false;
    }

    public boolean esResbaladizo() {
        return false;
    }

    public boolean esCerrojoDe(char letra) {
        return false;
    }

    public boolean esCerrojoCerrado() {
        return false;
    }

    public boolean isOpen() {
        return false;
    }

    public void restoreOpen(boolean open) {
    }

    public void alEntrarCaja(Caja caja) {
    }

    public void vincular(List<Terreno> terrenos) {
    }

    public void suscribirA(Lock lock) {
    }
}
