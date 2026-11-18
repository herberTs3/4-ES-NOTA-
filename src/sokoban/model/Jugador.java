package sokoban.model;

import java.util.Objects;

public class Jugador {

    private Posicion posicion;

    public Jugador(Posicion posicion) {
        this.posicion = Objects.requireNonNull(posicion);
    }

    public Posicion getPosicion() {
        return posicion;
    }

    void moverA(Posicion destino) {
        posicion = Objects.requireNonNull(destino);
    }
}
