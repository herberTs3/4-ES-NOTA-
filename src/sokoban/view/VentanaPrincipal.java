package sokoban.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
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
import sokoban.dto.EstadoPowerUp;
import sokoban.dto.GameSnapshot;
import sokoban.dto.GameStatus;
import sokoban.dto.LevelOption;
import sokoban.model.event.GameListener;

public class VentanaPrincipal extends JFrame implements VistaJuego, GameListener {

    private static final String WINDOW_TITLE = "Sokoban - UADE GRUPO 4 ES NOTA";
    private static final String CONTROLS_HINT = "Usá las flechas para mover";
    private static final String SCREEN_MENU = "menu";
    private static final String SCREEN_LEVELS = "levels";
    private static final String SCREEN_CREDITS = "credits";
    private static final String SCREEN_GAME = "game";

    private final CardLayout pantallas = new CardLayout();
    private final JPanel contenedor = new JPanel(pantallas);
    private final HudPanel hud = new HudPanel();
    private final VistaTablero vista = new VistaTablero();
    private final LevelSelectPanel selector;
    private final JButton botonDeshacer;
    private final JLabel etiquetaEstado = new JLabel(CONTROLS_HINT);
    private boolean jugando;

    public VentanaPrincipal(Controlador controlador) {
        super(WINDOW_TITLE);
        Objects.requireNonNull(controlador);
        selector = new LevelSelectPanel(controlador);
        botonDeshacer = Buttons.create("Deshacer", controlador::deshacer);
        botonDeshacer.setEnabled(false);

        contenedor.add(new MenuPanel(controlador), SCREEN_MENU);
        contenedor.add(selector, SCREEN_LEVELS);
        contenedor.add(new CreditsPanel(controlador), SCREEN_CREDITS);
        contenedor.add(pantallaDeJuego(controlador), SCREEN_GAME);
        add(contenedor, BorderLayout.CENTER);

        registrarTecla("UP", controlador::moverArriba);
        registrarTecla("DOWN", controlador::moverAbajo);
        registrarTecla("LEFT", controlador::moverIzquierda);
        registrarTecla("RIGHT", controlador::moverDerecha);
        registrarTecla("ctrl UP", controlador::romperMuroArriba);
        registrarTecla("ctrl DOWN", controlador::romperMuroAbajo);
        registrarTecla("ctrl LEFT", controlador::romperMuroIzquierda);
        registrarTecla("ctrl RIGHT", controlador::romperMuroDerecha);
        registrarTecla("shift UP", controlador::jalarArriba);
        registrarTecla("shift DOWN", controlador::jalarAbajo);
        registrarTecla("shift LEFT", controlador::jalarIzquierda);
        registrarTecla("shift RIGHT", controlador::jalarDerecha);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(640, 480));
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void onGameChanged(GameSnapshot snapshot) {
        vista.setTablero(snapshot.board(), snapshot.powerUpsEnMapa());
        hud.update(snapshot);
        botonDeshacer.setEnabled(snapshot.canUndo());
        if (snapshot.status() == GameStatus.UNSOLVABLE) {
            mostrarEstado("Se rompió una caja necesaria: deshacé o reiniciá.", true);
        } else {
            mostrarEstado(textoDeControles(snapshot.powerUpsActivos()), false);
        }
    }

    private String textoDeControles(List<EstadoPowerUp> poderes) {
        if (poderes.isEmpty()) {
            return CONTROLS_HINT;
        }
        List<String> partes = new ArrayList<>();
        for (EstadoPowerUp poder : poderes) {
            partes.add(switch (poder.tipo()) {
                case MARTILLO -> String.format("Ctrl: martillo (%d turnos, %d usos)",
                        poder.turnosRestantes(), poder.usosRestantes());
                case SUPERFUERZA -> String.format("Shift: jalar (%d turnos)", poder.turnosRestantes());
            });
        }
        return String.join("  ·  ", partes);
    }

    @Override
    public void mostrarMenu() {
        mostrarPantalla(SCREEN_MENU);
    }

    @Override
    public void mostrarSelector(List<LevelOption> opciones) {
        selector.showOptions(opciones);
        mostrarPantalla(SCREEN_LEVELS);
    }

    @Override
    public void mostrarCreditos() {
        mostrarPantalla(SCREEN_CREDITS);
    }

    @Override
    public void mostrarJuego() {
        mostrarPantalla(SCREEN_GAME);
    }

    @Override
    public void mostrarError(int numeroDeNivel, String motivo) {
        JOptionPane.showMessageDialog(this, "No se pudo cargar el nivel " + numeroDeNivel + ": " + motivo, "Error",
                JOptionPane.ERROR_MESSAGE);
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

    private JPanel pantallaDeJuego(Controlador controlador) {
        JPanel pantalla = new JPanel(new BorderLayout());
        pantalla.add(hud, BorderLayout.NORTH);
        pantalla.add(vista, BorderLayout.CENTER);
        pantalla.add(barraInferior(controlador), BorderLayout.SOUTH);
        return pantalla;
    }

    private JPanel barraInferior(Controlador controlador) {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);
        botones.add(Buttons.create("Menú", controlador::abrirMenu));
        botones.add(botonDeshacer);
        botones.add(Buttons.create("Reiniciar", controlador::recargarNivel));

        etiquetaEstado.setFont(new Font(Palette.FONT_FAMILY, Font.PLAIN, 12));
        etiquetaEstado.setForeground(Palette.MUTED);

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Palette.SURFACE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Palette.LINE),
                BorderFactory.createEmptyBorder(10, 14, 10, 22)));
        barra.add(botones, BorderLayout.WEST);
        etiquetaEstado.setHorizontalAlignment(JLabel.RIGHT);
        barra.add(etiquetaEstado, BorderLayout.CENTER);
        return barra;
    }

    private void mostrarPantalla(String pantalla) {
        jugando = SCREEN_GAME.equals(pantalla);
        pantallas.show(contenedor, pantalla);
    }

    private void registrarTecla(String tecla, Runnable accion) {
        JComponent raiz = getRootPane();
        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla), tecla);
        raiz.getActionMap().put(tecla, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                if (jugando) {
                    accion.run();
                }
            }
        });
    }

    private void mostrarEstado(String texto, boolean error) {
        etiquetaEstado.setForeground(error ? Palette.ERROR : Palette.MUTED);
        etiquetaEstado.setText(texto);
    }
}
