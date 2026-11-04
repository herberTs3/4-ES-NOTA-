package sokoban;

import java.net.URISyntaxException;
import java.nio.file.Files;
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
        Path directorio = args.length > 0 ? Path.of(args[0]) : findLevelsDirectory();
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

    private static Path findLevelsDirectory() {
        Path fromWorkingDirectory = Path.of(DEFAULT_LEVELS_DIRECTORY);
        if (Files.isDirectory(fromWorkingDirectory)) {
            return fromWorkingDirectory;
        }
        for (Path folder = codeLocation(); folder != null; folder = folder.getParent()) {
            Path candidate = folder.resolve(DEFAULT_LEVELS_DIRECTORY);
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }
        return fromWorkingDirectory;
    }

    private static Path codeLocation() {
        try {
            return Path.of(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
