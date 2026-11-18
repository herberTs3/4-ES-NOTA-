package sokoban.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class MenuBackground extends JPanel {

    private static final int TILE = 64;
    private static final int INSET = 8;
    private static final double FREE_AREA_START = 0.18;
    private static final double FREE_AREA_END = 0.84;

    public MenuBackground() {
        super(new GridBagLayout());
        setBackground(Palette.MENU_BACKGROUND);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawGrid(g2);
            drawDecorations(g2);
        } finally {
            g2.dispose();
        }
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(Palette.MENU_GRID);
        for (int x = 0; x < getWidth(); x += TILE) {
            g2.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += TILE) {
            g2.drawLine(0, y, getWidth(), y);
        }
    }

    private void drawDecorations(Graphics2D g2) {
        for (int row = 0; row * TILE < getHeight(); row++) {
            for (int column = 0; column * TILE < getWidth(); column++) {
                if ((column * 5 + row * 3) % 4 == 0 && isOutsideFreeArea(column)) {
                    drawTile(g2, column, row);
                }
            }
        }
    }

    private boolean isOutsideFreeArea(int column) {
        double center = (column + 0.5) * TILE / getWidth();
        return center < FREE_AREA_START || center > FREE_AREA_END;
    }

    private void drawTile(Graphics2D g2, int column, int row) {
        int x = column * TILE + INSET;
        int y = row * TILE + INSET;
        int size = TILE - 2 * INSET;
        switch ((column + row * 2) % 4) {
            case 0 -> drawCrate(g2, Palette.titleColor(column + row), x, y, size);
            case 1 -> drawIce(g2, x, y, size);
            case 2 -> drawGoal(g2, x, y, size);
            default -> drawCrate(g2, Palette.BOX, x, y, size);
        }
    }

    private void drawCrate(Graphics2D g2, Color color, int x, int y, int size) {
        g2.setColor(color);
        g2.fillRoundRect(x, y, size, size, 10, 10);
        g2.setColor(color.darker());
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x + 7, y + 7, size - 14, size - 14, 6, 6);
    }

    private void drawIce(Graphics2D g2, int x, int y, int size) {
        g2.setColor(Palette.ICE);
        g2.fillRoundRect(x, y, size, size, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x + 11, y + 23, x + 23, y + 11);
        g2.drawLine(x + 25, y + 37, x + 37, y + 25);
    }

    private void drawGoal(Graphics2D g2, int x, int y, int size) {
        int center = size / 2;
        g2.setColor(Palette.GOAL);
        g2.setStroke(new BasicStroke(3f));
        g2.drawOval(x + center - 13, y + center - 13, 26, 26);
        g2.fillOval(x + center - 4, y + center - 4, 8, 8);
    }
}
