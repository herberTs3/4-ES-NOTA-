package sokoban.model.powerup;

import java.util.Objects;

import sokoban.dto.EstadoPowerUp;
import sokoban.model.Direction;
import sokoban.model.MoveResult;
import sokoban.model.Tablero;

public abstract class PowerUpDecorador implements AccionesJugador {

    protected final AccionesJugador componente;
    protected final Tablero tablero;
    protected int turnosRestantes;

    protected PowerUpDecorador(AccionesJugador componente, Tablero tablero, int turnosRestantes) {
        this.componente = Objects.requireNonNull(componente);
        this.tablero = Objects.requireNonNull(tablero);
        this.turnosRestantes = turnosRestantes;
    }

    public static PowerUpDecorador desde(EstadoPowerUp estado, AccionesJugador componente, Tablero tablero) {
        return switch (estado.tipo()) {
            case MARTILLO -> new MartilloDecorador(componente, tablero, estado.turnosRestantes(),
                    estado.usosRestantes());
            case SUPERFUERZA -> new SuperFuerzaDecorador(componente, tablero, estado.turnosRestantes());
        };
    }

    @Override
    public MoveResult mover(Direction direccion) {
        return componente.mover(direccion);
    }

    @Override
    public boolean romperMuro(Direction direccion) {
        return componente.romperMuro(direccion);
    }

    @Override
    public boolean jalarCaja(Direction direccion) {
        return componente.jalarCaja(direccion);
    }

    public void avanzarTurno() {
        turnosRestantes--;
    }

    public boolean estaActivo() {
        return turnosRestantes > 0;
    }

    public abstract EstadoPowerUp estado();
}
