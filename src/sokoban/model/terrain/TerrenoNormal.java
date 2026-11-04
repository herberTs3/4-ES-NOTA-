package sokoban.model.terrain;

import sokoban.dto.TileData;
import sokoban.dto.TileType;

public class TerrenoNormal extends Terreno {

    @Override
    public boolean esTransitable() {
        return true;
    }

    @Override
    public TileData toData() {
        return TileData.of(TileType.FLOOR);
    }
}
