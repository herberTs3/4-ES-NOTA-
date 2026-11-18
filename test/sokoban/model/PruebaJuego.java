package sokoban.model;

import java.nio.file.Path;
import java.util.List;

import sokoban.dto.BoxData;
import sokoban.dto.BoxType;
import sokoban.dto.CellPosition;
import sokoban.dto.EstadoPowerUp;
import sokoban.dto.GameSnapshot;
import sokoban.dto.GameStatus;
import sokoban.dto.TileType;
import sokoban.dto.TipoPowerUp;
import sokoban.level.CargaDeNivelException;
import sokoban.level.CargarNivelPorTxt;

public class PruebaJuego {

    private static final CargarNivelPorTxt CARGADOR = new CargarNivelPorTxt(Path.of("levels"));
    private static final List<String> PASILLO = List.of("#########", "#@      #", "#$.     #", "#########");
    private static final String SOLUCION_NIVEL_6 = "RRDDL" + "l" + "R" + ">" + "ULLLD" + "RRRRRR";
    private static final String SOLUCION_NIVEL_4 = "LUURR" + "URRDDRRDDR" + "UULLLUULLLDD" + "RRRR" + "RRRRR" + "DRU"
            + "DRRUULDD" + "LLULLLLLLDDL" + "ULURRR" + "RRRRRR" + "DRRUULD" + "RDL";
    private static final String SOLUCION_NIVEL_5 = "UURR" + "DRRR" + "R" + "URRRRD" + "RRRRR"
            + "LLLLLLLDDDDLLURRR" + "LUUULLLLLLULLD" + "RRRRRR" + "RRRRRR" + "DRU" + "LLLLLLLLLLL"
            + "DDDDRRULDLUUU" + "LU" + "RRRRR" + "RRRRRRR" + "DRRUULD";

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
        deshacerRespetaHistorialYLimiteDeUsos();
        deshacerRestauraCajasYMuros();
        deshacerRestauraContadores();
        reiniciarLimpiaElHistorial();
        losPoderesSeRecogenYDuran();
        elMartilloRompeSoloMurosInteriores();
        laSuperfuerzaJalaCajas();
        deshacerRestauraPoderesYMuros();
        unPoderExpiraYElOtroSigue();

