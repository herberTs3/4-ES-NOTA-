package sokoban.model.puntaje;

/** Da mas peso a la cantidad de movimientos. */
public class PoliticaPuntajeEficiencia implements PoliticaPuntaje {

    @Override
    public int calcular(EstadisticasNivel datos) {
        return Math.max(0, 1000 - 7 * datos.movimientos() - 6 * datos.empujes() - 40 * datos.usosUndo());
    }

    @Override
    public String nombre() {
        return "Eficiencia";
    }
}
