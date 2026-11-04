package sokoban.model.box;

import sokoban.dto.BoxData;
import sokoban.dto.BoxType;
import sokoban.model.Posicion;
import sokoban.model.terrain.Terreno;

public class CajaLlave extends Caja {

    public CajaLlave(Posicion posicion) {
        super(posicion);
    }

    @Override
    public Caja copiar() {
        return new CajaLlave(getPosicion());
    }

    @Override
    public boolean abreCerrojos() {
        return true;
    }

    @Override
    public boolean requiereDestino() {
        return false;
    }

    @Override
    public boolean estaEnLugar(Terreno terreno) {
        return terreno.esCerrojo();
    }

    @Override
    public BoxData toData(boolean enLugar) {
        return new BoxData(BoxType.KEY, getPosicion().toData(), 0, enLugar);
    }
}
