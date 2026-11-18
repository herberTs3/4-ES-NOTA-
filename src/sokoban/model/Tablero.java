package sokoban.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import sokoban.dto.BoardSnapshot;
import sokoban.dto.BoxData;
import sokoban.dto.EstadoPowerUp;
import sokoban.dto.PowerUpData;
import sokoban.dto.TileData;
import sokoban.dto.TipoPowerUp;
import sokoban.model.box.Caja;
import sokoban.model.terrain.Terreno;
import sokoban.model.terrain.TerrenoNormal;

public class Tablero {

    private final Jugador jugador;
    private final List<Caja> cajas;
    private final int filas;
    private final int columnas;
    private final Terreno[][] cuadricula;
    private final List<PowerUpEnMapa> powerUpsEnMapa;
    private final boolean[][] murosProtegidos;
    private final Map<Posicion, Terreno> murosRotos = new HashMap<>();

    public Tablero(Terreno[][] cuadricula, Jugador jugador, List<Caja> cajas, List<PowerUpEnMapa> powerUpsEnMapa,
                   boolean[][] murosProtegidos) {
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
        this.powerUpsEnMapa = new ArrayList<>(powerUpsEnMapa);
        this.murosProtegidos = new boolean[filas][columnas];
        for (int f = 0; f < filas; f++) {
            System.arraycopy(murosProtegidos[f], 0, this.murosProtegidos[f], 0, columnas);
        }
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

    public boolean romperMuroInterior(Direction direccion) {
        Posicion objetivo = jugador.getPosicion().next(direccion);
        if (!estaDentro(objetivo) || murosProtegidos[objetivo.fila()][objetivo.columna()]
                || !getTerreno(objetivo).esMuro()) {
            return false;
        }
        murosRotos.put(objetivo, getTerreno(objetivo));
        cuadricula[objetivo.fila()][objetivo.columna()] = new TerrenoNormal();
        return true;
    }

    public boolean jalarCaja(Direction direccion) {
        Posicion anterior = jugador.getPosicion();
        Posicion atras = anterior.next(direccion.opuesta());
        Optional<Caja> caja = getCajaEn(anterior.next(direccion));
        if (caja.isEmpty() || !puedeRecibirCaja(atras)) {
            return false;
        }
        jugador.moverA(atras);
        caja.get().jalarA(anterior, this);
        return true;
    }

    public Optional<TipoPowerUp> recogerPowerUpEnJugador() {
        for (PowerUpEnMapa poder : powerUpsEnMapa) {
            if (poder.posicion().equals(jugador.getPosicion())) {
                powerUpsEnMapa.remove(poder);
                return Optional.of(poder.tipo());
            }
        }
        return Optional.empty();
    }

    public List<PowerUpData> powerUpsEnMapa() {
        return powerUpsEnMapa.stream().map(PowerUpEnMapa::toData).toList();
    }

    public MoveResult moverJugador(Direction direccion) {
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
        long enDestino = cajas.stream().filter(Caja::requiereDestino).filter(this::estaEnLugar).count();
        return enDestino == contarDestinos() && !hayCerrojosCerrados();
    }

    private boolean hayCerrojosCerrados() {
        for (Terreno[] fila : cuadricula) {
            for (Terreno terreno : fila) {
                if (terreno.esCerrojoCerrado()) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean tieneCajasSuficientes() {
        return cajas.stream().filter(Caja::requiereDestino).count() >= contarDestinos();
    }

    BoardMemento crearMemento(int movimientos, int empujes, List<EstadoPowerUp> poderes) {
        boolean[][] aperturas = new boolean[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                aperturas[f][c] = cuadricula[f][c].isOpen();
            }
        }
        return new BoardMemento(jugador.getPosicion(), cajas.stream().map(Caja::copiar).toList(), aperturas,
                movimientos, empujes, murosRotos.keySet(), powerUpsEnMapa, poderes);
    }

    void restaurar(BoardMemento memento) {
        restaurarMuros(memento);
        powerUpsEnMapa.clear();
        powerUpsEnMapa.addAll(memento.powerUps());
        jugador.moverA(memento.playerPosition());
        cajas.clear();
        memento.boxes().forEach(caja -> cajas.add(caja.copiar()));
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                cuadricula[f][c].restoreOpen(memento.isOpen(f, c));
            }
        }
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

    private void restaurarMuros(BoardMemento memento) {
        for (Posicion muro : new ArrayList<>(murosRotos.keySet())) {
            if (!memento.brokenWalls().contains(muro)) {
                cuadricula[muro.fila()][muro.columna()] = murosRotos.remove(muro);
            }
        }
        for (Posicion muro : memento.brokenWalls()) {
            if (!murosRotos.containsKey(muro)) {
                murosRotos.put(muro, getTerreno(muro));
                cuadricula[muro.fila()][muro.columna()] = new TerrenoNormal();
            }
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
