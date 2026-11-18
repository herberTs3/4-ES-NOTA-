package sokoban.model.puntaje;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Elige la estrategia segun reglas configuradas, sin conocer condiciones ni clases concretas. */
public class SelectorPoliticaPuntaje {
    private final RegistroPoliticasPuntaje registro;
    private final List<ReglaSeleccionPuntaje> reglas;
    private final String predeterminada;

    public SelectorPoliticaPuntaje() {
        this(RegistroPoliticasPuntaje.conPoliticasBase(), ConfiguracionPoliticas.cargarPorDefecto());
    }

    public SelectorPoliticaPuntaje(RegistroPoliticasPuntaje registro, ConfiguracionPoliticas configuracion) {
        this.registro = Objects.requireNonNull(registro, "registro");
        Objects.requireNonNull(configuracion, "configuracion");
        this.predeterminada = configuracion.predeterminada();
        if (!registro.contiene(predeterminada)) {
            throw new IllegalArgumentException("Politica predeterminada no registrada: " + predeterminada);
        }
        List<ReglaSeleccionPuntaje> ordenadas = new ArrayList<>(configuracion.reglas());
        for (ReglaSeleccionPuntaje regla : ordenadas) {
            if (!registro.contiene(regla.politica())) {
                throw new IllegalArgumentException("Politica no registrada en regla "
                        + regla.identificador() + ": " + regla.politica());
            }
        }
        // Sort estable: si hay empate de prioridad, gana la primera regla del properties.
        ordenadas.sort(Comparator.comparingInt(ReglaSeleccionPuntaje::prioridad).reversed());
        this.reglas = List.copyOf(ordenadas);
    }

    public PoliticaPuntaje seleccionar(CaracteristicasNivel nivel) {
        Objects.requireNonNull(nivel, "nivel");
        for (ReglaSeleccionPuntaje regla : reglas) {
            if (regla.cumple(nivel)) {
                return registro.crear(regla.politica());
            }
        }
        return registro.crear(predeterminada);
    }
}
