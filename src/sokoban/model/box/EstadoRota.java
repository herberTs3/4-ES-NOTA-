package sokoban.model.box;

import sokoban.dto.EstadoVisualCajaFragil;

/** Estado terminal: Tablero conserva la regla existente de retirar la caja rota. */
public final class EstadoRota implements EstadoCajaFragil {

    @Override
    public void alSerEmpujada(CajaFragil caja) {
        // No hay resistencia restante que consumir.
    }

    @Override
    public void alSerJalada(CajaFragil caja) {
        throw new IllegalStateException("Una caja rota no puede jalarse");
    }

    @Override
    public boolean permiteJalar() {
        return false;
    }

    @Override
    public boolean estaRota() {
        return true;
    }

    @Override
    public EstadoVisualCajaFragil apariencia() {
        return EstadoVisualCajaFragil.ROTA;
    }
}
