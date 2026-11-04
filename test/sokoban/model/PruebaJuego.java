package sokoban.model;

import java.nio.file.Path;
import java.util.List;

import sokoban.dto.BoxData;
import sokoban.dto.BoxType;
import sokoban.dto.CellPosition;
import sokoban.dto.GameSnapshot;
import sokoban.dto.GameStatus;
import sokoban.dto.TileType;
import sokoban.level.CargaDeNivelException;
import sokoban.level.CargarNivelPorTxt;

public class PruebaJuego {

    private static final CargarNivelPorTxt CARGADOR = new CargarNivelPorTxt(Path.of("levels"));
    private static final List<String> PASILLO = List.of("#########", "#@      #", "#$.     #", "#########");

    private static int pasaron = 0;
    private static int fallaron = 0;

    public static void main(String[] args) throws Exception {
        losNivelesRealesSeGananJugando();
        elMovimientoRespetaLasReglas();
        lasCajasNoAtraviesanNada();
        elHieloDeslizaSoloCajas();
        laCajaFragilSeRompe();
        elCerrojoAbreSusMuros();
        laLlaveCuentaParaGanar();
        unaCajaDeRepuestoNoImpideGanar();
        laFotoDelJuegoNoExponeElModelo();
        deshacerVuelveCincoMovimientos();
        deshacerRespetaHistorialYUsosConsecutivos();
        deshacerRestauraCajasYMuros();
        deshacerNoRestauraContadores();
        reiniciarLimpiaElHistorial();

        System.out.printf("%n%d pruebas OK, %d fallidas%n", pasaron, fallaron);
        System.exit(fallaron == 0 ? 0 : 1);
    }

    static void losNivelesRealesSeGananJugando() throws Exception {
        verificarSolucion(1, "DLLUDLLU", 8, 2);
        verificarSolucion(2, "URDDRR", 6, 3);
        verificarSolucion(3, "ULURRLDRRRRDRULUURRDDRDL", 24, 9);
    }

    static void verificarSolucion(int nivel, String jugadas, int movimientos, int empujes) throws Exception {
        Game juego = new Game();
        juego.startLevel(CARGADOR.cargarNivel(nivel), nivel);
        jugar(juego, jugadas);
        GameSnapshot foto = juego.snapshot();
        verificar("el nivel " + nivel + " se gana (" + foto.moves() + " movimientos, " + foto.pushes() + " empujes)",
                juego.isLevelCompleted() && foto.moves() == movimientos && foto.pushes() == empujes);
    }

    static void elMovimientoRespetaLasReglas() throws Exception {
        Game juego = iniciar("######", "#@ $.#", "######");
        jugar(juego, "U");
        verificar("chocar una pared no cuenta como movimiento", juego.snapshot().moves() == 0);
        jugar(juego, "R");
        verificar("caminar suma movimiento y no empuje",
                juego.snapshot().moves() == 1 && juego.snapshot().pushes() == 0);
        jugar(juego, "R");
        verificar("empujar suma movimiento y empuje",
                juego.snapshot().moves() == 2 && juego.snapshot().pushes() == 1);
        verificar("la caja sobre el destino gana el nivel", juego.isLevelCompleted());
        jugar(juego, "L");
        verificar("con el nivel completado no se mueve", juego.snapshot().moves() == 2);
    }

    static void lasCajasNoAtraviesanNada() throws Exception {
        Game dosCajas = iniciar("#######", "#@$$..#", "#######");
        jugar(dosCajas, "R");
        verificar("no se empujan dos cajas a la vez", dosCajas.snapshot().moves() == 0);

        Game contraPared = iniciar("#####", "#.  #", "#@$ #", "#####");
        jugar(contraPared, "R");
        verificar("la caja llega hasta la pared", contraPared.snapshot().pushes() == 1);
        jugar(contraPared, "R");
        verificar("no se empuja una caja contra la pared", contraPared.snapshot().moves() == 1);

        Game muroCerrado = iniciar("#########", "#K a    #", "#@$ A.  #", "#########");
        jugar(muroCerrado, "R");
        verificar("la caja se empuja hasta el muro cerrado", muroCerrado.snapshot().pushes() == 1);
        jugar(muroCerrado, "R");
        verificar("no se empuja una caja contra un muro cerrado", muroCerrado.snapshot().pushes() == 1);
    }

