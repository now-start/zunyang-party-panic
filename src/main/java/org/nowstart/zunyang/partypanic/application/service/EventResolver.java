package org.nowstart.zunyang.partypanic.application.service;

import org.nowstart.zunyang.partypanic.domain.event.GameEvent;
import org.nowstart.zunyang.partypanic.domain.model.GameState;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

import java.util.Optional;

public final class EventResolver {
    public Optional<GameEvent> findFacingEvent(GameState state) {
        return Optional.ofNullable(
                state.gameMap().eventAt(state.player().position().translate(state.player().facing()))
        );
    }

    public Optional<GameEvent> findSuggestedEvent(GameState state, GameProgress progress) {
        return state.gameMap().events().stream()
                .filter(event -> !progress.isCompleted(event.activityId()) && progress.isUnlocked(event.activityId()))
                .findFirst();
    }
}
