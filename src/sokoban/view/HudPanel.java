package sokoban.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sokoban.dto.BoardSnapshot;
import sokoban.dto.GameSnapshot;

public class HudPanel extends JPanel {

    private static final String TITLE = "SOKOBAN";
    private static final String TEAM = "UADE GRUPO 4 ES NOTA";
    private static final String EMPTY_VALUE = "-";
    private static final int LABEL_RIGHT_PADDING = 8;

    private final JLabel level = valueLabel();
    private final JLabel moves = valueLabel();
    private final JLabel pushes = valueLabel();
    private final JLabel boxes = valueLabel();
    private final JLabel undos = valueLabel();

    public HudPanel() {
        super(new BorderLayout());
        setBackground(Palette.SURFACE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Palette.LINE),
                BorderFactory.createEmptyBorder(14, 22, 14, 22)));
        add(brand(), BorderLayout.WEST);
        add(stats(), BorderLayout.EAST);
    }

    public void showLevel(int number) {
        level.setText(String.valueOf(number));
    }

    public void update(GameSnapshot snapshot) {
        BoardSnapshot board = snapshot.board();
        showLevel(snapshot.level());
        moves.setText(String.valueOf(snapshot.moves()));
        pushes.setText(String.valueOf(snapshot.pushes()));
        boxes.setText(board.countBoxesOnGoals() + "/" + board.countGoals());
        undos.setText(String.valueOf(snapshot.remainingUndos()));
    }

    private JPanel brand() {
        JLabel title = new JLabel(TITLE);
        title.setFont(new Font(Palette.FONT_FAMILY, Font.BOLD, 20));
        title.setForeground(Palette.INK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel team = new JLabel(TEAM);
        team.setFont(new Font(Palette.FONT_FAMILY, Font.PLAIN, 11));
        team.setForeground(Palette.MUTED);
        team.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.add(title);
        brand.add(team);
        return brand;
    }

    private JPanel stats() {
        JPanel stats = new JPanel(new GridLayout(1, 5, 18, 0));
        stats.setOpaque(false);
        stats.add(stat("Nivel", level));
        stats.add(stat("Movimientos", moves));
        stats.add(stat("Empujes", pushes));
        stats.add(stat("Cajas", boxes));
        stats.add(stat("Deshacer", undos));
        return stats;
    }

    private JPanel stat(String name, JLabel value) {
        JLabel label = new JLabel(name);
        label.setFont(new Font(Palette.FONT_FAMILY, Font.PLAIN, 11));
        label.setForeground(Palette.MUTED);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, LABEL_RIGHT_PADDING));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel stat = new JPanel();
        stat.setOpaque(false);
        stat.setLayout(new BoxLayout(stat, BoxLayout.Y_AXIS));
        stat.add(value);
        stat.add(label);
        return stat;
    }

    private static JLabel valueLabel() {
        JLabel label = new JLabel(EMPTY_VALUE);
        label.setFont(new Font(Palette.FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(Palette.INK);
        return label;
    }
}