        System.out.printf("%n%d pruebas OK, %d fallidas%n", pasaron, fallaron);
        System.exit(fallaron == 0 ? 0 : 1);
    }

    static void losNivelesRealesSeGananJugando() throws Exception {
        verificarSolucion(1, "DLLUDLLU", 8, 2);
        verificarSolucion(2, "URDDRR", 6, 3);
        verificarSolucion(3, "ULURRLDRRRRDRULUURRDDRDL", 24, 9);
        verificarSolucion(4, SOLUCION_NIVEL_4, 81, 21);
        verificarSolucion(5, SOLUCION_NIVEL_5, 111, 33);
        verificarSolucion(6, SOLUCION_NIVEL_6, 19, 6);
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
        Game juego = iniciar("##########", "#@K a  A #", "#  $ . A #", "#     B  #", "#  L  b  #", "##########");
        verificar("los muros arrancan cerrados", !abierto(juego, 1, 7) && !abierto(juego, 2, 7));
        jugar(juego, "R");
        verificar("la llave fuera del cerrojo no abre", !abierto(juego, 1, 7));
        jugar(juego, "R");
        verificar("la llave sobre el cerrojo abre todos los muros de su letra",
                abierto(juego, 1, 7) && abierto(juego, 2, 7));
        verificar("no abre los muros de otra letra", !abierto(juego, 3, 6));

        Game llaveAjena = iniciar("##########", "#@K b  B #", "#  $ .   #", "#  L a A #", "##########");
        jugar(llaveAjena, "RR");
        verificar("una llave no abre un cerrojo de otra letra", !abierto(llaveAjena, 1, 7));
        verificar("la llave muestra su letra", llaveAjena.snapshot().board().boxes().stream()
                .anyMatch(caja -> caja.type() == BoxType.KEY && caja.label() == 'a'));
        jugar(juego, "R");
        verificar("si la llave sale del cerrojo el muro sigue abierto", abierto(juego, 1, 7));
    }

    static void laLlaveCuentaParaGanar() throws Exception {
        Game juego = iniciar("########", "#@$.   #", "#  K a #", "#    A #", "########");
        jugar(juego, "R");
        verificar("con la llave fuera del cerrojo no se gana", !juego.isLevelCompleted());
        jugar(juego, "DRR");
        verificar("con la llave en el cerrojo se gana", juego.isLevelCompleted());

        Game llaveMovida = iniciar("#########", "#@K a  A#", "# $.    #", "#########");
        jugar(llaveMovida, "RRRLLLDR");
        verificar("con el cerrojo ya abierto, mover la llave no impide ganar", llaveMovida.isLevelCompleted());
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
        verificar("con 6 movimientos, deshacer deja 1", juego.snapshot().moves() == 1);
        juego.undo();
        verificar("con menos de 5 movimientos vuelve al inicio", columnaJugador(juego) == 1);
    }

    static void deshacerRespetaHistorialYLimiteDeUsos() throws Exception {
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
        verificar("un movimiento nuevo no devuelve usos", juego.snapshot().remainingUndos() == 0
                && !juego.snapshot().canUndo());
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

    static void deshacerRestauraContadores() throws Exception {
        Game juego = iniciar("#######", "#@$  .#", "#     #", "#######");
        jugar(juego, "RR");
        juego.undo();
        GameSnapshot foto = juego.snapshot();
        verificar("deshacer descuenta movimientos y empujes", foto.moves() == 0 && foto.pushes() == 0);
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

    static void losPoderesSeRecogenYDuran() throws Exception {
        Game juego = iniciar("#########", "#@H #  .#", "#   #   #", "#$F     #", "#########");
        verificar("P01: el nivel muestra los dos poderes en el mapa", juego.snapshot().powerUpsEnMapa().size() == 2);
        jugar(juego, "L");
        verificar("P04: un movimiento bloqueado no cuenta", juego.snapshot().moves() == 0);
        jugar(juego, "R");
        EstadoPowerUp martillo = poder(juego, TipoPowerUp.MARTILLO);
        verificar("P02 y P10: pisar H activa el martillo completo", martillo != null
                && martillo.turnosRestantes() == 10 && martillo.usosRestantes() == 3);
        verificar("P02: el H desaparece del mapa", juego.snapshot().powerUpsEnMapa().size() == 1);
        jugar(juego, "R");
        verificar("cada acción válida descuenta un turno", poder(juego, TipoPowerUp.MARTILLO).turnosRestantes() == 9);
        Tablero otro = CARGADOR.cargarDesdeLineas(List.of("#####", "#@ .#", "# $ #", "#####"));
        juego.startLevel(otro, 1);
        verificar("P13: reiniciar quita los poderes activos", juego.snapshot().powerUpsActivos().isEmpty());
    }

    static void elMartilloRompeSoloMurosInteriores() throws Exception {
        Game juego = iniciar("#########", "#@H #  .#", "#   #   #", "#$F     #", "#########");
        jugar(juego, "R^");
        verificar("P06: no rompe el muro del borde", juego.snapshot().moves() == 1
                && poder(juego, TipoPowerUp.MARTILLO).turnosRestantes() == 10);
        jugar(juego, "R>");
        EstadoPowerUp martillo = poder(juego, TipoPowerUp.MARTILLO);
        verificar("P05: rompe un muro interior", tipo(juego, 1, 4) == TileType.FLOOR);
        verificar("P05: consume un uso y un turno", martillo.usosRestantes() == 2 && martillo.turnosRestantes() == 8);
        verificar("romper no mueve al jugador", juego.snapshot().board().player().column() == 3);
        Game sinPoder = iniciar("#########", "#@ H#  .#", "#   #   #", "#$F     #", "#########");
        jugar(sinPoder, ">");
        verificar("sin martillo no se rompe nada", sinPoder.snapshot().moves() == 0);
    }

    static void laSuperfuerzaJalaCajas() throws Exception {
        Game bloqueada = iniciar("#######", "#F    #", "#$@# .#", "#######");
        jugar(bloqueada, "ULRDl");
        verificar("P08: no jala si atrás hay un muro", bloqueada.snapshot().moves() == 4
                && poder(bloqueada, TipoPowerUp.SUPERFUERZA).turnosRestantes() == 13);
        Game normal = iniciar("#########", "#@H #  .#", "#   #   #", "#$F     #", "#########");
        jugar(normal, "RDDl");
        BoxData caja = normal.snapshot().board().boxes().get(0);
        verificar("P07: el jugador retrocede y la caja lo sigue", caja.position().column() == 2
                && normal.snapshot().board().player().column() == 3);
        verificar("jalar no cuenta como empuje", normal.snapshot().pushes() == 0);
        Game fragil = iniciar("#######", "#F    #", "#2@  .#", "#######");
        jugar(fragil, "ULRDl");
        verificar("P12: jalar no gasta la resistencia de la frágil", fragil.snapshot().board().boxes().get(0)
                .resistance() == 2);
        Game llave = iniciar("#########", "#F@  $. #", "#Ka   A #", "#########");
        jugar(llave, "LRDl");
        verificar("P12: jalar la llave sobre su cerrojo abre el muro", abierto(llave, 2, 6));
    }

    static void deshacerRestauraPoderesYMuros() throws Exception {
        Game juego = iniciar("#########", "#@H #  .#", "#   #   #", "#$F     #", "#########");
        jugar(juego, "RR>");
        juego.undo();
        GameSnapshot foto = juego.snapshot();
        verificar("P11: el muro roto vuelve", tipo(juego, 1, 4) == TileType.WALL);
        verificar("P11: el martillo vuelve al mapa y deja de estar activo", foto.powerUpsEnMapa().size() == 2
                && foto.powerUpsActivos().isEmpty());
        jugar(juego, "RR>");
        verificar("P11: después de deshacer se puede volver a romper", tipo(juego, 1, 4) == TileType.FLOOR);
    }

    static void unPoderExpiraYElOtroSigue() throws Exception {
        Game juego = iniciar("##########", "#@HF     #", "#$.      #", "##########");
        jugar(juego, "RR" + "RLRLRLRLR");
        List<EstadoPowerUp> activos = juego.snapshot().powerUpsActivos();
        verificar("P09: el martillo expira y la superfuerza sigue", activos.size() == 1
                && activos.get(0).tipo() == TipoPowerUp.SUPERFUERZA && activos.get(0).turnosRestantes() == 6);
    }

    private static EstadoPowerUp poder(Game juego, TipoPowerUp tipo) {
        return juego.snapshot().powerUpsActivos().stream().filter(estado -> estado.tipo() == tipo).findFirst()
                .orElse(null);
    }

    private static TileType tipo(Game juego, int fila, int columna) {
        return juego.snapshot().board().tileAt(fila, columna).type();
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
            switch (jugada) {
                case 'U' -> juego.move(Direction.UP);
                case 'D' -> juego.move(Direction.DOWN);
                case 'L' -> juego.move(Direction.LEFT);
                case 'R' -> juego.move(Direction.RIGHT);
                case 'u' -> juego.jalarCaja(Direction.UP);
                case 'd' -> juego.jalarCaja(Direction.DOWN);
                case 'l' -> juego.jalarCaja(Direction.LEFT);
                case 'r' -> juego.jalarCaja(Direction.RIGHT);
                case '^' -> juego.romperMuro(Direction.UP);
                case 'v' -> juego.romperMuro(Direction.DOWN);
                case '<' -> juego.romperMuro(Direction.LEFT);
                default -> juego.romperMuro(Direction.RIGHT);
            }
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
