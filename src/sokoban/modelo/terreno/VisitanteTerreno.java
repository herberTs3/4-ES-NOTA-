package sokoban.modelo.terreno;

/**
 * Visitante de terrenos (double dispatch). Permite que quien consuma el modelo (la vista, por ejemplo)
 * reaccione según el tipo de terreno sin usar {@code instanceof}: cada terreno llama al método que le
 * corresponde desde {@link Terreno#aceptar(VisitanteTerreno)}.
 */
public interface VisitanteTerreno {

    void visitarMuro();

    void visitarTerrenoNormal();

    void visitarTerrenoDestino();

    void visitarTerrenoResbaladizo();

    void visitarTerrenoCerrojo(int clave, boolean abierto);

    /** Terreno que no tiene un método propio (por ejemplo, uno agregado por extensión). */
    void visitarOtro();
}
