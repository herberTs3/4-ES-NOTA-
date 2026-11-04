package sokoban.model.box;

import java.util.Objects;

import sokoban.dto.BoxData;
import sokoban.model.Direction;
import sokoban.model.Posicion;
import sokoban.model.Tablero;
import sokoban.model.terrain.Terreno;

public abstract class Caja {

    private Posicion posicion;

    protected Caja(Posicion posicion) {
        this.posicion = Objects.requireNonNull(posicion);
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public final void empujar(Direction direccion, Tablero tablero) {
        posicion = posicion.next(direccion);
        deslizar(direccion, tablero);
        tablero.getTerreno(posicion).alEntrarCaja(this);
        despuesDeEmpujar();
    }

    protected void despuesDeEmpujar() {
    }

    public boolean estaRota() {
        return false;
    }

    public boolean abreCerrojos() {
        return false;
    }

    public abstract boolean requiereDestino();

    public abstract boolean estaEnLugar(Terreno terreno);

    public abstract BoxData toData(boolean enLugar);

    private void deslizar(Direction direccion, Tablero tablero) {
        while (tablero.getTerreno(posicion).esResbaladizo() && tablero.puedeRecibirCaja(posicion.next(direccion))) {
            posicion = posicion.next(direccion);
        }
    }
}
