package sokoban.modelo;

import sokoban.modelo.caja.Caja;
import sokoban.modelo.terreno.Terreno;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Tablero {

    private final Jugador jugador;
    private final List<Caja> cajas;
    private final int filas;
    private final int columnas;
    private final Terreno[][] cuadricula;

    public Tablero(Terreno[][] cuadricula, Jugador jugador, List<Caja> cajas) {
        Objects.requireNonNull(cuadricula, "cuadricula");
        if (cuadricula.length == 0 || cuadricula[0].length == 0) {
            throw new IllegalArgumentException("La cuadrícula no puede estar vacía");
        }
        this.filas = cuadricula.length;
        this.columnas = cuadricula[0].length;
        this.cuadricula = new Terreno[filas][columnas];
        for (int f = 0; f < filas; f++) {
            if (cuadricula[f].length != columnas) {
                throw new IllegalArgumentException("Todas las filas deben tener la misma cantidad de columnas");
            }
            for (int c = 0; c < columnas; c++) {
                this.cuadricula[f][c] = Objects.requireNonNull(cuadricula[f][c], "terreno");
            }
        }
        this.jugador = Objects.requireNonNull(jugador, "jugador");
        this.cajas = new ArrayList<>(cajas);
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public List<Caja> getCajas() {
        return Collections.unmodifiableList(cajas);
    }

    public boolean estaDentro(Posicion posicion) {
        return posicion.fila() >= 0 && posicion.fila() < filas
                && posicion.columna() >= 0 && posicion.columna() < columnas;
    }

    public Terreno getTerreno(int fila, int columna) {
        return cuadricula[fila][columna];
    }

    public Terreno getTerreno(Posicion posicion) {
        return getTerreno(posicion.fila(), posicion.columna());
    }

    public Optional<Caja> getCajaEn(Posicion posicion) {
        return cajas.stream().filter(caja -> caja.getPosicion().equals(posicion)).findFirst();
    }

    public boolean estaEnDestino(Caja caja) {
        return getTerreno(caja.getPosicion()).esDestino();
    }

    public int contarDestinos() {
        int total = 0;
        for (Terreno[] fila : cuadricula) {
            for (Terreno terreno : fila) {
                if (terreno.esDestino()) {
                    total++;
                }
            }
        }
        return total;
    }
}
