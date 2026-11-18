package sokoban.view;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import sokoban.dto.BoardSnapshot;
import sokoban.dto.BoxData;
import sokoban.dto.CellPosition;
import sokoban.dto.BoxType;
import sokoban.dto.EstadoVisualCajaFragil;
import sokoban.dto.EstadoPowerUp;
import sokoban.dto.PowerUpData;
import sokoban.dto.TileData;
import sokoban.dto.TileType;
import sokoban.dto.TipoPowerUp;
import sokoban.model.Direction;

public class VistaTablero extends JPanel {

    private static final int MARGEN = 28;
    private static final int LADO_MINIMO = 8;
    private static final int LADO_MAXIMO = 72;
    private static final long DURACION_RESTOS_NS = 850_000_000L;
    private static final long DURACION_RESTOS_MURO_NS = 520_000_000L;
    private static final long TIEMPO_POR_CELDA_NS = 115_000_000L;
    private static final long DURACION_MINIMA_NS = 150_000_000L;
    private static final long DURACION_MAXIMA_NS = 850_000_000L;
    private static final int FOTOGRAMA_MS = 16;
    private static final long DURACION_PASO_NS = 145_000_000L;

    private BoardSnapshot tablero;
    private List<PowerUpData> powerUps = List.of();
    private SpritesJugador.Variante varianteJugador = SpritesJugador.Variante.NORMAL;
    private final List<EfectoRotura> restosTemporales = new ArrayList<>();
    private final List<EfectoRoturaMuro> murosRotosTemporales = new ArrayList<>();
    private DeslizamientoCaja cajaDeslizandose;
    private final SpritesJugador spritesJugador = new SpritesJugador();
    private Direction direccionJugador = Direction.DOWN;
    private MovimientoJugador movimientoJugador;
    private final Timer animacionCaminata = new Timer(FOTOGRAMA_MS, e -> {
        if (!caminataEnCurso()) {
            movimientoJugador = null;
            ((Timer) e.getSource()).stop();
        }
        repaint();
    });

    /** Interpolacion visual exclusivamente: la posicion real pertenece al modelo. */
    private record MovimientoJugador(CellPosition origen, CellPosition destino,
                                    long inicioNanos, long duracionNanos) {
    }

    /** Cambia la orientacion incluso cuando la tecla apunta a una pared. */
    public void orientarJugador(Direction direccion) {
        if (direccion != null) {
            direccionJugador = direccion;
            repaint();
        }
    }
    private final Timer animacionHielo = new Timer(FOTOGRAMA_MS, e -> {
        if (!deslizamientoEnCurso()) {
            cajaDeslizandose = null;
            ((Timer) e.getSource()).stop();
        }
        repaint();
    });

    /** Solo datos de presentacion: el modelo ya movio la caja a su posicion final. */
    private record DeslizamientoCaja(CellPosition origen, CellPosition destino, BoxData caja,
                                    CellPosition jugadorAntes, CellPosition jugadorDespues,
                                    boolean desaparece, long inicioNanos, long duracionNanos) {
    }

    private final Timer animacionRotura = new Timer(45, e -> {
        restosTemporales.removeIf(resto -> System.nanoTime() - resto.inicioNanos() >= DURACION_RESTOS_NS);
        if (restosTemporales.isEmpty()) {
            ((Timer) e.getSource()).stop();
        }
        repaint();
    });

    private final Timer animacionRoturaMuro = new Timer(45, e -> {
        murosRotosTemporales.removeIf(resto -> System.nanoTime() - resto.inicioNanos() >= DURACION_RESTOS_MURO_NS);
        if (murosRotosTemporales.isEmpty()) {
            ((Timer) e.getSource()).stop();
        }
        repaint();
    });

    private record EfectoRotura(CellPosition posicion, long inicioNanos) {
    }

    private record EfectoRoturaMuro(CellPosition posicion, Direction direccion, long inicioNanos) {
    }

    public VistaTablero() {
        setBackground(Palette.BACKGROUND);
        setPreferredSize(new Dimension(760, 540));
    }

