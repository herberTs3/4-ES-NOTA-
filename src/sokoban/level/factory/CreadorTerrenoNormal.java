package sokoban.level.factory;

import sokoban.model.terrain.Terreno;
import sokoban.model.terrain.TerrenoNormal;

public class CreadorTerrenoNormal implements CreadorTerreno {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo == ' ' || simbolo == '-' || simbolo == '_' || simbolo == '@';
    }

    @Override
    public Terreno crear(char simbolo) {
        return new TerrenoNormal();
    }
}
