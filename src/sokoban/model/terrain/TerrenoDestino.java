package sokoban.model.terrain;

import sokoban.dto.TileData;
import sokoban.dto.TileType;

public class TerrenoDestino extends Terreno {

    @Override
    public boolean esTransitable() {
        return true;
    }

    @Override
    public boolean esDestino() {
        return true;
    }

    @Override
    public TileData toData() {
        return TileData.of(TileType.GOAL);
    }
}
