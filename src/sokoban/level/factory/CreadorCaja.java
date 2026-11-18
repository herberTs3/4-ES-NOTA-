package sokoban.level.factory;

import sokoban.model.Posicion;
import sokoban.model.box.Caja;

/**
 * <b>Creador</b> (rol "Creator" del patrón Factory Method) para la familia de {@link Caja}.
 *
 * <p>Es la hermana de {@link CreadorTerreno}, con dos diferencias:</p>
 * <ul>
 *   <li>{@link #crear(char, Posicion)} recibe también la <b>posición</b> inicial, porque toda
 *       caja necesita saber dónde está desde que nace (los terrenos, en cambio, no se
 *       "conocen" a sí mismos: es el tablero quien sabe en qué celda están).</li>
 *   <li>La mayoría de los símbolos <b>no</b> tienen caja (piso, muros, destinos...). Eso no es
 *       un error: simplemente ningún creador de cajas reconoce ese símbolo.</li>
 * </ul>
 *
 * <p>Se aplican los mismos contratos que en {@link CreadorTerreno}: {@code reconoce} es puro,
 * {@code crear} devuelve una instancia nueva cada vez y no hay dos creadores que reconozcan
 * el mismo símbolo.</p>
 *
 * <p>El símbolo sirve para parametrizar la caja: la frágil toma sus empujes del dígito ({@code 1}...{@code 9}).</p>
 */
public interface CreadorCaja {

    /**
     * @param simbolo carácter de una celda del archivo de nivel
     * @return {@code true} si en una celda con ese símbolo hay una caja de este tipo
     */
    boolean reconoce(char simbolo);

    /**
     * Construye la caja correspondiente al símbolo.
     *
     * @param simbolo  un símbolo para el cual {@link #reconoce(char)} devolvió {@code true}
     * @param posicion celda donde se coloca la caja al empezar el nivel
     * @return una instancia nueva de caja (nunca {@code null})
     */
    Caja crear(char simbolo, Posicion posicion);
}
