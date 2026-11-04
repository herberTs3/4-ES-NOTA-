package sokoban.carga.fabrica;

import sokoban.modelo.terreno.Terreno;
import sokoban.modelo.terreno.TerrenoDestino;

/**
 * Creador concreto de {@link TerrenoDestino}.
 *
 * <p>Símbolos reconocidos:</p>
 * <pre>
 *   .   destino vacío
 *   +   jugador sobre un destino
 *   *   caja normal sobre un destino
 * </pre>
 * <p>En los dos últimos casos, el jugador o la caja los crea otro componente (el cargador y
 * {@link CreadorCajaNormal}); a este creador solo le toca el destino que hay debajo.</p>
 */
public class CreadorTerrenoDestino implements CreadorTerreno {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo == '.' || simbolo == '+' || simbolo == '*';
    }

    @Override
    public Terreno crear(char simbolo) {
        return new TerrenoDestino();
    }
}
