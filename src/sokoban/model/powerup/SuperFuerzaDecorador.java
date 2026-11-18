package sokoban.model.powerup;

import sokoban.dto.EstadoPowerUp;
import sokoban.dto.TipoPowerUp;
import sokoban.model.Direction;
import sokoban.model.Tablero;

public class SuperFuerzaDecorador extends PowerUpDecorador {

    private static final int SIN_USOS = 0;

    public SuperFuerzaDecorador(AccionesJugador componente, Tablero tablero, int turnosRestantes) {
        super(componente, tablero, turnosRestantes);
    }

    @Override
    public boolean jalarCaja(Direction direccion) {
        if (estaActivo() && tablero.jalarCaja(direccion)) {
            return true;
        }
        return componente.jalarCaja(direccion);
    }

    @Override
    public EstadoPowerUp estado() {
        return new EstadoPowerUp(TipoPowerUp.SUPERFUERZA, turnosRestantes, SIN_USOS);
    }
}
