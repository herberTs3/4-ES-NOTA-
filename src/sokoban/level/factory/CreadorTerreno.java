package sokoban.level.factory;

import sokoban.model.terrain.Terreno;

/**
 * <b>Creador</b> (rol "Creator" del patrón Factory Method) para la familia de {@link Terreno}.
 *
 * <p>Cada implementación sabe construir <i>un</i> tipo de terreno y declara qué símbolos del
 * archivo le corresponden. El resto del programa solo conoce esta interfaz, nunca las clases
 * concretas de terreno.</p>
 *
 * <h3>Contrato</h3>
 * <ul>
 *   <li>{@link #reconoce(char)} debe ser una función pura: mismo símbolo, misma respuesta,
 *       sin efectos secundarios.</li>
 *   <li>{@link #crear(char)} solo se invoca con símbolos para los que {@code reconoce}
 *       devolvió {@code true}.</li>
 *   <li>{@code crear} debe devolver <b>una instancia nueva en cada llamada</b>. Los terrenos
 *       con estado (como el cerrojo, que se abre) no pueden compartirse entre celdas.</li>
 *   <li>Dos creadores de la misma familia no deberían reconocer el mismo símbolo; el
 *       {@link RegistroDeCreadores} lo detecta y avisa.</li>
 * </ul>
 *
 * <h3>¿Por qué {@code crear} recibe el símbolo?</h3>
 * <p>Porque algunos terrenos llevan un parámetro codificado en el propio símbolo: el cerrojo
 * y el muro cerrado guardan su letra ({@code a}-{@code e}, {@code A}-{@code E}). Pasar el símbolo permite que un
 * único creador atienda a todo un rango de caracteres sin crear una clase por letra.</p>
 *
 * <h3>Cómo agregar un terreno nuevo</h3>
 * <ol>
 *   <li>Crear la clase del terreno en {@code sokoban.model.terrain}.</li>
 *   <li>Crear su creador implementando esta interfaz.</li>
 *   <li>Registrarlo: {@code registro.registrar(new CreadorMiTerreno())}.</li>
 * </ol>
 * <p>No hace falta tocar el cargador ni ningún otro creador.</p>
 */
public interface CreadorTerreno {

    /**
     * @param simbolo carácter de una celda del archivo de nivel
     * @return {@code true} si este creador sabe construir el terreno que representa el símbolo
     */
    boolean reconoce(char simbolo);

    /**
     * Construye el terreno correspondiente al símbolo.
     *
     * @param simbolo un símbolo para el cual {@link #reconoce(char)} devolvió {@code true}
     * @return una instancia nueva de terreno (nunca {@code null})
     */
    Terreno crear(char simbolo);
}
