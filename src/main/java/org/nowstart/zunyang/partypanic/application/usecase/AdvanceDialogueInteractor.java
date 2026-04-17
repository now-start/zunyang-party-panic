package org.nowstart.zunyang.partypanic.application.usecase;

import lombok.RequiredArgsConstructor;
import org.nowstart.zunyang.partypanic.application.dto.AdvanceDialogueResult;
import org.nowstart.zunyang.partypanic.application.port.in.AdvanceDialogueUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.GameStatePort;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.model.Dialogue;
import org.nowstart.zunyang.partypanic.domain.model.GameState;

@RequiredArgsConstructor
public final class AdvanceDialogueInteractor implements AdvanceDialogueUseCase {
    private final GameStatePort gameStatePort;

    @Override
    public AdvanceDialogueResult advance() {
        GameState state = gameStatePort.load();
        Dialogue activeDialogue = state.activeDialogue();
        if (activeDialogue == null) {
            return new AdvanceDialogueResult(state, false, null);
        }

        if (activeDialogue.hasNext()) {
            GameState updatedState = state.startDialogue(activeDialogue.advance(), state.pendingActivityId());
            gameStatePort.save(updatedState);
            return new AdvanceDialogueResult(updatedState, true, null);
        }

        ActivityId completedActivityId = state.pendingActivityId();
        GameState updatedState = state.clearDialogue();
        gameStatePort.save(updatedState);
        return new AdvanceDialogueResult(updatedState, false, completedActivityId);
    }
}
