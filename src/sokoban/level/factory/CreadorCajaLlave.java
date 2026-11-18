package sokoban.level.factory;

import sokoban.model.Posicion;
import sokoban.model.box.Caja;
import sokoban.model.box.CajaLlave;

public class CreadorCajaLlave implements CreadorCaja {

    private static final char PRIMERA_LLAVE = 'K';
    private static final char ULTIMA_LLAVE = 'O';
    private static final char PRIMER_CERROJO = 'a';

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo >= PRIMERA_LLAVE && simbolo <= ULTIMA_LLAVE;
    }

    @Override
    public Caja crear(char simbolo, Posicion posicion) {
        char letra = (char) (PRIMER_CERROJO + simbolo - PRIMERA_LLAVE);
        return new CajaLlave(posicion, letra);
    }
}
