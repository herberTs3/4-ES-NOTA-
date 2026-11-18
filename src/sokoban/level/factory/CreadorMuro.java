package sokoban.level.factory;

import sokoban.model.terrain.Muro;
import sokoban.model.terrain.Terreno;

/**
 * Creador concreto de {@link Muro}.
 *
 * <p>Símbolo reconocido: {@code #}.</p>
 *
 * <p>Nota: el espacio en blanco que queda <i>fuera</i> del área alcanzable también termina
 * siendo un muro, pero eso no se decide acá. El cargador lo convierte en {@code #} antes de
 * consultar a los creadores, de modo que este creador sigue siendo trivial y no necesita
 * conocer el contexto del tablero.</p>
 */
public class CreadorMuro implements CreadorTerreno {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo == '#';
    }

    @Override
    public Terreno crear(char simbolo) {
        return new Muro();
    }
}
