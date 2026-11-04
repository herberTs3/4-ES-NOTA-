package sokoban.model.box;

import sokoban.dto.BoxData;
import sokoban.dto.BoxType;
import sokoban.model.Posicion;
import sokoban.model.terrain.Terreno;

public class CajaFragil extends Caja {

    private int movimientosRestantes;

    public CajaFragil(Posicion posicion, int movimientosRestantes) {
        super(posicion);
        this.movimientosRestantes = movimientosRestantes;
    }

    public int getMovimientosRestantes() {
        return movimientosRestantes;
    }

    @Override
    public boolean estaRota() {
        return movimientosRestantes <= 0;
    }

    @Override
    protected void despuesDeEmpujar() {
        movimientosRestantes--;
    }

    @Override
    public boolean requiereDestino() {
        return true;
    }

    @Override
    public boolean estaEnLugar(Terreno terreno) {
        return terreno.esDestino();
    }

    @Override
    public BoxData toData(boolean enLugar) {
        return new BoxData(BoxType.FRAGILE, getPosicion().toData(), movimientosRestantes, enLugar);
    }
}
