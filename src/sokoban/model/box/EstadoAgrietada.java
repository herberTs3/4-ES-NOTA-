package sokoban.model.box;

import sokoban.dto.EstadoVisualCajaFragil;

/** Caja que consumio al menos la mitad de su resistencia: empujable, no jalable. */
public final class EstadoAgrietada implements EstadoCajaFragil {

    @Override
    public void alSerEmpujada(CajaFragil caja) {
        caja.consumirResistencia();
    }

    @Override
    public void alSerJalada(CajaFragil caja) {
        throw new IllegalStateException("Una caja fragil agrietada no puede jalarse");
    }

    @Override
    public boolean permiteJalar() {
        return false;
    }

    @Override
    public boolean estaRota() {
        return false;
    }

    @Override
    public EstadoVisualCajaFragil apariencia() {
        return EstadoVisualCajaFragil.AGRIETADA;
    }
}
