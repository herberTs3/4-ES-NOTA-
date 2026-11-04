package sokoban.vista;

import sokoban.modelo.Posicion;
import sokoban.modelo.Tablero;
import sokoban.modelo.caja.Caja;
import sokoban.modelo.caja.VisitanteCaja;
import sokoban.modelo.terreno.VisitanteTerreno;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Dibuja un Tablero. Las celdas se escalan para ocupar el espacio disponible. */
public class VistaTablero extends JPanel {

    private static final int MARGEN = 20;
    private static final int LADO_MAXIMO = 96;

    private static final Color FONDO = new Color(0x1f2430);
    private static final Color TEXTO_FONDO = new Color(0xa9b0c2);
    private static final Color PISO = new Color(0xe6d8b8);
    private static final Color PISO_LINEA = new Color(0xd3c29a);
    private static final Color HIELO = new Color(0xbfe4f0);
    private static final Color HIELO_LINEA = new Color(0x93c5d8);
    private static final Color MURO = new Color(0x56607a);
    private static final Color MURO_JUNTA = new Color(0x363e54);
    private static final Color DESTINO = new Color(0xc0392b);
    private static final Color MADERA = new Color(0xc48a4b);
    private static final Color MADERA_BORDE = new Color(0x6f4519);
    private static final Color MADERA_EN_DESTINO = new Color(0x86b86a);
    private static final Color MADERA_EN_DESTINO_BORDE = new Color(0x3d6b2c);
    private static final Color FRAGIL = new Color(0xecd7ad);
    private static final Color FRAGIL_BORDE = new Color(0x8a6b3a);
    private static final Color TINTA = new Color(0x2b2118);
    private static final Color[] CLAVES = {
            new Color(0xd6453d), new Color(0x2f7fc1), new Color(0x2e9d5b), new Color(0xd19a1e),
            new Color(0x8e55b5), new Color(0xe0702f), new Color(0x1b9a9a), new Color(0xc2477f)
    };

    private Tablero tablero;

    public VistaTablero() {
        setBackground(FONDO);
        setPreferredSize(new Dimension(720, 560));
    }

