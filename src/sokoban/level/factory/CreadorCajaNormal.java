package sokoban.level.factory;

import sokoban.model.Posicion;
import sokoban.model.box.Caja;
import sokoban.model.box.CajaNormal;

/**
 * Creador concreto de {@link CajaNormal}.
 *
 * <p>Símbolos reconocidos:</p>
 * <pre>
 *   $   caja normal sobre piso
 *   *   caja normal sobre un destino (el destino lo crea CreadorTerrenoDestino)
 * </pre>
 */
public class CreadorCajaNormal implements CreadorCaja {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo == '$' || simbolo == '*';
    }

    @Override
    public Caja crear(char simbolo, Posicion posicion) {
        return new CajaNormal(posicion);
    }
}
