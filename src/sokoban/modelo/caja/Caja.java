package sokoban.modelo.caja;

import sokoban.modelo.Posicion;

import java.util.Objects;

public abstract class Caja {

    private Posicion posicion;

    protected Caja(Posicion posicion) {
        this.posicion = Objects.requireNonNull(posicion);
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = Objects.requireNonNull(posicion);
    }

    /** Se invoca cada vez que el jugador empuja la caja. */
    public abstract void empujar();

    /** Indica si la caja debe terminar sobre un destino para resolver el nivel. */
    public abstract boolean requiereDestino();

    /** Double dispatch: cada caja concreta llama al método de visita que le corresponde. */
    public abstract void aceptar(VisitanteCaja visitante);
}
