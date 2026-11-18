package sokoban.model.box;

import sokoban.dto.BoxData;
import sokoban.dto.BoxType;
import sokoban.model.Posicion;
import sokoban.model.terrain.Terreno;

public class CajaLlave extends Caja {

    private final char letra;

    public CajaLlave(Posicion posicion, char letra) {
        super(posicion);
        this.letra = letra;
    }

    public char getLetra() {
        return letra;
    }

    @Override
    public Caja copiar() {
        return new CajaLlave(getPosicion(), letra);
    }

    @Override
    public boolean abreCerrojo(char letraDelCerrojo) {
        return letra == letraDelCerrojo;
    }

    @Override
    public boolean requiereDestino() {
        return false;
    }

    @Override
    public boolean estaEnLugar(Terreno terreno) {
        return terreno.esCerrojoDe(letra);
    }

    @Override
    public BoxData toData(boolean enLugar) {
        return new BoxData(BoxType.KEY, getPosicion().toData(), 0, enLugar, letra);
    }
}
