package sokoban.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class TitleCrates extends JComponent {

    private static final String TITLE = "SOKOBAN";
    private static final int CRATE = 58;
    private static final int GAP = 6;
    private static final int ARC = 12;
    private static final int SHADOW = 4;

    public TitleCrates() {
        int width = TITLE.length() * CRATE + (TITLE.length() - 1) * GAP;
        Dimension size = new Dimension(width, CRATE + SHADOW);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(new Font(Palette.FONT_FAMILY, Font.BOLD, 32));
            for (int i = 0; i < TITLE.length(); i++) {
                drawCrate(g2, i);
            }
        } finally {
            g2.dispose();
        }
    }

    private void drawCrate(Graphics2D g2, int index) {
        int x = index * (CRATE + GAP);
        g2.setColor(Palette.SHADOW);
        g2.fillRoundRect(x, SHADOW, CRATE, CRATE, ARC, ARC);
        g2.setColor(Palette.titleColor(index));
        g2.fillRoundRect(x, 0, CRATE, CRATE, ARC, ARC);
        String letter = String.valueOf(TITLE.charAt(index));
        FontMetrics metrics = g2.getFontMetrics();
        g2.setColor(Color.WHITE);
        g2.drawString(letter, x + (CRATE - metrics.stringWidth(letter)) / 2,
                (CRATE + metrics.getAscent() - metrics.getDescent()) / 2);
    }
}