    /** Sobrecarga compatible para vistas que no informan si fue un nuevo movimiento. */
    public void setTablero(BoardSnapshot tablero, List<PowerUpData> powerUps) {
        setTablero(tablero, powerUps, false);
    }

    /** animarMovimiento=true solo si Game sumo exactamente un movimiento valido. */
    public void setTablero(BoardSnapshot tablero, List<PowerUpData> powerUps, boolean animarMovimiento) {
        setTablero(tablero, powerUps, List.of(), animarMovimiento);
    }

    /** La apariencia del obrero se decide solo a partir de los poderes del snapshot. */
    public void setTablero(BoardSnapshot tablero, List<PowerUpData> powerUps,
                           List<EstadoPowerUp> poderesActivos, boolean animarMovimiento) {
        this.powerUps = List.copyOf(powerUps);
        this.varianteJugador = SpritesJugador.variantePara(poderesActivos);
        actualizarTablero(tablero, animarMovimiento);
    }

    public void setTablero(BoardSnapshot tablero) {
        setTablero(tablero, List.of(), List.of(), false);
    }

    private void actualizarTablero(BoardSnapshot actual, boolean animarMovimiento) {
        BoardSnapshot anterior = this.tablero;
        // Undo, reinicio, acciones sin desplazamiento o nuevo nivel cancelan la interpolacion.
        animacionHielo.stop();
        animacionCaminata.stop();
        movimientoJugador = null;
        boolean mismoNivel = anterior != null && actual != null
                && anterior.rows() == actual.rows() && anterior.columns() == actual.columns();
        if (!mismoNivel) {
            direccionJugador = Direction.DOWN;
        }
        if (mismoNivel && animarMovimiento) {
            int df = actual.player().row() - anterior.player().row();
            int dc = actual.player().column() - anterior.player().column();
            if (Math.abs(df) + Math.abs(dc) == 1) {
                direccionJugador = direccionDesdeDesplazamiento(df, dc);
            }
        }
        cajaDeslizandose = animarMovimiento ? buscarDeslizamiento(anterior, actual) : null;
        if (cajaDeslizandose != null) {
            animacionHielo.start();
        } else if (mismoNivel && animarMovimiento && distancia(anterior.player(), actual.player()) == 1) {
            movimientoJugador = new MovimientoJugador(anterior.player(), actual.player(),
                    System.nanoTime(), DURACION_PASO_NS);
            animacionCaminata.start();
        }
        registrarRoturas(anterior, actual, cajaDeslizandose);
        registrarRoturasMuros(anterior, actual);
        this.tablero = actual;
        repaint();
    }

    private static Direction direccionDesdeDesplazamiento(int df, int dc) {
        if (df < 0) return Direction.UP;
        if (df > 0) return Direction.DOWN;
        return dc < 0 ? Direction.LEFT : Direction.RIGHT;
    }

    private boolean caminataEnCurso() {
        return movimientoJugador != null
                && System.nanoTime() - movimientoJugador.inicioNanos() < movimientoJugador.duracionNanos();
    }

    private static double progresoCaminata(MovimientoJugador movimiento) {
        return Math.max(0.0, Math.min(1.0,
                (double) (System.nanoTime() - movimiento.inicioNanos()) / movimiento.duracionNanos()));
    }

    private static int fotogramaAlCaminar(double avance) {
        return avance >= 1.0 ? 0 : Math.min(3, (int) Math.floor(avance * 4));
    }

    private boolean deslizamientoEnCurso() {
        return cajaDeslizandose != null
                && System.nanoTime() - cajaDeslizandose.inicioNanos() < cajaDeslizandose.duracionNanos();
    }