    public void setTablero(Tablero tablero) {
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
                dibujarTexto(g2, "Sin nivel cargado", getWidth() / 2, getHeight() / 2, 18, TEXTO_FONDO);
            } else {
                dibujarTablero(g2);
            }
        } finally {
            g2.dispose();
        }
    }

    // ---------------------------------------------------------------- tablero

    private void dibujarTablero(Graphics2D g2) {
        int filas = tablero.getFilas();
        int columnas = tablero.getColumnas();
        int lado = Math.min(LADO_MAXIMO, Math.max(8,
                Math.min((getWidth() - 2 * MARGEN) / columnas, (getHeight() - 2 * MARGEN) / filas)));
        int x0 = (getWidth() - lado * columnas) / 2;
        int y0 = (getHeight() - lado * filas) / 2;

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                dibujarTerreno(g2, f, c, x0 + c * lado, y0 + f * lado, lado);
            }
        }
        for (Caja caja : tablero.getCajas()) {
            Posicion p = caja.getPosicion();
            dibujarCaja(g2, caja, x0 + p.columna() * lado, y0 + p.fila() * lado, lado);
        }
        Posicion pj = tablero.getJugador().getPosicion();
        dibujarJugador(g2, x0 + pj.columna() * lado, y0 + pj.fila() * lado, lado);
    }

    // ---------------------------------------------------------------- terrenos

    private void dibujarTerreno(Graphics2D g2, int fila, int columna, int x, int y, int t) {
        tablero.getTerreno(fila, columna).aceptar(new DibujanteDeTerreno(g2, fila, columna, x, y, t));
    }

    private void dibujarPiso(Graphics2D g2, boolean hielo, int x, int y, int t) {
        g2.setColor(hielo ? HIELO : PISO);
        g2.fillRect(x, y, t, t);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(hielo ? HIELO_LINEA : PISO_LINEA);
        g2.drawRect(x, y, t - 1, t - 1);
    }

    /** Dibuja la celda según el tipo de terreno, resuelto por double dispatch (sin instanceof). */
    private final class DibujanteDeTerreno implements VisitanteTerreno {
        private final Graphics2D g2;
        private final int fila;
        private final int columna;
        private final int x;
        private final int y;
        private final int t;

        DibujanteDeTerreno(Graphics2D g2, int fila, int columna, int x, int y, int t) {
            this.g2 = g2;
            this.fila = fila;
            this.columna = columna;
            this.x = x;
            this.y = y;
            this.t = t;
        }

        @Override
        public void visitarMuro() {
            if (muroVisible(fila, columna)) {
                dibujarMuro(g2, x, y, t);
            }
        }

        @Override
        public void visitarTerrenoNormal() {
            dibujarPiso(g2, false, x, y, t);
        }

        @Override
        public void visitarTerrenoDestino() {
            dibujarPiso(g2, false, x, y, t);
            dibujarDestino(g2, x, y, t);
        }

        @Override
        public void visitarTerrenoResbaladizo() {
            dibujarPiso(g2, true, x, y, t);
            dibujarBrillos(g2, x, y, t);
        }

        @Override
        public void visitarTerrenoCerrojo(int clave, boolean abierto) {
            dibujarPiso(g2, false, x, y, t);
            dibujarCerrojo(g2, clave, abierto, x, y, t);
        }

        @Override
        public void visitarOtro() {
            dibujarPiso(g2, false, x, y, t);
        }
    }

    /** Un muro solo se dibuja si toca (incluso en diagonal) alguna celda que no sea muro; el resto es exterior. */
    private boolean muroVisible(int fila, int columna) {
        for (int df = -1; df <= 1; df++) {
            for (int dc = -1; dc <= 1; dc++) {
                int f = fila + df;
                int c = columna + dc;
                boolean dentro = f >= 0 && f < tablero.getFilas() && c >= 0 && c < tablero.getColumnas();
                if ((df != 0 || dc != 0) && dentro && !tablero.getTerreno(f, c).esMuro()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void dibujarMuro(Graphics2D g2, int x, int y, int t) {
        g2.setColor(MURO);
        g2.fillRect(x, y, t, t);
        g2.setColor(MURO_JUNTA);
        g2.setStroke(new BasicStroke(Math.max(1f, t / 28f)));
        int hilada = t / 3;
        for (int i = 0; i < 3; i++) {
            int yy = y + i * hilada;
            g2.drawLine(x, yy, x + t, yy);
            if (i % 2 == 0) {
                g2.drawLine(x + t / 2, yy, x + t / 2, yy + hilada);
            } else {
                g2.drawLine(x + t / 4, yy, x + t / 4, yy + hilada);
                g2.drawLine(x + 3 * t / 4, yy, x + 3 * t / 4, yy + hilada);
            }
        }
        g2.setColor(new Color(255, 255, 255, 40));
        g2.fillRect(x, y, t, Math.max(1, t / 12));
    }

    private void dibujarBrillos(Graphics2D g2, int x, int y, int t) {
        g2.setStroke(new BasicStroke(Math.max(1.5f, t / 22f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(255, 255, 255, 170));
        g2.drawLine(x + u(t, 0.22), y + u(t, 0.50), x + u(t, 0.50), y + u(t, 0.22));
        g2.drawLine(x + u(t, 0.58), y + u(t, 0.80), x + u(t, 0.80), y + u(t, 0.58));
    }

    private void dibujarDestino(Graphics2D g2, int x, int y, int t) {
        int cx = x + t / 2;
        int cy = y + t / 2;
        int r = u(t, 0.2);
        int punto = Math.max(2, r / 3);
        g2.setStroke(new BasicStroke(Math.max(2f, t / 14f)));
        g2.setColor(DESTINO);
        g2.drawOval(cx - r, cy - r, 2 * r, 2 * r);
        g2.fillOval(cx - punto, cy - punto, 2 * punto, 2 * punto);
    }

    private void dibujarCerrojo(Graphics2D g2, int clave, boolean abierto, int x, int y, int t) {
        Color color = colorDeClave(clave);
        int cx = x + t / 2;
        int elevacion = abierto ? u(t, 0.10) : 0;

        g2.setStroke(new BasicStroke(Math.max(2f, t / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(0x3a3f4b));
        int arcoAncho = u(t, 0.34);
        g2.drawArc(cx - arcoAncho / 2, y + u(t, 0.15) - elevacion, arcoAncho, u(t, 0.62), 0, 180);

        int ancho = u(t, 0.54);
        int alto = u(t, 0.38);
        int cuerpoY = y + u(t, 0.46);
        g2.setColor(color);
        g2.fillRoundRect(cx - ancho / 2, cuerpoY, ancho, alto, u(t, 0.12), u(t, 0.12));
        g2.setStroke(new BasicStroke(Math.max(1.5f, t / 24f)));
        g2.setColor(color.darker().darker());
        g2.drawRoundRect(cx - ancho / 2, cuerpoY, ancho, alto, u(t, 0.12), u(t, 0.12));
        dibujarTexto(g2, letraDeClave(clave, true), cx, cuerpoY + alto / 2, u(t, 0.28), Color.WHITE);
    }

    // ------------------------------------------------------------------ cajas

    private void dibujarCaja(Graphics2D g2, Caja caja, int x, int y, int t) {
        int m = u(t, 0.1);
        caja.aceptar(new DibujanteDeCaja(g2, tablero.estaEnDestino(caja), x + m, y + m, t - 2 * m));
    }

    /** Dibuja la caja según su tipo, resuelto por double dispatch (sin instanceof). */
    private final class DibujanteDeCaja implements VisitanteCaja {
        private final Graphics2D g2;
        private final boolean enDestino;
        private final int bx;
        private final int by;
        private final int l;

        DibujanteDeCaja(Graphics2D g2, boolean enDestino, int bx, int by, int l) {
            this.g2 = g2;
            this.enDestino = enDestino;
            this.bx = bx;
            this.by = by;
            this.l = l;
        }

        @Override
        public void visitarCajaNormal() {
            dibujarCajaNormal(g2, enDestino, bx, by, l);
        }

        @Override
        public void visitarCajaLlave(int clave) {
            dibujarCajaLlave(g2, clave, bx, by, l);
        }

        @Override
        public void visitarCajaFragil(int movimientosRestantes) {
            dibujarCajaFragil(g2, movimientosRestantes, bx, by, l);
        }
    }

    private void dibujarCajaNormal(Graphics2D g2, boolean enDestino, int bx, int by, int l) {
        int arco = u(l, 0.12);
        g2.setColor(enDestino ? MADERA_EN_DESTINO : MADERA);
        g2.fillRoundRect(bx, by, l, l, arco, arco);
        g2.setColor(enDestino ? MADERA_EN_DESTINO_BORDE : MADERA_BORDE);
        g2.setStroke(new BasicStroke(Math.max(2f, l / 10f)));
        g2.drawRoundRect(bx, by, l, l, arco, arco);
        g2.setStroke(new BasicStroke(Math.max(1.5f, l / 14f)));
        int in = l / 5;
        g2.drawLine(bx + in, by + in, bx + l - in, by + l - in);
        g2.drawLine(bx + l - in, by + in, bx + in, by + l - in);
    }

    private void dibujarCajaLlave(Graphics2D g2, int clave, int bx, int by, int l) {
        Color color = colorDeClave(clave);
        int arco = u(l, 0.12);
        g2.setColor(color);
        g2.fillRoundRect(bx, by, l, l, arco, arco);
        g2.setColor(color.darker().darker());
        g2.setStroke(new BasicStroke(Math.max(2f, l / 10f)));
        g2.drawRoundRect(bx, by, l, l, arco, arco);

        int cx = bx + l / 2;
        int cy = by + u(l, 0.62);
        int r = u(l, 0.13);
        int anilloX = cx - u(l, 0.22);
        int fin = cx + u(l, 0.28);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(Math.max(2f, l / 11f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawOval(anilloX - r, cy - r, 2 * r, 2 * r);
        g2.drawLine(anilloX + r, cy, fin, cy);
        g2.drawLine(fin, cy, fin, cy + u(l, 0.15));
        g2.drawLine(fin - u(l, 0.12), cy, fin - u(l, 0.12), cy + u(l, 0.11));
        dibujarTexto(g2, letraDeClave(clave, false), cx, by + u(l, 0.22), u(l, 0.24), Color.WHITE);
    }

    private void dibujarCajaFragil(Graphics2D g2, int movimientosRestantes, int bx, int by, int l) {
        int arco = u(l, 0.12);
        g2.setColor(FRAGIL);
        g2.fillRoundRect(bx, by, l, l, arco, arco);
        float trazo = Math.max(2f, l / 10f);
        g2.setColor(FRAGIL_BORDE);
        g2.setStroke(new BasicStroke(trazo, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
                new float[]{l / 8f, l / 12f}, 0f));
        g2.drawRoundRect(bx, by, l, l, arco, arco);

        g2.setStroke(new BasicStroke(Math.max(1.5f, l / 16f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawPolyline(
                new int[]{bx + u(l, 0.72), bx + u(l, 0.60), bx + u(l, 0.74), bx + u(l, 0.64)},
                new int[]{by + u(l, 0.04), by + u(l, 0.20), by + u(l, 0.32), by + u(l, 0.46)}, 4);
        g2.drawPolyline(
                new int[]{bx + u(l, 0.04), bx + u(l, 0.20), bx + u(l, 0.14), bx + u(l, 0.30)},
                new int[]{by + u(l, 0.78), by + u(l, 0.68), by + u(l, 0.88), by + u(l, 0.96)}, 4);

        Color tinta = movimientosRestantes <= 1 ? DESTINO : TINTA;
        dibujarTexto(g2, String.valueOf(movimientosRestantes), bx + l / 2, by + l / 2, u(l, 0.50), tinta);
    }

    // ---------------------------------------------------------------- jugador

    private void dibujarJugador(Graphics2D g2, int x, int y, int t) {
        int cx = x + t / 2;
        g2.setColor(new Color(0, 0, 0, 55));
        g2.fillOval(cx - u(t, 0.30), y + u(t, 0.78), u(t, 0.60), u(t, 0.14));

        g2.setColor(new Color(0x2f6fb5));
        g2.fillRoundRect(cx - u(t, 0.26), y + u(t, 0.46), u(t, 0.52), u(t, 0.38), u(t, 0.30), u(t, 0.30));

        int cabeza = u(t, 0.38);
        g2.setColor(new Color(0xf2c9a0));
        g2.fillOval(cx - cabeza / 2, y + u(t, 0.14), cabeza, cabeza);
        g2.setColor(DESTINO);
        g2.fillArc(cx - cabeza / 2, y + u(t, 0.14), cabeza, cabeza, 0, 180);

        int ojo = Math.max(2, u(t, 0.05));
        g2.setColor(TINTA);
        g2.fillOval(cx - u(t, 0.09) - ojo / 2, y + u(t, 0.37), ojo, ojo);
        g2.fillOval(cx + u(t, 0.09) - ojo / 2, y + u(t, 0.37), ojo, ojo);
    }

    // ---------------------------------------------------------------- ayudas

    /** Fracción del lado de la celda, en píxeles. */
    private static int u(int lado, double fraccion) {
        return (int) Math.round(lado * fraccion);
    }

    private static Color colorDeClave(int clave) {
        return CLAVES[Math.floorMod(clave - 1, CLAVES.length)];
    }

    private static String letraDeClave(int clave, boolean mayuscula) {
        if (clave < 1 || clave > 26) {
            return "?";
        }
        return String.valueOf((char) ((mayuscula ? 'A' : 'a') + clave - 1));
    }

    private static void dibujarTexto(Graphics2D g2, String texto, int cx, int cy, int tamano, Color color) {
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(6, tamano)));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(color);
        g2.drawString(texto, cx - fm.stringWidth(texto) / 2, cy + (fm.getAscent() - fm.getDescent()) / 2);
    }
}
