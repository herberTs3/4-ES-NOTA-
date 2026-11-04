package sokoban.carga.fabrica;

import sokoban.modelo.terreno.Terreno;
import sokoban.modelo.terreno.TerrenoNormal;

/**
 * Creador concreto de {@link TerrenoNormal} (piso común).
 *
 * <p>Es el creador que reconoce más símbolos, porque muchas cosas "están paradas sobre piso
 * común" aunque lo que se vea en el archivo sea otra cosa:</p>
 * <pre>
 *   ' '      piso (el cargador ya descartó el que está fuera del área alcanzable)
 *   - _      piso explícito
 *   @        el jugador (el jugador lo ubica el cargador; el terreno de abajo es piso)
 *   $        caja normal sobre piso
 *   a-z      caja llave sobre piso
 *   1-9      caja frágil sobre piso
 * </pre>
 *
 * <p>Esto muestra la separación en capas: el símbolo {@code $} lo reconocen
 * {@code CreadorTerrenoNormal} (la capa de terreno) y {@link CreadorCajaNormal} (la capa de
 * cajas), cada uno encargándose solo de su parte.</p>
 */
public class CreadorTerrenoNormal implements CreadorTerreno {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo == ' ' || simbolo == '-' || simbolo == '_'
                || simbolo == '@' || simbolo == '$'
                || (simbolo >= 'a' && simbolo <= 'z')
                || (simbolo >= '1' && simbolo <= '9');
    }

    @Override
    public Terreno crear(char simbolo) {
        return new TerrenoNormal();
    }
}