    /** Identifica una caja desplazada comparando dos snapshots, sin alterar reglas ni posiciones. */
    private DeslizamientoCaja buscarDeslizamiento(BoardSnapshot anterior, BoardSnapshot actual) {
        if (anterior == null || actual == null || anterior.rows() != actual.rows()
                || anterior.columns() != actual.columns()) {
            return null;
        }
        int df = actual.player().row() - anterior.player().row();
        int dc = actual.player().column() - anterior.player().column();
        if (Math.abs(df) + Math.abs(dc) != 1) {
            return null;
        }
        for (BoxData antes : anterior.boxes()) {
            // En un movimiento normal de empuje, el jugador ocupa la antigua celda de la caja.
            boolean empujada = antes.position().equals(actual.player());
            boolean juntoAlJugador = distancia(antes.position(), anterior.player()) == 1;
            if (!empujada && !juntoAlJugador) {
                continue;
            }
            if (contieneCajaEn(actual, antes.position())) {
                continue;
            }
            for (BoxData despues : actual.boxes()) {
                if (antes.type() != despues.type() || antes.label() != despues.label()
                        || contieneCajaEn(anterior, despues.position())) {
                    continue;
                }
                int pasos = pasosEnDireccion(antes.position(), despues.position(), df, dc);
                if (pasos < 1 || (!empujada && pasos != 1)) {
                    continue;
                }
                if (atraviesaHielo(anterior, antes.position(), despues.position(), df, dc)) {
                    return crearDeslizamiento(antes.position(), despues.position(), despues,
                            anterior.player(), actual.player(), false, pasos);
                }
            }
            // La caja fragil puede romperse en ese mismo empuje y no figurar en el snapshot final.
            if (empujada && antes.type() == BoxType.FRAGILE
                    && antes.resistance() == 1
                    && contarFragiles(anterior) == contarFragiles(actual) + 1) {
                CellPosition fin = destinoEstimadoDeCajaRota(anterior, antes.position(), df, dc);
                if (atraviesaHielo(anterior, antes.position(), fin, df, dc)) {
                    return crearDeslizamiento(antes.position(), fin, antes,
                            anterior.player(), actual.player(), true, distancia(antes.position(), fin));
                }
            }
        }
        return null;
    }

    private DeslizamientoCaja crearDeslizamiento(CellPosition origen, CellPosition destino, BoxData caja,
                                                CellPosition jugadorAntes, CellPosition jugadorDespues,
                                                boolean desaparece, int pasos) {
        long duracion = Math.min(DURACION_MAXIMA_NS,
                Math.max(DURACION_MINIMA_NS, pasos * TIEMPO_POR_CELDA_NS));
        return new DeslizamientoCaja(origen, destino, caja,
                jugadorAntes, jugadorDespues, desaparece, System.nanoTime(), duracion);
    }

    private static int contarFragiles(BoardSnapshot snapshot) {
        return (int) snapshot.boxes().stream().filter(c -> c.type() == BoxType.FRAGILE).count();
    }

    private static boolean contieneCajaEn(BoardSnapshot snapshot, CellPosition posicion) {
        return snapshot.boxes().stream().anyMatch(c -> c.position().equals(posicion));
    }

    private static int distancia(CellPosition a, CellPosition b) {
        return Math.abs(a.row() - b.row()) + Math.abs(a.column() - b.column());
    }

    private static int pasosEnDireccion(CellPosition origen, CellPosition fin, int df, int dc) {
        int dr = fin.row() - origen.row();
        int dcaja = fin.column() - origen.column();
        if ((df == 0 && dr != 0) || (dc == 0 && dcaja != 0)) {
            return -1;
        }
        int pasos = dr * df + dcaja * dc;
        return pasos > 0 && dr == pasos * df && dcaja == pasos * dc ? pasos : -1;
    }

    private static boolean atraviesaHielo(BoardSnapshot board, CellPosition origen, CellPosition fin,
                                          int df, int dc) {
        int pasos = pasosEnDireccion(origen, fin, df, dc);
        for (int paso = 0; paso <= pasos; paso++) {
            int fila = origen.row() + paso * df;
            int columna = origen.column() + paso * dc;
            if (fila >= 0 && fila < board.rows() && columna >= 0 && columna < board.columns()
                    && board.tileAt(fila, columna).type() == TileType.ICE) {
                return true;
            }
        }
        return false;
    }

