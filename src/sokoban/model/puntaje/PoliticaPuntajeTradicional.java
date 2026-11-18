package sokoban.model.puntaje;

/** Puntuacion equilibrada entre movimientos, empujes y uso de deshacer. */
public class PoliticaPuntajeTradicional implements PoliticaPuntaje {

    @Override
    public int calcular(EstadisticasNivel datos) {
        return Math.max(0, 1000 - 4 * datos.movimientos() - 8 * datos.empujes() - 80 * datos.usosUndo());
    }

    @Override
    public String nombre() {
        return "Tradicional";
    }
}
