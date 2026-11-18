package sokoban.model.puntaje;

import java.util.Map;
import java.util.Objects;

/** Una regla configurable: sus condiciones se combinan con AND. Varias reglas pueden apuntar a la misma politica (OR). */
public final class ReglaSeleccionPuntaje {
    public record Rango(Integer minimo, Integer maximo) {
        public Rango {
            if (minimo == null && maximo == null) {
                throw new IllegalArgumentException("El rango debe tener al menos un limite");
            }
            if (minimo != null && minimo < 0 || maximo != null && maximo < 0
                    || minimo != null && maximo != null && minimo > maximo) {
                throw new IllegalArgumentException("Rango de seleccion invalido");
            }
        }

        public boolean contiene(int valor) {
            return (minimo == null || valor >= minimo) && (maximo == null || valor <= maximo);
        }
    }

    private final String identificador;
    private final String politica;
    private final int prioridad;
    private final Map<String, Rango> condiciones;

    public ReglaSeleccionPuntaje(String identificador, String politica, int prioridad,
                                  Map<String, Rango> condiciones) {
        this.identificador = Objects.requireNonNull(identificador);
        this.politica = Objects.requireNonNull(politica);
        this.prioridad = prioridad;
        this.condiciones = Map.copyOf(Objects.requireNonNull(condiciones));
        if (condiciones.isEmpty()) {
            throw new IllegalArgumentException("La regla " + identificador + " no tiene condiciones");
        }
        for (String criterio : condiciones.keySet()) {
            if (!esCaracteristicaSoportada(criterio)) {
                throw new IllegalArgumentException("Criterio desconocido en " + identificador + ": " + criterio);
            }
        }
    }

    public String identificador() { return identificador; }
    public String politica() { return politica; }
    public int prioridad() { return prioridad; }
    public Map<String, Rango> condiciones() { return condiciones; }

    public boolean cumple(CaracteristicasNivel nivel) {
        Objects.requireNonNull(nivel);
        return condiciones.entrySet().stream().allMatch(entrada ->
                entrada.getValue().contiene(valor(nivel, entrada.getKey())));
    }

    static boolean esCaracteristicaSoportada(String nombre) {
        return switch (nombre) {
            case "celdasJugables", "cajas", "puertas", "hielo", "powerUps" -> true;
            default -> false;
        };
    }

    private static int valor(CaracteristicasNivel nivel, String nombre) {
        return switch (nombre) {
            case "celdasJugables" -> nivel.celdasJugables();
            case "cajas" -> nivel.cajas();
            case "puertas" -> nivel.puertas();
            case "hielo" -> nivel.hielo();
            case "powerUps" -> nivel.powerUps();
            default -> throw new IllegalArgumentException("Criterio no soportado: " + nombre);
        };
    }
}
