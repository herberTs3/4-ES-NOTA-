package sokoban.model.puntaje;

/** Penaliza especialmente los usos del boton deshacer. */
public class PoliticaPuntajeSinAyudas implements PoliticaPuntaje {

    @Override
    public int calcular(EstadisticasNivel datos) {
        return Math.max(0, 1000 - 3 * datos.movimientos() - 6 * datos.empujes() - 250 * datos.usosUndo());
    }

    @Override
    public String nombre() {
        return "Sin ayudas";
    }
}