    /** Reproduce solo el calculo del destino para una caja que ya desaparecio por rotura. */
    private static CellPosition destinoEstimadoDeCajaRota(BoardSnapshot anterior, CellPosition origen,
                                                          int df, int dc) {
        CellPosition actual = new CellPosition(origen.row() + df, origen.column() + dc);
        if (!estaDentro(anterior, actual)) {
            return origen;
        }
        while (anterior.tileAt(actual.row(), actual.column()).type() == TileType.ICE) {
            CellPosition siguiente = new CellPosition(actual.row() + df, actual.column() + dc);
            if (!estaDentro(anterior, siguiente) || contieneCajaEn(anterior, siguiente)) {
                break;
            }
            TileData tile = anterior.tileAt(siguiente.row(), siguiente.column());
            if (tile.type() == TileType.WALL || (tile.type() == TileType.DOOR && !tile.open())) {
                break;
            }
            actual = siguiente;
        }
        return actual;
    }

    private static boolean estaDentro(BoardSnapshot board, CellPosition posicion) {
        return posicion.row() >= 0 && posicion.row() < board.rows()
                && posicion.column() >= 0 && posicion.column() < board.columns();
    }

    /** Conserva el efecto de rotura; si resbalo primero, los restos aparecen al llegar al destino. */
    private void registrarRoturas(BoardSnapshot anterior, BoardSnapshot actual, DeslizamientoCaja deslizamiento) {
        if (anterior == null || actual == null || anterior.rows() != actual.rows()
                || anterior.columns() != actual.columns()) {
            restosTemporales.clear();
            animacionRotura.stop();
            return;
        }
        int previas = contarFragiles(anterior);
        int actuales = contarFragiles(actual);
        if (actuales > previas) {
            restosTemporales.clear();
            animacionRotura.stop();
        }
        if (previas != actuales + 1) {
            return;
        }
        int df = actual.player().row() - anterior.player().row();
        int dc = actual.player().column() - anterior.player().column();
        if (Math.abs(df) + Math.abs(dc) != 1) {
            return;
        }
        for (BoxData caja : anterior.boxes()) {
            if (caja.type() != BoxType.FRAGILE || contieneCajaEn(actual, caja.position())) {
                continue;
            }
            CellPosition pos = destinoEstimadoDeCajaRota(anterior, caja.position(), df, dc);
            long inicio = System.nanoTime();
            if (deslizamiento != null && deslizamiento.desaparece()
                    && deslizamiento.origen().equals(caja.position())) {
                pos = deslizamiento.destino();
                inicio = deslizamiento.inicioNanos() + deslizamiento.duracionNanos();
            }
            if (estaDentro(actual, pos)) {
                restosTemporales.add(new EfectoRotura(pos, inicio));
                animacionRotura.start();
            }
            break;
        }
    }

    /**
     * Efecto puramente visual: cuando un muro pasa a ser piso tras usar el martillo,
     * se dibujan fragmentos durante un instante. No altera el modelo ni las colisiones.
     */
    private void registrarRoturasMuros(BoardSnapshot anterior, BoardSnapshot actual) {
        if (anterior == null || actual == null || anterior.rows() != actual.rows()
                || anterior.columns() != actual.columns()) {
            murosRotosTemporales.clear();
            animacionRoturaMuro.stop();
            return;
        }
        if (contarMuros(actual) > contarMuros(anterior)) {
            murosRotosTemporales.clear();
            animacionRoturaMuro.stop();
            return;
        }
        long inicio = System.nanoTime();
        for (int f = 0; f < anterior.rows(); f++) {
            for (int c = 0; c < anterior.columns(); c++) {
                if (anterior.tileAt(f, c).type() == TileType.WALL && actual.tileAt(f, c).type() != TileType.WALL) {
                    CellPosition posicion = new CellPosition(f, c);
                    Direction direccion = direccionDeRoturaMuro(anterior.player(), posicion);
                    murosRotosTemporales.add(new EfectoRoturaMuro(posicion, direccion, inicio));
                }
            }
        }
        if (!murosRotosTemporales.isEmpty()) {
            animacionRoturaMuro.start();
        }
    }

