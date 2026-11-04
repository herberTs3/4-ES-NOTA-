package sokoban.vista;

import sokoban.controlador.Controlador;
import sokoban.controlador.VistaJuego;
import sokoban.modelo.Tablero;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Objects;

/** Ventana que muestra el nivel cargado y permite pasar de un nivel a otro. Solo habla con el Controlador. */
public class VentanaPrincipal extends JFrame implements VistaJuego {

    private static final Color COLOR_ERROR = new Color(0xb3261e);

    private final Controlador controlador;
    private final VistaTablero vista = new VistaTablero();
    private final JButton botonAnterior = new JButton("« Anterior");
    private final JButton botonSiguiente = new JButton("Siguiente »");
    private final JButton botonRecargar = new JButton("Recargar");
    private final JLabel etiquetaNivel = new JLabel("Nivel");
    private final JLabel etiquetaEstado = new JLabel(" ");
    private final Color colorEstadoNormal = etiquetaEstado.getForeground();

    public VentanaPrincipal(Controlador controlador) {
        super("Sokoban - Visor de niveles");
        this.controlador = Objects.requireNonNull(controlador);

        etiquetaNivel.setFont(etiquetaNivel.getFont().deriveFont(Font.BOLD, 15f));
        etiquetaNivel.setPreferredSize(new Dimension(90, 24));
        etiquetaNivel.setHorizontalAlignment(JLabel.CENTER);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        barra.add(botonAnterior);
        barra.add(etiquetaNivel);
        barra.add(botonSiguiente);
        barra.add(botonRecargar);

        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(6, 12, 8, 12));

        add(barra, BorderLayout.NORTH);
        add(vista, BorderLayout.CENTER);
        add(etiquetaEstado, BorderLayout.SOUTH);

        botonAnterior.addActionListener(e -> controlador.anteriorNivel());
        botonSiguiente.addActionListener(e -> controlador.siguienteNivel());
        botonRecargar.addActionListener(e -> controlador.recargarNivel());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(420, 360));
        pack();
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------- VistaJuego (llamado por el Controlador)

    @Override
    public void mostrarTablero(Tablero tablero) {
        vista.setTablero(tablero);
        mostrarEstado(String.format("%d filas x %d columnas, %d cajas, %d destinos",
                tablero.getFilas(), tablero.getColumnas(), tablero.getCajas().size(),
                tablero.contarDestinos()), false);
    }

    @Override
    public void mostrarError(int numeroDeNivel, String motivo) {
        mostrarEstado("No se pudo cargar el nivel " + numeroDeNivel + ": " + motivo, true);
    }

    @Override
    public void actualizarNavegacion(int nivelActual, boolean hayNivelAnterior) {
        etiquetaNivel.setText("Nivel " + nivelActual);
        botonAnterior.setEnabled(hayNivelAnterior);
    }

    private void mostrarEstado(String texto, boolean error) {
        etiquetaEstado.setForeground(error ? COLOR_ERROR : colorEstadoNormal);
        etiquetaEstado.setText(texto);
    }
}
