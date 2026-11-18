package sokoban.model.powerup;

import java.util.Objects;

import sokoban.model.Direction;
import sokoban.model.MoveResult;
import sokoban.model.Tablero;

public class JugadorBase implements AccionesJugador {

    private final Tablero tablero;

    public JugadorBase(Tablero tablero) {
        this.tablero = Objects.requireNonNull(tablero);
    }

    @Override
    public MoveResult mover(Direction direccion) {
        return tablero.moverJugador(direccion);
    }

    @Override
    public boolean romperMuro(Direction direccion) {
        return false;
    }

    @Override
    public boolean jalarCaja(Direction direccion) {
        return false;
    }
}
