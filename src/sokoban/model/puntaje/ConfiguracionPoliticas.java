package sokoban.model.puntaje;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Properties;

/** Carga reglas desde un archivo externo; ofrece valores predeterminados si no existe. */
public final class ConfiguracionPoliticas {
    public static final String RUTA_POR_DEFECTO = "config/politicas.properties";
    public static final String PROPIEDAD_RUTA = "sokoban.puntaje.config";

    private static final String REGLAS_ORIGINALES = """
            politica.predeterminada=tradicional
            reglas=eficiencia_tamano,eficiencia_cajas,eficiencia_puertas,sin_ayudas
            regla.eficiencia_tamano.politica=eficiencia
            regla.eficiencia_tamano.prioridad=100
            regla.eficiencia_tamano.celdasJugables.min=90
            regla.eficiencia_cajas.politica=eficiencia
            regla.eficiencia_cajas.prioridad=100
            regla.eficiencia_cajas.cajas.min=5
            regla.eficiencia_puertas.politica=eficiencia
            regla.eficiencia_puertas.prioridad=100
            regla.eficiencia_puertas.puertas.min=2
            regla.eficiencia_puertas.cajas.min=3
            regla.sin_ayudas.politica=sinAyudas
            regla.sin_ayudas.prioridad=50
            regla.sin_ayudas.powerUps.max=0
            regla.sin_ayudas.celdasJugables.max=55
            regla.sin_ayudas.cajas.max=3
            regla.sin_ayudas.hielo.max=0
            regla.sin_ayudas.puertas.max=0
            """;

    private final String predeterminada;
    private final List<ReglaSeleccionPuntaje> reglas;

    private ConfiguracionPoliticas(String predeterminada, List<ReglaSeleccionPuntaje> reglas) {
        this.predeterminada = predeterminada;
        this.reglas = List.copyOf(reglas);
    }

    public String predeterminada() { return predeterminada; }
    public List<ReglaSeleccionPuntaje> reglas() { return reglas; }

    /** La ruta puede cambiarse con -Dsokoban.puntaje.config=/ruta/politicas.properties */
    public static ConfiguracionPoliticas cargarPorDefecto() {
        return cargar(Path.of(System.getProperty(PROPIEDAD_RUTA, RUTA_POR_DEFECTO)));
    }

    /** Si falta el archivo se conserva el comportamiento previo; si existe pero esta mal, falla con un error claro. */
    public static ConfiguracionPoliticas cargar(Path ruta) {
        Properties propiedades = new Properties();
        try {
            if (Files.exists(ruta)) {
                try (var lector = Files.newBufferedReader(ruta)) {
                    propiedades.load(lector);
                }
            } else {
                propiedades.load(new StringReader(REGLAS_ORIGINALES));
            }
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer la configuracion de puntajes: " + ruta, ex);
        }
        return desde(propiedades);
    }

    private static ConfiguracionPoliticas desde(Properties props) {
        String predeterminada = requerido(props, "politica.predeterminada");
        List<ReglaSeleccionPuntaje> reglas = new ArrayList<>();
        String lista = requerido(props, "reglas");
        for (String candidato : lista.split(",", -1)) {
            String id = candidato.trim();
            if (id.isEmpty()) throw new IllegalArgumentException("Hay una regla vacia en reglas");
            if (reglas.stream().anyMatch(regla -> regla.identificador().equals(id))) {
                throw new IllegalArgumentException("Regla de seleccion repetida: " + id);
            }
            String prefijo = "regla." + id + ".";
            String politica = requerido(props, prefijo + "politica");
            int prioridad = entero(props, prefijo + "prioridad", 0);
            Map<String, ReglaSeleccionPuntaje.Rango> condiciones = new LinkedHashMap<>();
            for (String campo : List.of("celdasJugables", "cajas", "puertas", "hielo", "powerUps")) {
                Integer minimo = enteroOpcional(props, prefijo + campo + ".min");
                Integer maximo = enteroOpcional(props, prefijo + campo + ".max");
                if (minimo != null || maximo != null) {
                    condiciones.put(campo, new ReglaSeleccionPuntaje.Rango(minimo, maximo));
                }
            }
            // Evita errores silenciosos de tipografia en una condicion.
            for (String clave : props.stringPropertyNames()) {
                if (clave.startsWith(prefijo)) {
                    String sufijo = clave.substring(prefijo.length());
                    if (!sufijo.equals("politica") && !sufijo.equals("prioridad")
                            && !condicionValida(sufijo)) {
                        throw new IllegalArgumentException("Propiedad desconocida: " + clave);
                    }
                }
            }
            reglas.add(new ReglaSeleccionPuntaje(id, politica, prioridad, condiciones));
        }
        return new ConfiguracionPoliticas(predeterminada, reglas);
    }

    private static boolean condicionValida(String sufijo) {
        int punto = sufijo.lastIndexOf('.');
        return punto > 0 && ReglaSeleccionPuntaje.esCaracteristicaSoportada(sufijo.substring(0, punto))
                && (sufijo.substring(punto + 1).equals("min") || sufijo.substring(punto + 1).equals("max"));
    }

    private static String requerido(Properties props, String clave) {
        String valor = props.getProperty(clave);
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("Falta propiedad: " + clave);
        return valor.trim();
    }

    private static Integer enteroOpcional(Properties props, String clave) {
        String valor = props.getProperty(clave);
        if (valor == null) return null;
        try { return Integer.valueOf(valor.trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Entero invalido en " + clave, ex); }
    }

    private static int entero(Properties props, String clave, int porDefecto) {
        Integer valor = enteroOpcional(props, clave);
        return valor == null ? porDefecto : valor;
    }
}
