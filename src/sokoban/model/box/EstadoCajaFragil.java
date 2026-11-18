package sokoban.model.box;

import sokoban.dto.EstadoVisualCajaFragil;

/** State: comportamiento de una caja fragil segun el deterioro acumulado. */
public interface EstadoCajaFragil {

    void alSerEmpujada(CajaFragil caja);

    void alSerJalada(CajaFragil caja);

    boolean permiteJalar();

    boolean estaRota();

    EstadoVisualCajaFragil apariencia();
}