    private static int contarMuros(BoardSnapshot board) {
        int total = 0;
        for (int f = 0; f < board.rows(); f++) {
            for (int c = 0; c < board.columns(); c++) {
                if (board.tileAt(f, c).type() == TileType.WALL) {
                    total++;
                }
            }
        }
        return total;
    }

    private Direction direccionDeRoturaMuro(CellPosition jugador, CellPosition muro) {
        int df = muro.row() - jugador.row();
        int dc = muro.column() - jugador.column();
        if (Math.abs(df) + Math.abs(dc) == 1) {
            return direccionDesdeDesplazamiento(df, dc);
        }
        return direccionJugador;
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
        for (PowerUpData poder : powerUps) {
            CellPosition p = poder.position();
            dibujarPowerUp(g2, poder.tipo(), x0 + p.column() * lado, y0 + p.row() * lado, lado);
        }
        boolean resbalando = deslizamientoEnCurso();
        for (BoxData caja : tablero.boxes()) {
            // No dibujar simultaneamente la caja en el destino y en la interpolacion.
            if (resbalando && caja == cajaDeslizandose.caja()) {
                continue;
            }
            CellPosition p = caja.position();
            dibujarCaja(g2, caja, x0 + p.column() * lado, y0 + p.row() * lado, lado);
        }
        if (resbalando) {
            dibujarCajaDeslizandose(g2, x0, y0, lado);
        }
        for (EfectoRotura resto : restosTemporales) {
            Graphics2D dibujo = (Graphics2D) g2.create();
            try {
                long tiempo = System.nanoTime() - resto.inicioNanos();
                if (tiempo < 0 || tiempo >= DURACION_RESTOS_NS) {
                    continue;
                }
                float opacidad = Math.max(0f, 1f - (float) tiempo / DURACION_RESTOS_NS);
                dibujo.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
                CellPosition p = resto.posicion();
                dibujarRestosCaja(dibujo, x0 + p.column() * lado + u(lado, 0.1),
                        y0 + p.row() * lado + u(lado, 0.1), lado - 2 * u(lado, 0.1));
            } finally {
                dibujo.dispose();
            }
        }
        for (EfectoRoturaMuro resto : murosRotosTemporales) {
            Graphics2D dibujo = (Graphics2D) g2.create();
            try {
                long tiempo = System.nanoTime() - resto.inicioNanos();
                if (tiempo < 0 || tiempo >= DURACION_RESTOS_MURO_NS) {
                    continue;
                }
                float opacidad = Math.max(0f, 1f - (float) tiempo / DURACION_RESTOS_MURO_NS);
                dibujo.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
                CellPosition p = resto.posicion();
                dibujarRoturaMuro(dibujo, x0 + p.column() * lado, y0 + p.row() * lado, lado, resto.direccion(),
                        (float) tiempo / DURACION_RESTOS_MURO_NS);
            } finally {
                dibujo.dispose();
            }
        }
        CellPosition pj = tablero.player();
        if (resbalando) {
            // Durante el primer tramo el jugador avanza una celda a la misma velocidad
            // que la caja. Despues permanece quieto mientras la caja sigue sobre hielo.
            double avanceCaja = progresoDeslizamiento(cajaDeslizandose);
            int pasos = distancia(cajaDeslizandose.origen(), cajaDeslizandose.destino());
            double avanceJugador = Math.min(1.0, avanceCaja * pasos);
            int jx = x0 + (int) Math.round(lado * (cajaDeslizandose.jugadorAntes().column()
                    + (cajaDeslizandose.jugadorDespues().column()
                    - cajaDeslizandose.jugadorAntes().column()) * avanceJugador));
            int jy = y0 + (int) Math.round(lado * (cajaDeslizandose.jugadorAntes().row()
                    + (cajaDeslizandose.jugadorDespues().row()
                    - cajaDeslizandose.jugadorAntes().row()) * avanceJugador));
            dibujarJugador(g2, jx, jy, lado, fotogramaAlCaminar(avanceJugador));
        } else if (caminataEnCurso()) {
            double avance = progresoCaminata(movimientoJugador);
            CellPosition desde = movimientoJugador.origen();
            CellPosition hasta = movimientoJugador.destino();
            int jx = x0 + (int) Math.round(lado * (desde.column()
                    + (hasta.column() - desde.column()) * avance));
            int jy = y0 + (int) Math.round(lado * (desde.row()
                    + (hasta.row() - desde.row()) * avance));
            dibujarJugador(g2, jx, jy, lado, fotogramaAlCaminar(avance));
        } else {
            dibujarJugador(g2, x0 + pj.column() * lado, y0 + pj.row() * lado, lado, 0);
        }
    }

