package sokoban.model.puntaje;

import java.util.Objects;

/** Context del patron Strategy: delega el algoritmo a la politica seleccionada. */
public class CalculadorPuntaje {

    private PoliticaPuntaje politica;

    public void establecerPolitica(PoliticaPuntaje politica) {
        this.politica = Objects.requireNonNull(politica, "politica");
    }

    public ResultadoPuntaje calcular(EstadisticasNivel estadisticas) {
        Objects.requireNonNull(estadisticas, "estadisticas");
        if (politica == null) {
            throw new IllegalStateException("No hay una politica de puntaje seleccionada");
        }
        return new ResultadoPuntaje(politica.calcular(estadisticas), politica.nombre());
    }
}
