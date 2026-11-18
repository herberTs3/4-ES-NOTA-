package sokoban.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sokoban.controller.Controlador;

public class MenuPanel extends MenuBackground {

    private static final int BUTTON_WIDTH = 260;
    private static final int BUTTON_HEIGHT = 46;

    public MenuPanel(Controlador controlador) {
        Objects.requireNonNull(controlador);

        TitleCrates title = new TitleCrates();
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(title);
        content.add(Box.createVerticalStrut(16));
        content.add(label("UADE GRUPO 4 ES NOTA", Font.BOLD, 15, Palette.MENU_TEXT));
        content.add(Box.createVerticalStrut(4));
        content.add(label("Empujá, deslizá y abrí tu camino", Font.PLAIN, 13, Palette.MENU_MUTED));
        content.add(Box.createVerticalStrut(30));
        content.add(sizedButton(Buttons.createPrimary("Jugar", controlador::jugar)));
        content.add(Box.createVerticalStrut(10));
        content.add(sizedButton(Buttons.createLarge("Seleccionar nivel", controlador::abrirSelector)));
        content.add(Box.createVerticalStrut(10));
        content.add(sizedButton(Buttons.createLarge("Créditos", controlador::abrirCreditos)));
        content.add(Box.createVerticalStrut(30));
        content.add(label("Proceso de Desarrollo de Software", Font.PLAIN, 12, Palette.MENU_MUTED));
        content.add(Box.createVerticalStrut(2));
        content.add(label("Profesor: Jonathan Pepe", Font.PLAIN, 12, Palette.MENU_MUTED));
        add(content);
    }

    private JLabel label(String text, int style, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Palette.FONT_FAMILY, style, size));
        label.setForeground(color);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, label.getPreferredSize().height));
        return label;
    }

    private JButton sizedButton(JButton button) {
        Dimension size = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }
}
