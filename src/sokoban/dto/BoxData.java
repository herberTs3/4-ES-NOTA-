package sokoban.dto;

/** Datos de vista; estadoFragil es null para cajas que no son fragiles. */
public record BoxData(BoxType type, CellPosition position, int resistance, boolean inPlace,
                      char label, EstadoVisualCajaFragil estadoFragil) {

    /** Constructor original: mantiene las llamadas existentes de CajaNormal y CajaLlave. */
    public BoxData(BoxType type, CellPosition position, int resistance, boolean inPlace, char label) {
        this(type, position, resistance, inPlace, label, null);
    }
}
