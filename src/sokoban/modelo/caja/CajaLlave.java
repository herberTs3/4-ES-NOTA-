package sokoban.modelo.caja;

import sokoban.modelo.Posicion;

/** Caja que abre el TerrenoCerrojo con su misma clave. */
public class CajaLlave extends Caja {

    private final int clave;

    public CajaLlave(Posicion posicion, int clave) {
        super(posicion);
        this.clave = clave;
    }

    public int getClave() {
        return clave;
    }

    @Override
    public void empujar() {
        // No cambia de estado al ser empujada.
    }

    @Override
    public boolean requiereDestino() {
        return false;
    }

    @Override
    public void aceptar(VisitanteCaja visitante) {
        visitante.visitarCajaLlave(clave);
    }
}
