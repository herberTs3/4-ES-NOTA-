package sokoban.view;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sokoban.controller.Controlador;
import sokoban.dto.LevelOption;

public class LevelSelectPanel extends MenuBackground {

    private static final int COLUMNS = 3;

    private final Controlador controlador;
    private final JPanel grid = new JPanel(new GridLayout(0, COLUMNS, 10, 10));

    public LevelSelectPanel(Controlador controlador) {
        this.controlador = Objects.requireNonNull(controlador);
        grid.setOpaque(false);

        JLabel title = new JLabel("Seleccionar nivel");
        title.setFont(new Font(Palette.FONT_FAMILY, Font.BOLD, 26));
        title.setForeground(Palette.MENU_TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("Terminá un nivel para desbloquear el siguiente");
        hint.setFont(new Font(Palette.FONT_FAMILY, Font.PLAIN, 12));
        hint.setForeground(Palette.MENU_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton back = Buttons.create("Volver", controlador::abrirMenu);
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        grid.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(title);
        content.add(Box.createVerticalStrut(4));
        content.add(hint);
        content.add(Box.createVerticalStrut(26));
        content.add(grid);
        content.add(Box.createVerticalStrut(26));
        content.add(back);
        add(content);
    }

    public void showOptions(List<LevelOption> options) {
        grid.removeAll();
        for (LevelOption option : options) {
            grid.add(levelButton(option));
        }
        revalidate();
        repaint();
    }

    private JButton levelButton(LevelOption option) {
        int number = option.number();
        JButton button = Buttons.create(label(option), () -> controlador.seleccionarNivel(number));
        button.setEnabled(option.unlocked());
        return button;
    }

    private String label(LevelOption option) {
        String name = "Nivel " + option.number();
        if (!option.unlocked()) {
            return name + " (bloqueado)";
        }
        return option.completed() ? name + " (completado)" : name;
    }
}
