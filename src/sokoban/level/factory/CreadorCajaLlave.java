package sokoban.level.factory;

import sokoban.model.Posicion;
import sokoban.model.box.Caja;
import sokoban.model.box.CajaLlave;

public class CreadorCajaLlave implements CreadorCaja {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo == 'K';
    }

    @Override
    public Caja crear(char simbolo, Posicion posicion) {
        return new CajaLlave(posicion);
    }
}
