package sokoban.controller;

import java.util.List;

import sokoban.dto.GameSnapshot;
import sokoban.dto.LevelOption;
import sokoban.model.puntaje.ResultadoPuntaje;

public interface VistaJuego {

    void mostrarMenu();

    void mostrarSelector(List<LevelOption> opciones);

    void mostrarCreditos();

    void mostrarJuego();

    void mostrarError(int numeroDeNivel, String motivo);

    void mostrarNivelCompletado(GameSnapshot resumen, ResultadoPuntaje puntaje);

    void mostrarJuegoTerminado();
}
