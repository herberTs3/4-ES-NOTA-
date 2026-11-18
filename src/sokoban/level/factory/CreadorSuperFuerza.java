package sokoban.level.factory;

import sokoban.dto.TipoPowerUp;
import sokoban.model.Posicion;
import sokoban.model.PowerUpEnMapa;

public class CreadorSuperFuerza implements CreadorPowerUp {

    private static final char SIMBOLO = 'F';

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo == SIMBOLO;
    }

    @Override
    public PowerUpEnMapa crear(char simbolo, Posicion posicion) {
        return new PowerUpEnMapa(TipoPowerUp.SUPERFUERZA, posicion);
    }
}
