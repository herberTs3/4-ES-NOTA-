package sokoban.carga.fabrica;

import sokoban.modelo.terreno.Terreno;
import sokoban.modelo.terreno.TerrenoResbaladizo;

/**
 * Creador concreto de {@link TerrenoResbaladizo} (hielo).
 *
 * <p>Símbolo reconocido: {@code ~}.</p>
 */
public class CreadorTerrenoResbaladizo implements CreadorTerreno {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo == '~';
    }

    @Override
    public Terreno crear(char simbolo) {
        return new TerrenoResbaladizo();
    }
}
