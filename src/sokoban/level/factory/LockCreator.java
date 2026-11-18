package sokoban.level.factory;

import sokoban.model.terrain.Lock;
import sokoban.model.terrain.Terreno;

public class LockCreator implements CreadorTerreno {

    @Override
    public boolean reconoce(char simbolo) {
        return simbolo >= 'a' && simbolo <= 'e';
    }

    @Override
    public Terreno crear(char simbolo) {
        return new Lock(simbolo);
    }
}
