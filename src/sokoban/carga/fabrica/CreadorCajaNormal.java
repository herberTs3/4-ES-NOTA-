package sokoban.carga.fabrica;

import sokoban.modelo.Posicion;
import sokoban.modelo.caja.Caja;
import sokoban.modelo.caja.CajaNormal;

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
