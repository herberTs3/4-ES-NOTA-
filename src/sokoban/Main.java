package sokoban;

import java.nio.file.Path;

import javax.swing.SwingUtilities;

import sokoban.controller.Controlador;
import sokoban.level.CargadorDeNivel;
import sokoban.level.CargarNivelPorTxt;
import sokoban.level.factory.RegistroDeCreadores;
import sokoban.model.Game;
import sokoban.view.VentanaPrincipal;

public class Main {

    private static final String DEFAULT_LEVELS_DIRECTORY = "levels";
    private static final int FIRST_LEVEL = 1;

    public static void main(String[] args) {
        Path directorio = Path.of(args.length > 0 ? args[0] : DEFAULT_LEVELS_DIRECTORY);
        CargadorDeNivel cargador = new CargarNivelPorTxt(directorio, RegistroDeCreadores.porDefecto());

        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            Controlador controlador = new Controlador(cargador, game, FIRST_LEVEL);
            VentanaPrincipal ventana = new VentanaPrincipal(controlador);
            game.addListener(ventana);
            controlador.setVista(ventana);
            controlador.iniciar();
            ventana.setVisible(true);
        });
    }
}
