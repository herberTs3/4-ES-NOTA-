package sokoban.model.terrain;

import sokoban.dto.TileData;
import sokoban.dto.TileType;

public class TerrenoResbaladizo extends Terreno {

    @Override
    public boolean esTransitable() {
        return true;
    }

    @Override
    public boolean esResbaladizo() {
        return true;
    }

    @Override
    public TileData toData() {
        return TileData.of(TileType.ICE);
    }
}
