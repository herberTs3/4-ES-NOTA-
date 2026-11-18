package sokoban.level.factory;

import sokoban.model.Posicion;
import sokoban.model.PowerUpEnMapa;

public interface CreadorPowerUp {

    boolean reconoce(char simbolo);

    PowerUpEnMapa crear(char simbolo, Posicion posicion);
}
