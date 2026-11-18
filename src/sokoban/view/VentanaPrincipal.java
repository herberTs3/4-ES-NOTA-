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
import sokoban.model.Direction;
import sokoban.model.puntaje.ResultadoPuntaje;

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
    private int ultimoNivelConSnapshot = -1;
    private int ultimosMovimientos = -1;

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

        registrarTeclaDireccion("UP", Direction.UP, controlador::moverArriba);
        registrarTeclaDireccion("DOWN", Direction.DOWN, controlador::moverAbajo);
        registrarTeclaDireccion("LEFT", Direction.LEFT, controlador::moverIzquierda);
        registrarTeclaDireccion("RIGHT", Direction.RIGHT, controlador::moverDerecha);
        registrarTeclaDireccion("ctrl UP", Direction.UP, controlador::romperMuroArriba);
        registrarTeclaDireccion("ctrl DOWN", Direction.DOWN, controlador::romperMuroAbajo);
        registrarTeclaDireccion("ctrl LEFT", Direction.LEFT, controlador::romperMuroIzquierda);
        registrarTeclaDireccion("ctrl RIGHT", Direction.RIGHT, controlador::romperMuroDerecha);
        registrarTeclaDireccion("shift UP", Direction.UP, controlador::jalarArriba);
        registrarTeclaDireccion("shift DOWN", Direction.DOWN, controlador::jalarAbajo);
        registrarTeclaDireccion("shift LEFT", Direction.LEFT, controlador::jalarIzquierda);
        registrarTeclaDireccion("shift RIGHT", Direction.RIGHT, controlador::jalarDerecha);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(640, 480));
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void onGameChanged(GameSnapshot snapshot) {
        boolean huboMovimiento = snapshot.level() == ultimoNivelConSnapshot
                && snapshot.moves() == ultimosMovimientos + 1;
        vista.setTablero(snapshot.board(), snapshot.powerUpsEnMapa(), snapshot.powerUpsActivos(), huboMovimiento);
        ultimoNivelConSnapshot = snapshot.level();
        ultimosMovimientos = snapshot.moves();
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
    public void mostrarNivelCompletado(GameSnapshot resumen, ResultadoPuntaje puntaje) {
        String detalle = String.format(
                "Nivel %d completado.%n%nPuntaje: %d / 1000%nPolítica: %s%n%n"
                + "Movimientos: %d%nEmpujes: %d%nUsos de deshacer: %d",
                resumen.level(), puntaje.puntos(), puntaje.politicaAplicada(),
                resumen.moves(), resumen.pushes(), resumen.undoUses());
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

    /** La orientacion es visual y no requiere nuevos atributos en Jugador ni en Game. */
    private void registrarTeclaDireccion(String tecla, Direction direccion, Runnable accion) {
        registrarTecla(tecla, () -> {
            vista.orientarJugador(direccion);
            accion.run();
        });
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
