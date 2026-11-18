package sokoban.level.factory;

import sokoban.level.CargaDeNivelException;
import sokoban.model.Posicion;
import sokoban.model.PowerUpEnMapa;
import sokoban.model.box.Caja;
import sokoban.model.terrain.Terreno;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Catálogo de creadores disponibles: el punto de entrada único para construir terrenos y
 * cajas a partir de un símbolo.
 *
 * <h3>El problema que resuelve</h3>
 * <p>Factory Method define <i>cómo</i> se construye cada producto, pero no <i>quién decide</i>
 * qué creador usar para cada símbolo. Si esa decisión fuera un {@code switch} en el cargador,
 * volveríamos al punto de partida: el cargador conocería todas las clases concretas. Este
 * registro es esa decisión, convertida en datos: una lista de creadores a los que se les
 * pregunta {@code reconoce(simbolo)}.</p>
 *
 * <h3>Cómo se usa</h3>
 * <pre>{@code
 * // Configuración estándar: todos los terrenos y cajas del juego.
 * RegistroDeCreadores registro = RegistroDeCreadores.porDefecto();
 *
 * // Configuración propia: partir de cero y elegir qué existe.
 * RegistroDeCreadores minimo = new RegistroDeCreadores()
 *         .registrarTerreno(new CreadorMuro())
 *         .registrarTerreno(new CreadorTerrenoNormal())
 *         .registrarTerreno(new CreadorTerrenoDestino())
 *         .registrarCaja(new CreadorCajaNormal());
 *
 * // Extensión: sumar un tipo nuevo sin tocar nada existente.
 * registro.registrarTerreno(new CreadorMiTerrenoNuevo());
 * }</pre>
 *
 * <h3>Reglas de resolución</h3>
 * <ul>
 *   <li><b>Terreno:</b> toda celda tiene que tener terreno. Si ningún creador reconoce el
 *       símbolo, es un error de carga ({@link CargaDeNivelException}).</li>
 *   <li><b>Caja:</b> la mayoría de las celdas no tienen caja. Si ningún creador la reconoce,
 *       el resultado es simplemente {@link Optional#empty()}.</li>
 *   <li><b>Ambigüedad:</b> si dos creadores de la misma familia reconocen el mismo símbolo es
 *       un error de <i>programación</i> (configuración incoherente), no del archivo, y se
 *       informa con {@link IllegalStateException} nombrando a ambos. Se prefiere fallar
 *       fuerte antes que elegir uno en silencio según el orden de registro.</li>
 * </ul>
 *
 * <p>La clase no es segura para uso concurrente mientras se está registrando; la idea es
 * configurarla una vez al inicio y luego solo consultarla.</p>
 */
public class RegistroDeCreadores {

    private static final char SIMBOLO_PISO = ' ';

    private final List<CreadorTerreno> creadoresDeTerreno = new ArrayList<>();
    private final List<CreadorCaja> creadoresDeCaja = new ArrayList<>();
    private final List<CreadorPowerUp> creadoresDePowerUp = new ArrayList<>();

    // ------------------------------------------------------------- configuración

    /**
     * Crea el registro con todos los tipos que trae el juego.
     *
     * <p>Es el único lugar del programa que enumera las clases concretas de creadores. Si se
     * agrega un tipo nuevo "de fábrica", se suma una línea acá.</p>
     *
     * @return un registro nuevo e independiente (modificarlo no afecta a otros)
     */
    public static RegistroDeCreadores porDefecto() {
        return new RegistroDeCreadores()
                // Terrenos
                .registrarTerreno(new CreadorMuro())
                .registrarTerreno(new CreadorTerrenoNormal())
                .registrarTerreno(new CreadorTerrenoDestino())
                .registrarTerreno(new CreadorTerrenoResbaladizo())
                .registrarTerreno(new LockCreator())
                .registrarTerreno(new DoorCreator())
                // Cajas
                .registrarCaja(new CreadorCajaNormal())
                .registrarCaja(new CreadorCajaLlave())
                .registrarCaja(new CreadorCajaFragil())
                .registrarPowerUp(new CreadorMartillo())
                .registrarPowerUp(new CreadorSuperFuerza());
    }

    /**
     * Agrega un creador de terrenos al catálogo.
     *
     * @param creador creador a registrar (no puede ser {@code null})
     * @return este mismo registro, para encadenar llamadas
     */
    public RegistroDeCreadores registrarTerreno(CreadorTerreno creador) {
        creadoresDeTerreno.add(Objects.requireNonNull(creador, "creador"));
        return this;
    }

    /**
     * Agrega un creador de cajas al catálogo.
     *
     * @param creador creador a registrar (no puede ser {@code null})
     * @return este mismo registro, para encadenar llamadas
     */
    public RegistroDeCreadores registrarCaja(CreadorCaja creador) {
        creadoresDeCaja.add(Objects.requireNonNull(creador, "creador"));
        return this;
    }

    public RegistroDeCreadores registrarPowerUp(CreadorPowerUp creador) {
        creadoresDePowerUp.add(Objects.requireNonNull(creador, "creador"));
        return this;
    }

    // --------------------------------------------------------------- resolución

    /**
     * Construye el terreno que representa el símbolo.
     *
     * @param simbolo carácter de la celda
     * @return un terreno nuevo
     * @throws CargaDeNivelException si ningún creador de terrenos reconoce el símbolo
     * @throws IllegalStateException si más de un creador lo reconoce (configuración ambigua)
     */
    public Terreno crearTerreno(char simbolo) throws CargaDeNivelException {
        CreadorTerreno elegido = null;
        for (CreadorTerreno candidato : creadoresDeTerreno) {
            if (!candidato.reconoce(simbolo)) {
                continue;
            }
            if (elegido != null) {
                throw ambiguedad("terreno", simbolo, elegido, candidato);
            }
            elegido = candidato;
        }
        if (elegido != null) {
            return elegido.crear(simbolo);
        }
        if (hayCreadorDeCajaPara(simbolo) || hayCreadorDePowerUpPara(simbolo)) {
            return crearTerreno(SIMBOLO_PISO);
        }
        throw new CargaDeNivelException(
                String.format("carácter '%c' (U+%04X) no reconocido", simbolo, (int) simbolo));
    }

    private boolean hayCreadorDeCajaPara(char simbolo) {
        return creadoresDeCaja.stream().anyMatch(creador -> creador.reconoce(simbolo));
    }

    private boolean hayCreadorDePowerUpPara(char simbolo) {
        return creadoresDePowerUp.stream().anyMatch(creador -> creador.reconoce(simbolo));
    }

    public Optional<PowerUpEnMapa> crearPowerUp(char simbolo, Posicion posicion) {
        CreadorPowerUp elegido = null;
        for (CreadorPowerUp candidato : creadoresDePowerUp) {
            if (!candidato.reconoce(simbolo)) {
                continue;
            }
            if (elegido != null) {
                throw ambiguedad("power-up", simbolo, elegido, candidato);
            }
            elegido = candidato;
        }
        return elegido == null ? Optional.empty() : Optional.of(elegido.crear(simbolo, posicion));
    }

    /**
     * Construye la caja que hay en una celda con ese símbolo, si la hay.
     *
     * @param simbolo  carácter de la celda
     * @param posicion celda donde queda la caja
     * @return la caja nueva, o {@link Optional#empty()} si la celda no contiene ninguna
     * @throws IllegalStateException si más de un creador la reconoce (configuración ambigua)
     */
    public Optional<Caja> crearCaja(char simbolo, Posicion posicion) {
        CreadorCaja elegido = null;
        for (CreadorCaja candidato : creadoresDeCaja) {
            if (!candidato.reconoce(simbolo)) {
                continue;
            }
            if (elegido != null) {
                throw ambiguedad("caja", simbolo, elegido, candidato);
            }
            elegido = candidato;
        }
        return elegido == null ? Optional.empty() : Optional.of(elegido.crear(simbolo, posicion));
    }

    private static IllegalStateException ambiguedad(String familia, char simbolo, Object a, Object b) {
        return new IllegalStateException(String.format(
                "Configuración ambigua: el símbolo '%c' lo reconocen dos creadores de %s (%s y %s)",
                simbolo, familia, a.getClass().getSimpleName(), b.getClass().getSimpleName()));
    }
}
