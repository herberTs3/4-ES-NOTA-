package sokoban.model.puntaje;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/** Registro explicito de estrategias. Las reglas no necesitan conocer sus clases concretas. */
public class RegistroPoliticasPuntaje {
    private final Map<String, Supplier<? extends PoliticaPuntaje>> fabricas = new LinkedHashMap<>();

    public static RegistroPoliticasPuntaje conPoliticasBase() {
        RegistroPoliticasPuntaje registro = new RegistroPoliticasPuntaje();
        registro.registrar("tradicional", PoliticaPuntajeTradicional::new);
        registro.registrar("eficiencia", PoliticaPuntajeEficiencia::new);
        registro.registrar("sinAyudas", PoliticaPuntajeSinAyudas::new);
        return registro;
    }

    /** Para ampliar el catalogo basta con registrar una nueva politica aqui o desde configuracion. */
    public void registrar(String identificador, Supplier<? extends PoliticaPuntaje> fabrica) {
        if (identificador == null || !identificador.matches("[a-zA-Z][a-zA-Z0-9_-]*")) {
            throw new IllegalArgumentException("Identificador de politica invalido: " + identificador);
        }
        Objects.requireNonNull(fabrica, "fabrica");
        if (fabricas.putIfAbsent(identificador, fabrica) != null) {
            throw new IllegalArgumentException("Politica duplicada: " + identificador);
        }
    }

    public boolean contiene(String identificador) {
        return fabricas.containsKey(identificador);
    }

    public PoliticaPuntaje crear(String identificador) {
        Supplier<? extends PoliticaPuntaje> fabrica = fabricas.get(identificador);
        if (fabrica == null) {
            throw new IllegalArgumentException("Politica no registrada: " + identificador);
        }
        return Objects.requireNonNull(fabrica.get(), "La fabrica de politica devolvio null: " + identificador);
    }
}
