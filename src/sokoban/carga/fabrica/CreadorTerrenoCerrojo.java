package sokoban.carga.fabrica;

import sokoban.modelo.terreno.Terreno;
import sokoban.modelo.terreno.TerrenoCerrojo;

/**
 * Creador concreto de {@link TerrenoCerrojo}: un <b>creador parametrizado por el símbolo</b>.
 *
 * <p>Símbolos reconocidos: las mayúsculas {@code A-Z}. La letra no solo dice "esto es un
 * cerrojo": además es su <b>clave</b> ({@code A}=1, {@code B}=2, ... {@code Z}=26). Una
 * {@code CajaLlave} con la misma clave (la minúscula equivalente) lo abre.</p>
 *
 * <p>Este es el motivo de que {@link CreadorTerreno#crear(char)} reciba el símbolo: un solo
 * creador atiende a las 26 letras en lugar de necesitar una clase por cada una.</p>
 *
 * <p>Cada llamada devuelve un cerrojo <b>nuevo</b>, porque el cerrojo tiene estado
 * (abierto/cerrado) y dos cerrojos con la misma clave deben abrirse de forma independiente
 * en la celda que les corresponde.</p>
 */
public class CreadorTerrenoCerrojo implements CreadorTerreno {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo >= 'A' && simbolo <= 'Z';
    }

    @Override
    public Terreno crear(char simbolo) {
        int clave = simbolo - 'A' + 1;
        return new TerrenoCerrojo(clave);
    }
}
