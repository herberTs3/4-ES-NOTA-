package sokoban.modelo.caja;

import sokoban.modelo.Posicion;

/** Caja que se rompe tras una cantidad limitada de empujes. */
public class CajaFragil extends Caja {

    private int movimientosRestantes;

    public CajaFragil(Posicion posicion, int movimientosRestantes) {
        super(posicion);
        this.movimientosRestantes = movimientosRestantes;
    }

    public int getMovimientosRestantes() {
        return movimientosRestantes;
    }

    public boolean estaRota() {
        return movimientosRestantes <= 0;
    }

    @Override
    public void empujar() {
        if (movimientosRestantes > 0) {
            movimientosRestantes--;
        }
    }

    @Override
    public boolean requiereDestino() {
        return true;
    }

    @Override
    public void aceptar(VisitanteCaja visitante) {
        visitante.visitarCajaFragil(movimientosRestantes);
    }
}