    private static double progresoDeslizamiento(DeslizamientoCaja movimiento) {
        return Math.min(1.0,
                Math.max(0.0, (double) (System.nanoTime() - movimiento.inicioNanos())
                        / movimiento.duracionNanos()));
    }

    /** Posicion fraccionaria entre las celdas de origen y destino (velocidad constante sobre hielo). */
    private void dibujarCajaDeslizandose(Graphics2D g2, int x0, int y0, int lado) {
        DeslizamientoCaja movimiento = cajaDeslizandose;
        if (movimiento == null) {
            return;
        }
        double progreso = progresoDeslizamiento(movimiento);
        int x = x0 + (int) Math.round(lado * (movimiento.origen().column()
                + (movimiento.destino().column() - movimiento.origen().column()) * progreso));
        int y = y0 + (int) Math.round(lado * (movimiento.origen().row()
                + (movimiento.destino().row() - movimiento.origen().row()) * progreso));

        Graphics2D estela = (Graphics2D) g2.create();
        try {
            estela.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.42f));
            estela.setColor(Palette.ICE_LINE);
            estela.setStroke(new BasicStroke(Math.max(1.5f, lado / 24f),
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int dc = Integer.compare(movimiento.destino().column(), movimiento.origen().column());
            int df = Integer.compare(movimiento.destino().row(), movimiento.origen().row());
            int centroX = x + lado / 2;
            int centroY = y + lado / 2;
            int rastro = u(lado, 0.48);
            for (int salto = -1; salto <= 1; salto++) {
                int desplazamiento = salto * u(lado, 0.20);
                int ex = centroX - dc * rastro + df * desplazamiento;
                int ey = centroY - df * rastro - dc * desplazamiento;
                estela.drawLine(ex, ey, ex - dc * u(lado, 0.20), ey - df * u(lado, 0.20));
            }
        } finally {
            estela.dispose();
        }
        dibujarCaja(g2, movimiento.caja(), x, y, lado);
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

    private void dibujarRoturaMuro(Graphics2D g2, int x, int y, int t, Direction direccion, float progreso) {
        Graphics2D particulas = (Graphics2D) g2.create();
        try {
            float polvo = Math.max(0f, 1f - progreso * 1.1f);
            particulas.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(0.55f, polvo)));
            particulas.setColor(Palette.WALL_EDGE);
            int nube = u(t, 0.64);
            int cx = x + t / 2;
            int cy = y + t / 2;
            particulas.fillOval(cx - nube / 2, cy - nube / 3, nube, nube / 2);
            particulas.fillOval(cx - nube / 3, cy - nube / 2, nube / 2, nube / 2);
        } finally {
            particulas.dispose();
        }

