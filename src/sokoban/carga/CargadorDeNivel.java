package sokoban.carga;

import sokoban.modelo.Tablero;

/**
 * Estrategia para construir el Tablero de un nivel.
 *
 * Quien la use (Partida, la vista, etc.) solo conoce esta interfaz, de modo que
 * el origen de los niveles (txt, JSON, base de datos, generador aleatorio...)
 * se puede cambiar sin tocar el resto del juego.
 */
public interface CargadorDeNivel {

    /**
     * @param numeroDeNivel número de nivel, empezando en 1
     * @return el tablero listo para jugar
     * @throws CargaDeNivelException si el nivel no existe o es inválido
     */
    Tablero cargarNivel(int numeroDeNivel) throws CargaDeNivelException;
}
