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
        despuesDeMover();
    }

    public final void jalarA(Posicion destino, Tablero tablero) {
        if (!puedeSerJalada()) {
            throw new IllegalStateException("Esta caja no puede jalarse en su estado actual");
        }
        posicion = Objects.requireNonNull(destino);
        tablero.getTerreno(posicion).alEntrarCaja(this);
        despuesDeJalar();
    }

    /** Las cajas normales y llave conservan su comportamiento previo. */
    public boolean puedeSerJalada() {
        return true;
    }

    protected void despuesDeMover() {
    }

    /** Por compatibilidad, el comportamiento por defecto sigue siendo el de mover. */
    protected void despuesDeJalar() {
        despuesDeMover();
    }

    public boolean estaRota() {
        return false;
    }

    public boolean abreCerrojo(char letraDelCerrojo) {
        return false;
    }

    public abstract Caja copiar();

    public abstract boolean requiereDestino();

    public abstract boolean estaEnLugar(Terreno terreno);

    public abstract BoxData toData(boolean enLugar);

    private void deslizar(Direction direccion, Tablero tablero) {
        while (tablero.getTerreno(posicion).esResbaladizo() && tablero.puedeRecibirCaja(posicion.next(direccion))) {
            posicion = posicion.next(direccion);
        }
    }
}
