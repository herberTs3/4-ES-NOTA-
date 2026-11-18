package sokoban.level.factory;

import sokoban.dto.TipoPowerUp;
import sokoban.model.Posicion;
import sokoban.model.PowerUpEnMapa;

public class CreadorMartillo implements CreadorPowerUp {

    private static final char SIMBOLO = 'H';

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo == SIMBOLO;
    }

    @Override
    public PowerUpEnMapa crear(char simbolo, Posicion posicion) {
        return new PowerUpEnMapa(TipoPowerUp.MARTILLO, posicion);
    }
}
