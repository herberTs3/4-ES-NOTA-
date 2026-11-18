package sokoban.model.event;

import sokoban.dto.GameSnapshot;

public interface GameListener {

    void onGameChanged(GameSnapshot snapshot);
}
