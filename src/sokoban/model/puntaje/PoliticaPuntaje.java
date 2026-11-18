package sokoban.model.puntaje;

/** Strategy: contrato de los distintos criterios de puntuacion. */
public interface PoliticaPuntaje {
    int calcular(EstadisticasNivel estadisticas);
    String nombre();
}