    static void elHieloDeslizaSoloCajas() throws Exception {
        Game juego = iniciar("#########", "#@$~~~ .#", "#########");
        jugar(juego, "R");
        verificar("la caja se desliza hasta salir del hielo", posicionDeCaja(juego, BoxType.NORMAL).column() == 6);
        verificar("el jugador no se desliza", juego.snapshot().board().player().column() == 2);
        jugar(juego, "R");
        verificar("el jugador camina sobre el hielo sin resbalar", juego.snapshot().board().player().column() == 3);

        Game choque = iniciar("#########", "#@$~~$..#", "#########");
        jugar(choque, "R");
        verificar("la caja deslizada se detiene contra otra caja",
                choque.snapshot().board().boxes().stream().anyMatch(caja -> caja.position().column() == 4));
    }

    static void laCajaFragilSeRompe() throws Exception {
        Game juego = iniciar("#######", "#@2  .#", "#######");
        jugar(juego, "R");
        verificar("cada empuje baja la resistencia", juego.snapshot().board().boxes().get(0).resistance() == 1);
        jugar(juego, "R");
        verificar("en cero la caja desaparece", juego.snapshot().board().boxes().isEmpty());
        verificar("sin cajas suficientes el nivel no tiene solución",
                juego.snapshot().status() == GameStatus.UNSOLVABLE);
        int movimientos = juego.snapshot().moves();
        jugar(juego, "R");
        verificar("sin solución ya no se mueve", juego.snapshot().moves() == movimientos);
    }

    static void elCerrojoAbreSusMuros() throws Exception {
        Game juego = iniciar("##########", "#@K a  A #", "#  $ . A #", "#     B  #", "#     b  #", "##########");
        verificar("los muros arrancan cerrados", !abierto(juego, 1, 7) && !abierto(juego, 2, 7));
        jugar(juego, "R");
        verificar("la llave fuera del cerrojo no abre", !abierto(juego, 1, 7));
        jugar(juego, "R");
        verificar("la llave sobre el cerrojo abre todos los muros de su letra",
                abierto(juego, 1, 7) && abierto(juego, 2, 7));
        verificar("no abre los muros de otra letra", !abierto(juego, 3, 6));
        jugar(juego, "R");
        verificar("si la llave sale del cerrojo el muro sigue abierto", abierto(juego, 1, 7));
    }

    static void laLlaveCuentaParaGanar() throws Exception {
        Game juego = iniciar("########", "#@$.   #", "#  K a #", "#    A #", "########");
        jugar(juego, "R");
        verificar("con la llave fuera del cerrojo no se gana", !juego.isLevelCompleted());
        jugar(juego, "DRR");
        verificar("con la llave en el cerrojo se gana", juego.isLevelCompleted());
    }

    static void unaCajaDeRepuestoNoImpideGanar() throws Exception {
        Game juego = iniciar("#######", "#@$.  #", "#  3  #", "#######");
        jugar(juego, "R");
        verificar("una caja de repuesto no impide ganar", juego.isLevelCompleted());
    }

    static void laFotoDelJuegoNoExponeElModelo() throws Exception {
        Game juego = iniciar("#####", "#@$.#", "#####");
        GameSnapshot foto = juego.snapshot();
        jugar(juego, "R");
        verificar("la foto vieja no cambia cuando el juego avanza", foto.moves() == 0
                && foto.board().player().column() == 1);
        try {
            foto.board().boxes().remove(0);
            verificar("la lista de cajas de la foto es de solo lectura", false);
        } catch (UnsupportedOperationException e) {
            verificar("la lista de cajas de la foto es de solo lectura", true);
        }
        verificar("la foto informa el tipo de terreno",
                foto.board().tileAt(1, 3).type() == TileType.GOAL && foto.board().countGoals() == 1);
    }

    static void deshacerVuelveCincoMovimientos() throws Exception {
        Game juego = iniciarPasillo();
        verificar("sin movimientos no se puede deshacer", !juego.snapshot().canUndo());
        juego.undo();
        verificar("deshacer sin historial no gasta usos", juego.snapshot().undoUses() == 0);
        jugar(juego, "RRRRRR");
        juego.undo();
        verificar("deshacer vuelve 5 movimientos", columnaJugador(juego) == 2);
        juego.undo();
        verificar("con menos de 5 movimientos vuelve al inicio", columnaJugador(juego) == 1);
    }

