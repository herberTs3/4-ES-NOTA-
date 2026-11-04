package sokoban.view;

import java.awt.BorderLayout;
import java.awt.Color;
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

    private static final Color COLOR_ERROR = new Color(0xb3261e);

    private final VistaTablero vista = new VistaTablero();
    private final JButton botonAnterior = new JButton("« Anterior");
    private final JButton botonSiguiente = new JButton("Siguiente »");
    private final JButton botonReiniciar = new JButton("Reiniciar");
    private final JLabel etiquetaNivel = new JLabel("Nivel");
    private final JLabel etiquetaEstado = new JLabel(" ");
    private final Color colorEstadoNormal = etiquetaEstado.getForeground();

    public VentanaPrincipal(Controlador controlador) {
        super("Sokoban");
        Objects.requireNonNull(controlador);

        etiquetaNivel.setFont(etiquetaNivel.getFont().deriveFont(Font.BOLD, 15f));
        etiquetaNivel.setPreferredSize(new Dimension(90, 24));
        etiquetaNivel.setHorizontalAlignment(JLabel.CENTER);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        barra.add(botonAnterior);
        barra.add(etiquetaNivel);
        barra.add(botonSiguiente);
        barra.add(botonReiniciar);

        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(6, 12, 8, 12));

        add(barra, BorderLayout.NORTH);
        add(vista, BorderLayout.CENTER);
        add(etiquetaEstado, BorderLayout.SOUTH);

        configurarBoton(botonAnterior, controlador::anteriorNivel);
        configurarBoton(botonSiguiente, controlador::siguienteNivel);
        configurarBoton(botonReiniciar, controlador::recargarNivel);

        registrarTecla("UP", controlador::moverArriba);
        registrarTecla("DOWN", controlador::moverAbajo);
        registrarTecla("LEFT", controlador::moverIzquierda);
        registrarTecla("RIGHT", controlador::moverDerecha);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(420, 360));
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void onGameChanged(GameSnapshot snapshot) {
        vista.setTablero(snapshot.board());
        if (snapshot.status() == GameStatus.UNSOLVABLE) {
            mostrarEstado("Se rompió una caja necesaria: el nivel no tiene solución. Tocá Reiniciar.", true);
        } else {
            mostrarEstado(textoContadores(snapshot), false);
        }
    }

    @Override
    public void mostrarError(int numeroDeNivel, String motivo) {
        mostrarEstado("No se pudo cargar el nivel " + numeroDeNivel + ": " + motivo, true);
    }

    @Override
    public void actualizarNavegacion(int nivelActual, boolean hayNivelAnterior, boolean hayNivelSiguiente) {
        etiquetaNivel.setText("Nivel " + nivelActual);
        botonAnterior.setEnabled(hayNivelAnterior);
        botonSiguiente.setEnabled(hayNivelSiguiente);
    }

    @Override
    public void mostrarNivelCompletado(GameSnapshot resumen) {
        JOptionPane.showMessageDialog(this, "Nivel " + resumen.level() + " completado.\n" + textoContadores(resumen),
                "¡Nivel completado!", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void mostrarJuegoTerminado() {
        JOptionPane.showMessageDialog(this, "Completaste todos los niveles.", "¡Juego terminado!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String textoContadores(GameSnapshot snapshot) {
        return String.format("Movimientos: %d   Empujes: %d", snapshot.moves(), snapshot.pushes());
    }

    private void configurarBoton(JButton boton, Runnable accion) {
        boton.setFocusable(false);
        boton.addActionListener(e -> accion.run());
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
        etiquetaEstado.setForeground(error ? COLOR_ERROR : colorEstadoNormal);
        etiquetaEstado.setText(texto);
    }
}
