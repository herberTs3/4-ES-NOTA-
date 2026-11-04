package sokoban;

import sokoban.carga.CargadorDeNivel;
import sokoban.carga.CargarNivelPorTxt;
import sokoban.carga.fabrica.RegistroDeCreadores;
import sokoban.controlador.Controlador;
import sokoban.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;
import java.nio.file.Path;

public class Main {

    /** Uso: java sokoban.Main [directorio_de_niveles]   (por defecto "niveles") */
    public static void main(String[] args) {
        Path directorio = Path.of(args.length > 0 ? args[0] : "niveles");

        // 1) Registro de creadores (Factory Method): el catálogo de "qué símbolo construye qué".
        //    Para sumar un tipo nuevo de terreno o caja se registra su creador acá, por ejemplo:
        //        registro.registrarTerreno(new CreadorMiTerreno());
        //    y no hace falta modificar el cargador. Ver README, sección "Cómo agregar un tipo nuevo".
        RegistroDeCreadores registro = RegistroDeCreadores.porDefecto();

        // 2) Cargador de niveles: lee los .txt y usa el registro para construir cada celda.
        //    Para cambiar el origen de los niveles basta con reemplazar esta línea por otro CargadorDeNivel.
        CargadorDeNivel cargador = new CargarNivelPorTxt(directorio, registro);

        // 3) Controlador: único punto de contacto entre la vista y el modelo/carga.
        SwingUtilities.invokeLater(() -> {
            Controlador controlador = new Controlador(cargador, 1);
            VentanaPrincipal ventana = new VentanaPrincipal(controlador);
            controlador.setVista(ventana);
            controlador.iniciar();
            ventana.setVisible(true);
        });
    }
}
