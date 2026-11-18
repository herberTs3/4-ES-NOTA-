package sokoban.level.factory;

import sokoban.model.Posicion;
import sokoban.model.box.Caja;
import sokoban.model.box.CajaFragil;

/**
 * Creador concreto de {@link CajaFragil}, parametrizado por el símbolo.
 *
 * <p>Símbolos reconocidos: los dígitos {@code 1-9}. El dígito es la cantidad de empujes que
 * la caja aguanta antes de romperse ({@code 3} = tres movimientos restantes). El {@code 0}
 * no existe a propósito: una caja que nace rota no tendría sentido.</p>
 */
public class CreadorCajaFragil implements CreadorCaja {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo >= '1' && simbolo <= '9';
    }

    @Override
    public Caja crear(char simbolo, Posicion posicion) {
        int movimientos = simbolo - '0';
        return new CajaFragil(posicion, movimientos);
    }
}