    static void deshacerRespetaHistorialYUsosConsecutivos() throws Exception {
        Game juego = iniciarPasillo();
        jugar(juego, "RRRRRRLLLLLLRRRRRRLL");
        juego.undo();
        verificar("primer uso: vuelve al movimiento 15", columnaJugador(juego) == 4);
        juego.undo();
        verificar("segundo uso: vuelve al movimiento 10", columnaJugador(juego) == 3);
        juego.undo();
        verificar("tercer uso: vuelve al movimiento 5", columnaJugador(juego) == 6);
        verificar("después de 3 usos no quedan usos", juego.snapshot().remainingUndos() == 0
                && !juego.snapshot().canUndo());
        juego.undo();
        verificar("el cuarto uso no hace nada", columnaJugador(juego) == 6 && juego.snapshot().undoUses() == 3);
        jugar(juego, "R");
        verificar("un movimiento nuevo reinicia los usos", juego.snapshot().remainingUndos() == 3
                && juego.snapshot().canUndo());
    }

    static void deshacerRestauraCajasYMuros() throws Exception {
        Game fragil = iniciar("#######", "#@2  .#", "#######");
        jugar(fragil, "RR");
        verificar("la frágil se rompió", fragil.snapshot().status() == GameStatus.UNSOLVABLE);
        verificar("sin solución todavía se puede deshacer", fragil.snapshot().canUndo());
        fragil.undo();
        BoxData caja = fragil.snapshot().board().boxes().get(0);
        verificar("deshacer devuelve la frágil con su resistencia", caja.resistance() == 2
                && caja.position().column() == 2);
        verificar("deshacer vuelve a estar jugando", fragil.snapshot().status() == GameStatus.PLAYING);

        Game cerrojo = iniciar("#########", "#@K a  A#", "#  $ .  #", "#########");
        jugar(cerrojo, "RR");
        verificar("el muro se abrió", abierto(cerrojo, 1, 7));
        cerrojo.undo();
        verificar("deshacer vuelve a cerrar el muro", !abierto(cerrojo, 1, 7) && !abierto(cerrojo, 1, 4));
        jugar(cerrojo, "RR");
        verificar("el cerrojo se puede volver a abrir", abierto(cerrojo, 1, 7));
    }

    static void deshacerNoRestauraContadores() throws Exception {
        Game juego = iniciar("#######", "#@$  .#", "#     #", "#######");
        jugar(juego, "RR");
        juego.undo();
        GameSnapshot foto = juego.snapshot();
        verificar("deshacer no descuenta movimientos ni empujes", foto.moves() == 2 && foto.pushes() == 2);
        verificar("deshacer cuenta el uso", foto.undoUses() == 1);
        verificar("deshacer devuelve la caja", posicionDeCaja(juego, BoxType.NORMAL).column() == 2);
    }

    static void reiniciarLimpiaElHistorial() throws Exception {
        Game juego = iniciarPasillo();
        jugar(juego, "RR");
        juego.undo();
        juego.startLevel(CARGADOR.cargarDesdeLineas(PASILLO), 1);
        GameSnapshot foto = juego.snapshot();
        verificar("reiniciar limpia usos e historial", foto.undoUses() == 0 && foto.remainingUndos() == 3
                && !foto.canUndo());
    }

    private static Game iniciarPasillo() throws CargaDeNivelException {
        return iniciar(PASILLO.toArray(String[]::new));
    }

    private static int columnaJugador(Game juego) {
        return juego.snapshot().board().player().column();
    }

    private static Game iniciar(String... filas) throws CargaDeNivelException {
        Game juego = new Game();
        juego.startLevel(CARGADOR.cargarDesdeLineas(List.of(filas)), 1);
        return juego;
    }

    private static void jugar(Game juego, String jugadas) {
        for (char jugada : jugadas.toCharArray()) {
            juego.move(switch (jugada) {
                case 'U' -> Direction.UP;
                case 'D' -> Direction.DOWN;
                case 'L' -> Direction.LEFT;
                default -> Direction.RIGHT;
            });
        }
    }

    private static boolean abierto(Game juego, int fila, int columna) {
        return juego.snapshot().board().tileAt(fila, columna).open();
    }

    private static CellPosition posicionDeCaja(Game juego, BoxType tipo) {
        return juego.snapshot().board().boxes().stream().filter(caja -> caja.type() == tipo)
                .map(BoxData::position).findFirst().orElseThrow();
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            pasaron++;
            System.out.println("  OK   " + descripcion);
        } else {
            fallaron++;
            System.out.println("  FALLA " + descripcion);
        }
    }
}
