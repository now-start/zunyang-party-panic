package org.nowstart.zunyang.partypanic.adapter.out.state;

import lombok.AllArgsConstructor;
import org.nowstart.zunyang.partypanic.application.port.out.GameStatePort;
import org.nowstart.zunyang.partypanic.domain.model.GameState;

@AllArgsConstructor
public final class InMemoryGameStateAdapter implements GameStatePort {
    private GameState state;

    @Override
    public GameState load() {
        return state;
    }

    @Override
    public void save(GameState state) {
        this.state = state;
    }
}
