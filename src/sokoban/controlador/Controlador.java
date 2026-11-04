package sokoban.controlador;

import sokoban.carga.CargaDeNivelException;
import sokoban.carga.CargadorDeNivel;
import sokoban.modelo.Tablero;

import java.util.Objects;

/**
 * Intermediario entre la vista y el modelo.
 *
 * <ul>
 *   <li>La vista le informa lo que hizo el usuario ({@link #siguienteNivel()}, {@link #recargarNivel()}...)
 *       y no conoce al {@link CargadorDeNivel} ni decide cómo se arma un nivel.</li>
 *   <li>El modelo ({@code Tablero}, terrenos, cajas) no sabe que existen ni el controlador ni la vista.</li>
 *   <li>El controlador le avisa a la vista qué mostrar a través de {@link VistaJuego}.</li>
 * </ul>
 */
public class Controlador {

    private static final int PRIMER_NIVEL = 1;

    private final CargadorDeNivel cargador;
    private VistaJuego vista;
    private int nivelActual;

    public Controlador(CargadorDeNivel cargador, int nivelInicial) {
        this.cargador = Objects.requireNonNull(cargador, "cargador");
        this.nivelActual = nivelInicial;
    }

    /** Registra la vista que será notificada. Debe llamarse antes de {@link #iniciar()}. */
    public void setVista(VistaJuego vista) {
        this.vista = Objects.requireNonNull(vista, "vista");
    }

    /** Carga el nivel inicial. */
    public void iniciar() {
        cargarNivel(nivelActual);
    }

    public void siguienteNivel() {
        cargarNivel(nivelActual + 1);
    }

    public void anteriorNivel() {
        cargarNivel(nivelActual - 1);
    }

    public void recargarNivel() {
        cargarNivel(nivelActual);
    }

    public int getNivelActual() {
        return nivelActual;
    }

    public boolean hayNivelAnterior() {
        return nivelActual > PRIMER_NIVEL;
    }

    private void cargarNivel(int numeroDeNivel) {
        if (vista == null) {
            throw new IllegalStateException("Falta registrar la vista (setVista) antes de usar el controlador");
        }
        try {
            Tablero tablero = cargador.cargarNivel(numeroDeNivel);
            nivelActual = numeroDeNivel;
            vista.mostrarTablero(tablero);
        } catch (CargaDeNivelException e) {
            vista.mostrarError(numeroDeNivel, e.getMessage());
        }
        vista.actualizarNavegacion(nivelActual, hayNivelAnterior());
    }
}
