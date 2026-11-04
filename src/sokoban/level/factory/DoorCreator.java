package sokoban.level.factory;

import sokoban.model.terrain.Door;
import sokoban.model.terrain.Terreno;

public class DoorCreator implements CreadorTerreno {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo >= 'A' && simbolo <= 'E';
    }

    @Override
    public Terreno crear(char simbolo) {
        return new Door(simbolo);
    }
}
