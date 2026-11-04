package sokoban.modelo.terreno;

public abstract class Terreno {

    public abstract boolean esTransitable();

    /** Un terreno es destino cuando las cajas deben llevarse hasta él. */
    public boolean esDestino() {
        return false;
    }

    /** Un muro delimita el tablero. */
    public boolean esMuro() {
        return false;
    }

    /**
     * Double dispatch. Por defecto un terreno se informa como "otro"; así un terreno nuevo funciona sin
     * tocar la vista y se dibuja como piso común hasta que se le agregue su propio método de visita.
     */
    public void aceptar(VisitanteTerreno visitante) {
        visitante.visitarOtro();
    }
}
