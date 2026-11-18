package sokoban.model.box;

import sokoban.dto.EstadoVisualCajaFragil;

/** Estado inicial: la caja todavia consumio menos del 50 % de su resistencia. */
public final class EstadoIntacta implements EstadoCajaFragil {

    @Override
    public void alSerEmpujada(CajaFragil caja) {
        caja.consumirResistencia();
    }

    @Override
    public void alSerJalada(CajaFragil caja) {
        caja.consumirResistencia();
    }

    @Override
    public boolean permiteJalar() {
        return true;
    }

    @Override
    public boolean estaRota() {
        return false;
    }

    @Override
    public EstadoVisualCajaFragil apariencia() {
        return EstadoVisualCajaFragil.INTACTA;
    }
}
