package sokoban.model.terrain;

import sokoban.dto.TileData;
import sokoban.dto.TileType;

public class Muro extends Terreno {

    @Override
    public boolean esTransitable() {
        return false;
    }

    @Override
    public boolean esMuro() {
        return true;
    }

    @Override
    public TileData toData() {
        return TileData.of(TileType.WALL);
    }
}
