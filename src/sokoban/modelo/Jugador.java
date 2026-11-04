package sokoban.modelo;

import java.util.Objects;

public class Jugador {

    private Posicion posicion;

    public Jugador(Posicion posicion) {
        this.posicion = Objects.requireNonNull(posicion);
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = Objects.requireNonNull(posicion);
    }
}
