package sokoban.modelo;

/** Coordenada (fila, columna) dentro del tablero. Es inmutable. */
public record Posicion(int fila, int columna) {

    public Posicion desplazada(int deltaFila, int deltaColumna) {
        return new Posicion(fila + deltaFila, columna + deltaColumna);
    }
}
