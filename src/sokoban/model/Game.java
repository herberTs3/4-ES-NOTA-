package sokoban.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import sokoban.dto.EstadoPowerUp;
import sokoban.dto.GameSnapshot;
import sokoban.dto.GameStatus;
import sokoban.dto.TipoPowerUp;
import sokoban.model.event.GameListener;
import sokoban.model.history.MoveHistory;
import sokoban.model.powerup.AccionesJugador;
import sokoban.model.powerup.JugadorBase;
import sokoban.model.powerup.PowerUpDecorador;

public class Game {

    private final List<GameListener> listeners = new ArrayList<>();
    private final MoveHistory history = new MoveHistory();
    private final List<PowerUpDecorador> powerUpsActivos = new ArrayList<>();
    private AccionesJugador accionesJugador;
    private Tablero board;
    private int level;
    private int moves;
    private int pushes;
    private GameStatus status = GameStatus.NO_LEVEL;

    public void addListener(GameListener listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public void startLevel(Tablero newBoard, int levelNumber) {
        board = Objects.requireNonNull(newBoard);
        level = levelNumber;
        moves = 0;
        pushes = 0;
        history.clear();
        reconstruirPoderes(List.of());
        status = GameStatus.PLAYING;
        notifyListeners();
    }

    public void move(Direction direction) {
        ejecutar(() -> accionesJugador.mover(direction));
    }

    public void romperMuro(Direction direction) {
        ejecutar(() -> accionesJugador.romperMuro(direction) ? MoveResult.WALL_BROKEN : MoveResult.BLOCKED);
    }

    public void jalarCaja(Direction direction) {
        ejecutar(() -> accionesJugador.jalarCaja(direction) ? MoveResult.PULLED : MoveResult.BLOCKED);
    }

    public void undo() {
        if (!canUndo()) {
            return;
        }
        history.undo().ifPresent(this::restore);
        status = nextStatus();
        notifyListeners();
    }

    public boolean isLevelCompleted() {
        return status == GameStatus.LEVEL_COMPLETED;
    }

    public GameSnapshot snapshot() {
        return new GameSnapshot(level, moves, pushes, history.totalUses(), history.remainingUses(), canUndo(), status,
                board == null ? null : board.snapshot(),
                board == null ? List.of() : board.powerUpsEnMapa(), estadosActivos());
    }

    private void ejecutar(Supplier<MoveResult> accion) {
        if (status != GameStatus.PLAYING) {
            return;
        }
        BoardMemento previousState = board.crearMemento(moves, pushes, estadosActivos());
        MoveResult result = accion.get();
        if (result == MoveResult.BLOCKED) {
            return;
        }
        history.record(previousState);
        moves++;
        if (result == MoveResult.PUSHED) {
            pushes++;
        }
        avanzarTurnos();
        if (result.desplazaAlJugador()) {
            board.recogerPowerUpEnJugador().ifPresent(this::activar);
        }
        status = nextStatus();
        notifyListeners();
    }

    private void avanzarTurnos() {
        powerUpsActivos.forEach(PowerUpDecorador::avanzarTurno);
        if (powerUpsActivos.stream().anyMatch(poder -> !poder.estaActivo())) {
            reconstruirPoderes(estadosActivos());
        }
    }

    private void activar(TipoPowerUp tipo) {
        List<EstadoPowerUp> estados = new ArrayList<>(estadosActivos());
        estados.removeIf(estado -> estado.tipo() == tipo);
        estados.add(tipo.estadoInicial());
        reconstruirPoderes(estados);
    }

    private List<EstadoPowerUp> estadosActivos() {
        return powerUpsActivos.stream().filter(PowerUpDecorador::estaActivo).map(PowerUpDecorador::estado).toList();
    }

    private void reconstruirPoderes(List<EstadoPowerUp> estados) {
        powerUpsActivos.clear();
        accionesJugador = new JugadorBase(board);
        for (EstadoPowerUp estado : estados) {
            PowerUpDecorador poder = PowerUpDecorador.desde(estado, accionesJugador, board);
            powerUpsActivos.add(poder);
            accionesJugador = poder;
        }
    }

    private void restore(BoardMemento memento) {
        board.restaurar(memento);
        moves = memento.moves();
        pushes = memento.pushes();
        reconstruirPoderes(memento.activePowerUps());
    }

    private boolean canUndo() {
        boolean levelInProgress = status == GameStatus.PLAYING || status == GameStatus.UNSOLVABLE;
        return levelInProgress && history.canUndo();
    }

    private GameStatus nextStatus() {
        if (board.estaResuelto()) {
            return GameStatus.LEVEL_COMPLETED;
        }
        return board.tieneCajasSuficientes() ? GameStatus.PLAYING : GameStatus.UNSOLVABLE;
    }

    private void notifyListeners() {
        GameSnapshot snapshot = snapshot();
        for (GameListener listener : listeners) {
            listener.onGameChanged(snapshot);
        }
    }
}
