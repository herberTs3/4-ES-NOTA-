package sokoban.model.box;

import sokoban.dto.BoxData;
import sokoban.dto.BoxType;
import sokoban.dto.EstadoVisualCajaFragil;
import sokoban.model.Posicion;
import sokoban.model.terrain.Terreno;

/** Context del patron State: conserva la resistencia y delega las reacciones al estado. */
public class CajaFragil extends Caja {

    private static final char SIN_LETRA = ' ';

    private final int resistenciaInicial;
    private int movimientosRestantes;
    private EstadoCajaFragil estado;

    public CajaFragil(Posicion posicion, int movimientosRestantes) {
        this(posicion, movimientosRestantes, movimientosRestantes);
    }

    private CajaFragil(Posicion posicion, int resistenciaInicial, int movimientosRestantes) {
        super(posicion);
        this.resistenciaInicial = resistenciaInicial;
        this.movimientosRestantes = movimientosRestantes;
        actualizarEstado();
    }

    public int getMovimientosRestantes() {
        return movimientosRestantes;
    }

    public int getResistenciaInicial() {
        return resistenciaInicial;
    }

    public EstadoVisualCajaFragil getEstadoVisual() {
        return estado.apariencia();
    }

    @Override
    public boolean puedeSerJalada() {
        return estado.permiteJalar();
    }

    @Override
    public boolean estaRota() {
        return estado.estaRota();
    }

    @Override
    protected void despuesDeMover() {
        estado.alSerEmpujada(this);
    }

    @Override
    protected void despuesDeJalar() {
        estado.alSerJalada(this);
    }

    /** Un movimiento valido consume exactamente una unidad de resistencia. */
    void consumirResistencia() {
        if (movimientosRestantes > 0) {
            movimientosRestantes--;
            actualizarEstado();
        }
    }

    private void actualizarEstado() {
        if (movimientosRestantes <= 0) {
            estado = new EstadoRota();
        } else if ((long) movimientosRestantes * 2 <= resistenciaInicial) {
            estado = new EstadoAgrietada();
        } else {
            estado = new EstadoIntacta();
        }
    }

    @Override
    public Caja copiar() {
        // Memento necesita los DOS valores: de otro modo se pierde el umbral del 50 %.
        return new CajaFragil(getPosicion(), resistenciaInicial, movimientosRestantes);
    }

    @Override
    public boolean requiereDestino() {
        return true;
    }

    @Override
    public boolean estaEnLugar(Terreno terreno) {
        return terreno.esDestino();
    }

    @Override
    public BoxData toData(boolean enLugar) {
        return new BoxData(BoxType.FRAGILE, getPosicion().toData(), movimientosRestantes, enLugar, SIN_LETRA,
                getEstadoVisual());
    }
}
