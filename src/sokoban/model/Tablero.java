package sokoban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import sokoban.dto.BoardSnapshot;
import sokoban.dto.BoxData;
import sokoban.dto.TileData;
import sokoban.model.box.Caja;
import sokoban.model.terrain.Terreno;

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
        vincularTerrenos();
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

    public boolean puedeRecibirCaja(Posicion posicion) {
        return esTransitable(posicion) && getCajaEn(posicion).isEmpty();
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

    MoveResult moverJugador(Direction direccion) {
        Posicion destino = jugador.getPosicion().next(direccion);
        if (!esTransitable(destino)) {
            return MoveResult.BLOCKED;
        }
        Optional<Caja> caja = getCajaEn(destino);
        if (caja.isEmpty()) {
            jugador.moverA(destino);
            return MoveResult.MOVED;
        }
        if (!puedeRecibirCaja(destino.next(direccion))) {
            return MoveResult.BLOCKED;
        }
        empujar(caja.get(), direccion);
        jugador.moverA(destino);
        return MoveResult.PUSHED;
    }

    boolean estaResuelto() {
        boolean llavesEnLugar = cajas.stream().filter(caja -> !caja.requiereDestino()).allMatch(this::estaEnLugar);
        long enDestino = cajas.stream().filter(Caja::requiereDestino).filter(this::estaEnLugar).count();
        return llavesEnLugar && enDestino == contarDestinos();
    }

    boolean tieneCajasSuficientes() {
        return cajas.stream().filter(Caja::requiereDestino).count() >= contarDestinos();
    }

    BoardSnapshot snapshot() {
        List<List<TileData>> tiles = new ArrayList<>();
        for (Terreno[] fila : cuadricula) {
            List<TileData> row = new ArrayList<>();
            for (Terreno terreno : fila) {
                row.add(terreno.toData());
            }
            tiles.add(row);
        }
        List<BoxData> boxes = cajas.stream().map(caja -> caja.toData(estaEnLugar(caja))).toList();
        return new BoardSnapshot(filas, columnas, tiles, boxes, jugador.getPosicion().toData());
    }

    private boolean esTransitable(Posicion posicion) {
        return estaDentro(posicion) && getTerreno(posicion).esTransitable();
    }

    private boolean estaEnLugar(Caja caja) {
        return caja.estaEnLugar(getTerreno(caja.getPosicion()));
    }

    private void empujar(Caja caja, Direction direccion) {
        caja.empujar(direccion, this);
        if (caja.estaRota()) {
            cajas.remove(caja);
        }
    }

    private void vincularTerrenos() {
        List<Terreno> terrenos = new ArrayList<>();
        for (Terreno[] fila : cuadricula) {
            terrenos.addAll(List.of(fila));
        }
        terrenos.forEach(terreno -> terreno.vincular(terrenos));
    }
}
