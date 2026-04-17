package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.model.GameState;

public interface GameStatePort {
    GameState load();

    void save(GameState state);
}
