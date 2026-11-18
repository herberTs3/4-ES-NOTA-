package sokoban.view;

import java.awt.Component;
import java.awt.Font;
import java.util.List;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sokoban.controller.Controlador;

public class CreditsPanel extends MenuBackground {

    private static final List<String> MEMBERS = List.of(
            "Tiago Muller - 1205306",
            "Luca Gribaudo - 1208805",
            "Herbert Muller Wilcke - 1188358",
            "Agustin Iacuzzi - 1226543");

    public CreditsPanel(Controlador controlador) {
        Objects.requireNonNull(controlador);

        JLabel title = new JLabel("Créditos");
        title.setFont(new Font(Palette.FONT_FAMILY, Font.BOLD, 26));
        title.setForeground(Palette.MENU_TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(title);
        content.add(Box.createVerticalStrut(24));
        for (String member : MEMBERS) {
            content.add(memberLabel(member));
            content.add(Box.createVerticalStrut(8));
        }
        content.add(Box.createVerticalStrut(10));
        content.add(memberLabel("Profesor: Jonathan Pepe"));
        content.add(Box.createVerticalStrut(24));
        JButton back = Buttons.create("Volver", controlador::abrirMenu);
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(back);
        add(content);
    }

    private JLabel memberLabel(String member) {
        JLabel label = new JLabel(member);
        label.setFont(new Font(Palette.FONT_FAMILY, Font.PLAIN, 15));
        label.setForeground(Palette.MENU_TEXT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
}
