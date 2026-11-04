package sokoban.model.box;

import sokoban.dto.BoxData;
import sokoban.dto.BoxType;
import sokoban.model.Posicion;
import sokoban.model.terrain.Terreno;

public class CajaNormal extends Caja {

    public CajaNormal(Posicion posicion) {
        super(posicion);
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
        return new BoxData(BoxType.NORMAL, getPosicion().toData(), 0, enLugar);
    }
}
