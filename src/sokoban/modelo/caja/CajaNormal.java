package sokoban.modelo.caja;

import sokoban.modelo.Posicion;

public class CajaNormal extends Caja {

    public CajaNormal(Posicion posicion) {
        super(posicion);
    }

    @Override
    public void empujar() {
        // Una caja normal no cambia de estado al ser empujada.
    }

    @Override
    public boolean requiereDestino() {
        return true;
    }

    @Override
    public void aceptar(VisitanteCaja visitante) {
        visitante.visitarCajaNormal();
    }
}
