package sokoban.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import sokoban.controller.Controlador;
import sokoban.controller.VistaJuego;
import sokoban.dto.GameSnapshot;
import sokoban.dto.GameStatus;
import sokoban.model.event.GameListener;

public class VentanaPrincipal extends JFrame implements VistaJuego, GameListener {

    private static final String WINDOW_TITLE = "Sokoban - UADE GRUPO 4 ES NOTA";
    private static final String CONTROLS_HINT = "Usá las flechas para mover";

    private final HudPanel hud = new HudPanel();
    private final VistaTablero vista = new VistaTablero();
    private final JButton botonAnterior = new JButton("Anterior");
    private final JButton botonSiguiente = new JButton("Siguiente");
    private final JButton botonDeshacer = new JButton("Deshacer");
    private final JButton botonReiniciar = new JButton("Reiniciar");
    private final JLabel etiquetaEstado = new JLabel(CONTROLS_HINT);

    public VentanaPrincipal(Controlador controlador) {
        super(WINDOW_TITLE);
        Objects.requireNonNull(controlador);

        add(hud, BorderLayout.NORTH);
        add(vista, BorderLayout.CENTER);
        add(barraInferior(), BorderLayout.SOUTH);

        configurarBoton(botonAnterior, controlador::anteriorNivel);
        configurarBoton(botonSiguiente, controlador::siguienteNivel);
        configurarBoton(botonDeshacer, controlador::deshacer);
        configurarBoton(botonReiniciar, controlador::recargarNivel);
        botonDeshacer.setEnabled(false);

        registrarTecla("UP", controlador::moverArriba);
        registrarTecla("DOWN", controlador::moverAbajo);
        registrarTecla("LEFT", controlador::moverIzquierda);
        registrarTecla("RIGHT", controlador::moverDerecha);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(640, 480));
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void onGameChanged(GameSnapshot snapshot) {
        vista.setTablero(snapshot.board());
        hud.update(snapshot);
        botonDeshacer.setEnabled(snapshot.canUndo());
        if (snapshot.status() == GameStatus.UNSOLVABLE) {
            mostrarEstado("Se rompió una caja necesaria: deshacé o reiniciá.", true);
        } else {
            mostrarEstado(CONTROLS_HINT, false);
        }
    }

    @Override
    public void mostrarError(int numeroDeNivel, String motivo) {
        mostrarEstado("No se pudo cargar el nivel " + numeroDeNivel + ": " + motivo, true);
    }

    @Override
    public void actualizarNavegacion(int nivelActual, boolean hayNivelAnterior, boolean hayNivelSiguiente) {
        hud.showLevel(nivelActual);
        botonAnterior.setEnabled(hayNivelAnterior);
        botonSiguiente.setEnabled(hayNivelSiguiente);
    }

    @Override
    public void mostrarNivelCompletado(GameSnapshot resumen) {
        String detalle = String.format("Nivel %d completado.%nMovimientos: %d%nEmpujes: %d%nUsos de deshacer: %d",
                resumen.level(), resumen.moves(), resumen.pushes(), resumen.undoUses());
        JOptionPane.showMessageDialog(this, detalle, "¡Nivel completado!", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarJuegoTerminado() {
        JOptionPane.showMessageDialog(this, "Completaste todos los niveles.", "¡Juego terminado!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel barraInferior() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);
        botones.add(botonAnterior);
        botones.add(botonSiguiente);
        botones.add(botonDeshacer);
        botones.add(botonReiniciar);

        etiquetaEstado.setFont(new Font(Palette.FONT_FAMILY, Font.PLAIN, 12));
        etiquetaEstado.setForeground(Palette.MUTED);

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Palette.SURFACE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Palette.LINE),
                BorderFactory.createEmptyBorder(10, 14, 10, 22)));
        barra.add(botones, BorderLayout.WEST);
        barra.add(etiquetaEstado, BorderLayout.EAST);
        return barra;
    }

    private void configurarBoton(JButton boton, Runnable accion) {
        boton.setFocusable(false);
        boton.setFocusPainted(false);
        boton.setRolloverEnabled(true);
        boton.setFont(new Font(Palette.FONT_FAMILY, Font.PLAIN, 12));
        boton.setForeground(Palette.INK);
        boton.setBackground(Palette.SURFACE);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Palette.LINE),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        boton.getModel().addChangeListener(e -> resaltarBoton(boton));
        boton.addActionListener(e -> accion.run());
    }

    private void resaltarBoton(JButton boton) {
        boolean resaltado = boton.isEnabled() && boton.getModel().isRollover();
        boton.setBackground(resaltado ? Palette.SURFACE_HOVER : Palette.SURFACE);
    }

    private void registrarTecla(String tecla, Runnable accion) {
        JComponent raiz = getRootPane();
        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla), tecla);
        raiz.getActionMap().put(tecla, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                accion.run();
            }
        });
    }

    private void mostrarEstado(String texto, boolean error) {
        etiquetaEstado.setForeground(error ? Palette.ERROR : Palette.MUTED);
        etiquetaEstado.setText(texto);
    }
}
