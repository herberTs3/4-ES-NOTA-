package sokoban.model.powerup;

import sokoban.model.Direction;
import sokoban.model.MoveResult;

public interface AccionesJugador {

    MoveResult mover(Direction direccion);

    boolean romperMuro(Direction direccion);

    boolean jalarCaja(Direction direccion);
}
