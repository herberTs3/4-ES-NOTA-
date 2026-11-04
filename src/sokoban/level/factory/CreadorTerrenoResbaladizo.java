package sokoban.level.factory;

import sokoban.model.terrain.Terreno;
import sokoban.model.terrain.TerrenoResbaladizo;

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
