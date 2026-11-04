package sokoban.carga.fabrica;

import sokoban.modelo.Posicion;
import sokoban.modelo.caja.Caja;
import sokoban.modelo.caja.CajaLlave;

/**
 * Creador concreto de {@link CajaLlave}, parametrizado por el símbolo.
 *
 * <p>Símbolos reconocidos: las minúsculas {@code a-z}. La letra define la clave
 * ({@code a}=1 ... {@code z}=26) y la caja abre el {@code TerrenoCerrojo} cuya clave coincide,
 * que en el archivo es la <i>misma letra en mayúscula</i>. Que las dos letras se correspondan
 * a simple vista es la razón de elegir este formato.</p>
 */
public class CreadorCajaLlave implements CreadorCaja {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo >= 'a' && simbolo <= 'z';
    }

    @Override
    public Caja crear(char simbolo, Posicion posicion) {
        int clave = simbolo - 'a' + 1;
        return new CajaLlave(posicion, clave);
    }
}
