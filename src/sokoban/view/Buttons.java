package sokoban.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;

final class Buttons {

    private Buttons() {
    }

    static JButton create(String text, Runnable action) {
        JButton button = base(text, action, Palette.SURFACE, Palette.SURFACE_HOVER);
        button.setFont(new Font(Palette.FONT_FAMILY, Font.PLAIN, 12));
        button.setForeground(Palette.INK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Palette.LINE),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        return button;
    }

    static JButton createLarge(String text, Runnable action) {
        JButton button = create(text, action);
        button.setFont(new Font(Palette.FONT_FAMILY, Font.PLAIN, 15));
        return button;
    }

    static JButton createPrimary(String text, Runnable action) {
        JButton button = base(text, action, Palette.GOAL, Palette.GOAL_HOVER);
        button.setFont(new Font(Palette.FONT_FAMILY, Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return button;
    }

    private static JButton base(String text, Runnable action, Color normal, Color hover) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.setBackground(normal);
        button.getModel().addChangeListener(e -> highlight(button, normal, hover));
        button.addActionListener(e -> action.run());
        return button;
    }

    private static void highlight(JButton button, Color normal, Color hover) {
        boolean active = button.isEnabled() && button.getModel().isRollover();
        button.setBackground(active ? hover : normal);
    }
}
