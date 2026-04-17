package org.nowstart.zunyang.partypanic.application.usecase;

import lombok.RequiredArgsConstructor;
import org.nowstart.zunyang.partypanic.application.dto.InteractResult;
import org.nowstart.zunyang.partypanic.application.port.in.InteractUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.GameStatePort;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.event.GameEvent;
import org.nowstart.zunyang.partypanic.domain.model.GameState;
import org.nowstart.zunyang.partypanic.domain.policy.EventResolver;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

import java.util.Optional;

@RequiredArgsConstructor
public final class InteractInteractor implements InteractUseCase {
    private final GameStatePort gameStatePort;
    private final EventResolver eventResolver;
    private final GameProgress progress;

    @Override
    public InteractResult interact() {
        GameState state = gameStatePort.load();
        Optional<GameEvent> event = eventResolver.findFacingEvent(state);
        if (event.isEmpty()) {
            return new InteractResult(state, false, false, null);
        }

        GameEvent facingEvent = event.get();
        boolean unlocked = progress.isUnlocked(facingEvent.activityId());
        ActivityId pendingActivityId = unlocked ? facingEvent.activityId() : null;
        GameState updatedState = state.startDialogue(
                unlocked ? facingEvent.interactionDialogue() : facingEvent.lockedDialogue(),
                pendingActivityId
        );
        gameStatePort.save(updatedState);
        return new InteractResult(updatedState, true, unlocked, pendingActivityId);
    }
}
