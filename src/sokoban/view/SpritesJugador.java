package sokoban.view;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;

import javax.imageio.ImageIO;

import sokoban.dto.EstadoPowerUp;
import sokoban.dto.TipoPowerUp;
import sokoban.model.Direction;

/**
 * Sprites 4 x 4 del obrero. Exclusivo de la vista: no cambia Jugador ni Game.
 * Filas: abajo/frente, izquierda, derecha, arriba/espalda.
 * Columnas: cuatro fotogramas de caminata.
 *
 * Cada variante esta en un PNG independiente con las mismas dimensiones y la
 * misma cuadricula, lo que evita cambios de escala al activar un power-up.
 */
final class SpritesJugador {
    enum Variante { NORMAL, MARTILLO, SUPERFUERZA, AMBOS }

    private static final int FILAS = 4;
    private static final int COLUMNAS = 4;
    private static final String CARPETA = "sokoban/view/assets/";

    private record Hoja(BufferedImage imagen, int anchoFotograma, int altoFotograma) { }
    private final EnumMap<Variante, Hoja> hojas = new EnumMap<>(Variante.class);

    SpritesJugador() {
        cargar(Variante.NORMAL, "obrero_normal.png");
        cargar(Variante.MARTILLO, "obrero_martillo.png");
        cargar(Variante.SUPERFUERZA, "obrero_superfuerza.png");
        cargar(Variante.AMBOS, "obrero_ambos.png");
        // Compatibilidad si alguien instala solo el Java sin los nuevos PNG.
        if (!hojas.containsKey(Variante.NORMAL)) {
            cargar(Variante.NORMAL, "personaje_hp01.png");
        }
    }

    private void cargar(Variante variante, String nombreArchivo) {
        BufferedImage imagen = cargarImagen(nombreArchivo);
        if (imagen != null && imagen.getWidth() % COLUMNAS == 0 && imagen.getHeight() % FILAS == 0) {
            hojas.put(variante, new Hoja(imagen,
                    imagen.getWidth() / COLUMNAS, imagen.getHeight() / FILAS));
        }
    }

    private static BufferedImage cargarImagen(String nombreArchivo) {
        String recurso = "/" + CARPETA + nombreArchivo;
        try (InputStream entrada = SpritesJugador.class.getResourceAsStream(recurso)) {
            if (entrada != null) return ImageIO.read(entrada);
        } catch (IOException e) {
            // Probar tambien los directorios de trabajo habituales de un proyecto Java.
        }
        String[] rutas = {
                CARPETA + nombreArchivo,
                "src/" + CARPETA + nombreArchivo,
                "src/main/resources/" + CARPETA + nombreArchivo
        };
        for (String ruta : rutas) {
            Path archivo = Path.of(ruta);
            if (Files.isRegularFile(archivo)) {
                try {
                    BufferedImage imagen = ImageIO.read(archivo.toFile());
                    if (imagen != null) return imagen;
                } catch (IOException e) {
                    // Continuar con otras rutas.
                }
            }
        }
        return null;
    }

    /** Los poderes llegan a la vista en el GameSnapshot (solo lectura). */
    static Variante variantePara(List<EstadoPowerUp> poderes) {
        boolean martillo = false;
        boolean fuerza = false;
        if (poderes != null) {
            for (EstadoPowerUp poder : poderes) {
                if (poder.turnosRestantes() <= 0) continue;
                if (poder.tipo() == TipoPowerUp.MARTILLO && poder.usosRestantes() > 0) {
                    martillo = true;
                }
                if (poder.tipo() == TipoPowerUp.SUPERFUERZA) {
                    fuerza = true;
                }
            }
        }
        if (martillo && fuerza) return Variante.AMBOS;
        if (martillo) return Variante.MARTILLO;
        if (fuerza) return Variante.SUPERFUERZA;
        return Variante.NORMAL;
    }

    boolean disponible() {
        return hojas.containsKey(Variante.NORMAL);
    }

    /** API anterior conservada: dibuja la variante sin power-ups. */
    boolean dibujar(Graphics2D g, Direction direccion, int fotograma, int x, int y, int lado) {
        return dibujar(g, direccion, fotograma, x, y, lado, Variante.NORMAL);
    }

    /** Dibuja la variante segun los poderes activos, sin modificar su posicion real. */
    boolean dibujar(Graphics2D g, Direction direccion, int fotograma,
                    int x, int y, int lado, Variante variante) {
        Hoja hoja = hojas.getOrDefault(variante, hojas.get(Variante.NORMAL));
        if (hoja == null) return false;

        int fila = switch (direccion) {
            case DOWN -> 0;
            case LEFT -> 1;
            case RIGHT -> 2;
            case UP -> 3;
        };
        int columna = Math.floorMod(fotograma, COLUMNAS);
        int origenX = columna * hoja.anchoFotograma();
        int origenY = fila * hoja.altoFotograma();

        int ancho = Math.max(1, Math.round(lado * 0.96f));
        int alto = Math.max(1, Math.round(lado * 1.00f));
        int destinoX = x + (lado - ancho) / 2;
        int destinoY = y + lado - alto;

        Object filtroAnterior = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(hoja.imagen(), destinoX, destinoY, destinoX + ancho, destinoY + alto,
                    origenX, origenY, origenX + hoja.anchoFotograma(), origenY + hoja.altoFotograma(), null);
        } finally {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    filtroAnterior != null ? filtroAnterior : RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        return true;
    }
}
