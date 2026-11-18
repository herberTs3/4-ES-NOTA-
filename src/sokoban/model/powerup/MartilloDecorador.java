package sokoban.model.powerup;

import sokoban.dto.EstadoPowerUp;
import sokoban.dto.TipoPowerUp;
import sokoban.model.Direction;
import sokoban.model.Tablero;

public class MartilloDecorador extends PowerUpDecorador {

    private int usosRestantes;

    public MartilloDecorador(AccionesJugador componente, Tablero tablero, int turnosRestantes, int usosRestantes) {
        super(componente, tablero, turnosRestantes);
        this.usosRestantes = usosRestantes;
    }

    @Override
    public boolean romperMuro(Direction direccion) {
        if (estaActivo() && tablero.romperMuroInterior(direccion)) {
            usosRestantes--;
            return true;
        }
        return componente.romperMuro(direccion);
    }

    @Override
    public boolean estaActivo() {
        return super.estaActivo() && usosRestantes > 0;
    }

    @Override
    public EstadoPowerUp estado() {
        return new EstadoPowerUp(TipoPowerUp.MARTILLO, turnosRestantes, usosRestantes);
    }
}