        Color relleno = Palette.WALL;
        Color borde = Palette.WALL_EDGE;
        float avance = Math.max(0f, Math.min(1f, progreso));
        int[][] base = {
                {18, 18, 34, 10, 42, 26, 25, 38},
                {58, 15, 79, 22, 70, 42, 52, 34},
                {26, 54, 48, 50, 42, 76, 18, 74},
                {57, 55, 82, 50, 78, 78, 54, 73}
        };
        int[] dx = direccion == Direction.LEFT ? new int[]{-1, -1, -1, -1}
                : direccion == Direction.RIGHT ? new int[]{1, 1, 1, 1}
                : new int[]{-1, 1, -1, 1};
        int[] dy = direccion == Direction.UP ? new int[]{-1, -1, -1, -1}
                : direccion == Direction.DOWN ? new int[]{1, 1, 1, 1}
                : new int[]{-1, -1, 1, 1};
        for (int i = 0; i < base.length; i++) {
            int empujeX = Math.round(dx[i] * t * 0.18f * avance);
            int empujeY = Math.round(dy[i] * t * 0.18f * avance);
            int gravedad = Math.round(t * 0.10f * avance * avance * (i < 2 ? 1f : 1.8f));
            int[] xs = new int[4];
            int[] ys = new int[4];
            for (int p = 0; p < 4; p++) {
                xs[p] = x + Math.round(base[i][p * 2] / 100f * t) + empujeX;
                ys[p] = y + Math.round(base[i][p * 2 + 1] / 100f * t) + empujeY + gravedad;
            }
            g2.setColor(relleno);
            g2.fillPolygon(xs, ys, 4);
            g2.setColor(borde);
            g2.setStroke(new BasicStroke(Math.max(1f, t / 28f)));
            g2.drawPolygon(xs, ys, 4);
        }
        g2.setColor(new Color(255, 255, 255, Math.max(0, 120 - Math.round(120 * avance))));
        g2.setStroke(new BasicStroke(Math.max(1.5f, t / 20f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int crackX1 = x + u(t, 0.25);
        int crackY1 = y + u(t, 0.20);
        int crackX2 = x + u(t, 0.52);
        int crackY2 = y + u(t, 0.48);
        int crackX3 = x + u(t, 0.40);
        int crackY3 = y + u(t, 0.72);
        g2.drawPolyline(new int[]{crackX1, crackX2, crackX3}, new int[]{crackY1, crackY2, crackY3}, 3);
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
            case FRAGILE -> dibujarCajaFragil(g2, caja, bx, by, l);
            case KEY -> dibujarCajaLlave(g2, caja.label(), bx, by, l);
        }
        if (caja.inPlace() && caja.estadoFragil() != EstadoVisualCajaFragil.ROTA) {
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

    private void dibujarCajaLlave(Graphics2D g2, char letra, int bx, int by, int l) {
        Color color = Palette.lockColor(letra);
        dibujarCuerpoDeCaja(g2, color, color.darker(), bx, by, l);
        dibujarTexto(g2, String.valueOf(letra), bx + l / 2, by + u(l, 0.34), u(l, 0.30), Color.WHITE);
        int cy = by + u(l, 0.66);
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

    private void dibujarCajaFragil(Graphics2D g2, BoxData caja, int bx, int by, int l) {
        EstadoVisualCajaFragil aspecto = caja.estadoFragil();
        if (aspecto == EstadoVisualCajaFragil.ROTA) {
            dibujarRestosCaja(g2, bx, by, l);
            return;
        }
        // Un DTO antiguo de cinco campos sigue viendose como una caja intacta.
        boolean agrietada = aspecto == EstadoVisualCajaFragil.AGRIETADA;
        int arco = u(l, 0.16);
        g2.setColor(Palette.FRAGILE);
        g2.fillRoundRect(bx, by, l, l, arco, arco);
        g2.setColor(agrietada ? Palette.ERROR : Palette.FRAGILE_LINE);
        g2.setStroke(new BasicStroke(Math.max(1.5f, l / 22f), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                10f, new float[]{Math.max(2f, l / 9f), Math.max(2f, l / 12f)}, 0f));
        g2.drawRoundRect(bx, by, l, l, arco, arco);

        if (agrietada) {
            // Las grietas aparecen unicamente cuando se consumio al menos el 50 %.
            g2.setStroke(new BasicStroke(Math.max(1.5f, l / 22f), BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            g2.drawPolyline(
                    new int[]{bx + u(l, 0.16), bx + u(l, 0.34), bx + u(l, 0.25), bx + u(l, 0.46)},
                    new int[]{by + u(l, 0.10), by + u(l, 0.32), by + u(l, 0.48), by + u(l, 0.66)}, 4);
            g2.drawPolyline(
                    new int[]{bx + u(l, 0.80), bx + u(l, 0.64), bx + u(l, 0.76), bx + u(l, 0.56)},
                    new int[]{by + u(l, 0.08), by + u(l, 0.28), by + u(l, 0.44), by + u(l, 0.72)}, 4);
        }
        dibujarTexto(g2, String.valueOf(caja.resistance()), bx + l / 2, by + l / 2,
                u(l, 0.42), agrietada ? Palette.ERROR : Palette.INK);
    }

    /** Representacion visual efimera; la caja destruida NO permanece en Tablero. */
    private void dibujarRestosCaja(Graphics2D g2, int bx, int by, int l) {
        g2.setColor(Palette.FRAGILE_LINE);
        g2.fillPolygon(
                new int[]{bx + u(l, 0.05), bx + u(l, 0.34), bx + u(l, 0.42), bx + u(l, 0.17)},
                new int[]{by + u(l, 0.25), by + u(l, 0.12), by + u(l, 0.40), by + u(l, 0.53)}, 4);
        g2.fillPolygon(
                new int[]{bx + u(l, 0.58), bx + u(l, 0.89), bx + u(l, 0.74), bx + u(l, 0.50)},
                new int[]{by + u(l, 0.14), by + u(l, 0.30), by + u(l, 0.66), by + u(l, 0.48)}, 4);
        g2.fillPolygon(
                new int[]{bx + u(l, 0.25), bx + u(l, 0.55), bx + u(l, 0.68), bx + u(l, 0.38)},
                new int[]{by + u(l, 0.66), by + u(l, 0.55), by + u(l, 0.93), by + u(l, 0.88)}, 4);
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

    private void dibujarPowerUp(Graphics2D g2, TipoPowerUp tipo, int x, int y, int t) {
        int diametro = u(t, 0.62);
        int bx = x + (t - diametro) / 2;
        int by = y + (t - diametro) / 2;
        g2.setColor(Palette.SHADOW);
        g2.fillOval(bx, by + u(t, 0.04), diametro, diametro);
        g2.setColor(tipo == TipoPowerUp.MARTILLO ? Palette.POWER_HAMMER : Palette.POWER_STRENGTH);
        g2.fillOval(bx, by, diametro, diametro);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(Math.max(1.5f, t / 22f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = x + t / 2;
        int cy = y + t / 2;
        int r = diametro / 2;
        if (tipo == TipoPowerUp.MARTILLO) {
            g2.drawLine(cx - u(r, 0.45), cy + u(r, 0.45), cx + u(r, 0.15), cy - u(r, 0.15));
            g2.fillRoundRect(cx - u(r, 0.05), cy - u(r, 0.6), u(r, 0.7), u(r, 0.45), 4, 4);
        } else {
            g2.fillPolygon(
                    new int[]{cx + u(r, 0.1), cx - u(r, 0.35), cx, cx - u(r, 0.1), cx + u(r, 0.35), cx},
                    new int[]{cy - u(r, 0.65), cy + u(r, 0.05), cy + u(r, 0.05), cy + u(r, 0.65), cy - u(r, 0.05),
                            cy - u(r, 0.05)}, 6);
        }
    }

    private void dibujarJugador(Graphics2D g2, int x, int y, int t, int fotograma) {
        if (spritesJugador.dibujar(g2, direccionJugador, fotograma, x, y, t, varianteJugador)) {
            return;
        }
        // Respaldo sin recurso PNG para mantener la compatibilidad con despliegues antiguos.
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
