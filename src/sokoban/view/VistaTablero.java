package sokoban.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import sokoban.dto.BoardSnapshot;
import sokoban.dto.BoxData;
import sokoban.dto.CellPosition;
import sokoban.dto.TileData;
import sokoban.dto.TileType;

public class VistaTablero extends JPanel {

    private static final int MARGEN = 28;
    private static final int LADO_MINIMO = 8;
    private static final int LADO_MAXIMO = 72;
    private static final int RESISTENCIA_CRITICA = 1;

    private BoardSnapshot tablero;

    public VistaTablero() {
        setBackground(Palette.BACKGROUND);
        setPreferredSize(new Dimension(760, 540));
    }

    public void setTablero(BoardSnapshot tablero) {
        this.tablero = tablero;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (tablero == null) {
                dibujarTexto(g2, "Sin nivel cargado", getWidth() / 2, getHeight() / 2, 16, Palette.MUTED);
            } else {
                dibujarTablero(g2);
            }
        } finally {
            g2.dispose();
        }
    }

    private void dibujarTablero(Graphics2D g2) {
        int filas = tablero.rows();
        int columnas = tablero.columns();
        int lado = Math.min(LADO_MAXIMO, Math.max(LADO_MINIMO,
                Math.min((getWidth() - 2 * MARGEN) / columnas, (getHeight() - 2 * MARGEN) / filas)));
        int x0 = (getWidth() - lado * columnas) / 2;
        int y0 = (getHeight() - lado * filas) / 2;

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                dibujarTerreno(g2, f, c, x0 + c * lado, y0 + f * lado, lado);
            }
        }
        for (BoxData caja : tablero.boxes()) {
            CellPosition p = caja.position();
            dibujarCaja(g2, caja, x0 + p.column() * lado, y0 + p.row() * lado, lado);
        }
        CellPosition pj = tablero.player();
        dibujarJugador(g2, x0 + pj.column() * lado, y0 + pj.row() * lado, lado);
    }

    private void dibujarTerreno(Graphics2D g2, int fila, int columna, int x, int y, int t) {
        TileData celda = tablero.tileAt(fila, columna);
        switch (celda.type()) {
            case WALL -> dibujarMuroSiEsVisible(g2, fila, columna, x, y, t);
            case FLOOR -> dibujarPiso(g2, Palette.FLOOR, Palette.FLOOR_LINE, x, y, t);
            case GOAL -> {
                dibujarPiso(g2, Palette.FLOOR, Palette.FLOOR_LINE, x, y, t);
                dibujarDestino(g2, x, y, t);
            }
            case ICE -> {
                dibujarPiso(g2, Palette.ICE, Palette.ICE_LINE, x, y, t);
                dibujarBrillos(g2, x, y, t);
            }
            case LOCK -> {
                dibujarPiso(g2, Palette.FLOOR, Palette.FLOOR_LINE, x, y, t);
                dibujarCerrojo(g2, celda, x, y, t);
            }
            case DOOR -> dibujarPuerta(g2, celda, fila, columna, x, y, t);
        }
    }

    private void dibujarPiso(Graphics2D g2, Color relleno, Color linea, int x, int y, int t) {
        g2.setColor(relleno);
        g2.fillRect(x, y, t, t);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(linea);
        g2.drawRect(x, y, t - 1, t - 1);
    }

    private void dibujarMuroSiEsVisible(Graphics2D g2, int fila, int columna, int x, int y, int t) {
        if (muroVisible(fila, columna)) {
            dibujarMuro(g2, fila, columna, x, y, t);
        }
    }

    private boolean muroVisible(int fila, int columna) {
        for (int df = -1; df <= 1; df++) {
            for (int dc = -1; dc <= 1; dc++) {
                boolean esVecino = df != 0 || dc != 0;
                if (esVecino && estaDentro(fila + df, columna + dc) && !esMuro(fila + df, columna + dc)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean estaDentro(int fila, int columna) {
        return fila >= 0 && fila < tablero.rows() && columna >= 0 && columna < tablero.columns();
    }

    private boolean esMuro(int fila, int columna) {
        return tablero.tileAt(fila, columna).type() == TileType.WALL;
    }

    private void dibujarMuro(Graphics2D g2, int fila, int columna, int x, int y, int t) {
        g2.setColor(Palette.WALL);
        g2.fillRect(x, y, t, t);
        if (estaDentro(fila + 1, columna) && !esMuro(fila + 1, columna)) {
            int canto = Math.max(2, u(t, 0.14));
            g2.setColor(Palette.WALL_EDGE);
            g2.fillRect(x, y + t - canto, t, canto);
        }
    }

    private void dibujarBrillos(Graphics2D g2, int x, int y, int t) {
        g2.setStroke(new BasicStroke(Math.max(1.5f, t / 26f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Color.WHITE);
        g2.drawLine(x + u(t, 0.24), y + u(t, 0.46), x + u(t, 0.46), y + u(t, 0.24));
        g2.drawLine(x + u(t, 0.56), y + u(t, 0.76), x + u(t, 0.76), y + u(t, 0.56));
    }

    private void dibujarDestino(Graphics2D g2, int x, int y, int t) {
        int cx = x + t / 2;
        int cy = y + t / 2;
        int r = u(t, 0.18);
        int punto = Math.max(2, u(t, 0.05));
        g2.setStroke(new BasicStroke(Math.max(1.5f, t / 26f)));
        g2.setColor(Palette.GOAL);
        g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);
        g2.fillOval(cx - punto, cy - punto, 2 * punto, 2 * punto);
    }

    private void dibujarCerrojo(Graphics2D g2, TileData celda, int x, int y, int t) {
        Color color = Palette.lockColor(celda.label());
        int cx = x + t / 2;
        int ancho = u(t, 0.46);
        int alto = u(t, 0.34);
        int cuerpoY = y + u(t, 0.44);
        int arco = u(t, 0.28);
        int elevacion = celda.open() ? u(t, 0.08) : 0;

        g2.setStroke(new BasicStroke(Math.max(1.5f, t / 18f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(color);
        g2.drawArc(cx - arco / 2, y + u(t, 0.22) - elevacion, arco, u(t, 0.46), 0, 180);
        g2.fillRoundRect(cx - ancho / 2, cuerpoY, ancho, alto, u(t, 0.10), u(t, 0.10));
        dibujarTexto(g2, String.valueOf(celda.label()), cx, cuerpoY + alto / 2, u(t, 0.24), Color.WHITE);
    }

    private void dibujarPuerta(Graphics2D g2, TileData celda, int fila, int columna, int x, int y, int t) {
        Color color = Palette.lockColor(celda.label());
        if (celda.open()) {
            dibujarPiso(g2, Palette.FLOOR, Palette.FLOOR_LINE, x, y, t);
            dibujarEsquinas(g2, color, x, y, t);
            return;
        }
        dibujarMuro(g2, fila, columna, x, y, t);
        int diametro = u(t, 0.52);
        int borde = (t - diametro) / 2;
        g2.setColor(color);
        g2.fillOval(x + borde, y + borde, diametro, diametro);
        dibujarTexto(g2, String.valueOf(celda.label()), x + t / 2, y + t / 2, u(t, 0.30), Color.WHITE);
    }

    private void dibujarEsquinas(Graphics2D g2, Color color, int x, int y, int t) {
        int m = u(t, 0.12);
        int largo = u(t, 0.18);
        int izquierda = x + m;
        int derecha = x + t - m;
        int arriba = y + m;
        int abajo = y + t - m;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(Math.max(2f, t / 20f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawPolyline(new int[]{izquierda, izquierda, izquierda + largo},
                new int[]{arriba + largo, arriba, arriba}, 3);
        g2.drawPolyline(new int[]{derecha - largo, derecha, derecha},
                new int[]{arriba, arriba, arriba + largo}, 3);
        g2.drawPolyline(new int[]{izquierda, izquierda, izquierda + largo},
                new int[]{abajo - largo, abajo, abajo}, 3);
        g2.drawPolyline(new int[]{derecha - largo, derecha, derecha},
                new int[]{abajo, abajo, abajo - largo}, 3);
    }

    private void dibujarCaja(Graphics2D g2, BoxData caja, int x, int y, int t) {
        int m = u(t, 0.1);
        int bx = x + m;
        int by = y + m;
        int l = t - 2 * m;
        g2.setColor(Palette.SHADOW);
        g2.fillRoundRect(bx, by + u(t, 0.05), l, l, u(l, 0.16), u(l, 0.16));
        switch (caja.type()) {
            case NORMAL -> dibujarCajaNormal(g2, caja.inPlace(), bx, by, l);
            case FRAGILE -> dibujarCajaFragil(g2, caja.resistance(), bx, by, l);
            case KEY -> dibujarCajaLlave(g2, bx, by, l);
        }
        if (caja.inPlace()) {
            dibujarMarcaEnLugar(g2, bx, by, l);
        }
    }

    private void dibujarCajaNormal(Graphics2D g2, boolean enLugar, int bx, int by, int l) {
        Color relleno = enLugar ? Palette.BOX_IN_PLACE : Palette.BOX;
        Color linea = enLugar ? Palette.BOX_IN_PLACE_LINE : Palette.BOX_LINE;
        dibujarCuerpoDeCaja(g2, relleno, linea, bx, by, l);
    }

    private void dibujarCuerpoDeCaja(Graphics2D g2, Color relleno, Color linea, int bx, int by, int l) {
        int arco = u(l, 0.16);
        int m = u(l, 0.14);
        g2.setColor(relleno);
        g2.fillRoundRect(bx, by, l, l, arco, arco);
        g2.setColor(linea);
        g2.setStroke(new BasicStroke(Math.max(1f, l / 30f)));
        g2.drawRoundRect(bx + m, by + m, l - 2 * m, l - 2 * m, arco / 2, arco / 2);
    }

    private void dibujarCajaLlave(Graphics2D g2, int bx, int by, int l) {
        dibujarCuerpoDeCaja(g2, Palette.KEY, Palette.KEY_LINE, bx, by, l);
        int cy = by + l / 2;
        int r = u(l, 0.11);
        int anilloX = bx + u(l, 0.34);
        int fin = bx + u(l, 0.72);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(Math.max(1.5f, l / 16f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawOval(anilloX - r, cy - r, 2 * r, 2 * r);
        g2.drawLine(anilloX + r, cy, fin, cy);
        g2.drawLine(fin, cy, fin, cy + u(l, 0.11));
        g2.drawLine(fin - u(l, 0.10), cy, fin - u(l, 0.10), cy + u(l, 0.08));
    }

    private void dibujarCajaFragil(Graphics2D g2, int resistencia, int bx, int by, int l) {
        int arco = u(l, 0.16);
        g2.setColor(Palette.FRAGILE);
        g2.fillRoundRect(bx, by, l, l, arco, arco);
        g2.setColor(Palette.FRAGILE_LINE);
        g2.setStroke(new BasicStroke(Math.max(1.5f, l / 22f), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10f,
                new float[]{l / 9f, l / 12f}, 0f));
        g2.drawRoundRect(bx, by, l, l, arco, arco);

        g2.setStroke(new BasicStroke(Math.max(1f, l / 30f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawPolyline(
                new int[]{bx + u(l, 0.76), bx + u(l, 0.66), bx + u(l, 0.78), bx + u(l, 0.70)},
                new int[]{by + u(l, 0.08), by + u(l, 0.20), by + u(l, 0.30), by + u(l, 0.42)}, 4);

        Color tinta = resistencia <= RESISTENCIA_CRITICA ? Palette.ERROR : Palette.INK;
        dibujarTexto(g2, String.valueOf(resistencia), bx + l / 2, by + l / 2, u(l, 0.42), tinta);
    }

    private void dibujarMarcaEnLugar(Graphics2D g2, int bx, int by, int l) {
        int diametro = Math.max(10, u(l, 0.30));
        int mx = bx + l - diametro + u(l, 0.08);
        int my = by - u(l, 0.08);
        g2.setColor(Palette.BOX_IN_PLACE_LINE);
        g2.fillOval(mx, my, diametro, diametro);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(Math.max(1.5f, diametro / 8f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawPolyline(
                new int[]{mx + u(diametro, 0.28), mx + u(diametro, 0.45), mx + u(diametro, 0.74)},
                new int[]{my + u(diametro, 0.52), my + u(diametro, 0.70), my + u(diametro, 0.34)}, 3);
    }

    private void dibujarJugador(Graphics2D g2, int x, int y, int t) {
        int cx = x + t / 2;
        int cy = y + t / 2;
        int r = u(t, 0.28);
        g2.setColor(Palette.SHADOW);
        g2.fillOval(cx - r, y + u(t, 0.70), 2 * r, u(t, 0.14));
        g2.setColor(Palette.PLAYER);
        g2.fillOval(cx - r, cy - r, 2 * r, 2 * r);

        int ojo = Math.max(2, u(t, 0.07));
        int separacion = u(t, 0.10);
        int ojoY = cy - u(t, 0.08);
        g2.setColor(Color.WHITE);
        g2.fillOval(cx - separacion - ojo / 2, ojoY, ojo, ojo);
        g2.fillOval(cx + separacion - ojo / 2, ojoY, ojo, ojo);
    }

    private static int u(int lado, double fraccion) {
        return (int) Math.round(lado * fraccion);
    }

    private static void dibujarTexto(Graphics2D g2, String texto, int cx, int cy, int tamano, Color color) {
        g2.setFont(new Font(Palette.FONT_FAMILY, Font.BOLD, Math.max(6, tamano)));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(color);
        g2.drawString(texto, cx - fm.stringWidth(texto) / 2, cy + (fm.getAscent() - fm.getDescent()) / 2);
    }
}
