package org.nowstart.zunyang.partypanic.application.dto;

import org.nowstart.zunyang.partypanic.application.port.in.AdvanceDialogueUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InteractUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MovePlayerUseCase;
import org.nowstart.zunyang.partypanic.domain.model.GameState;
import org.nowstart.zunyang.partypanic.domain.policy.EventResolver;

public record HubContext(
        GameState initialState,
        EventResolver eventResolver,
        MovePlayerUseCase movePlayerUseCase,
        InteractUseCase interactUseCase,
        AdvanceDialogueUseCase advanceDialogueUseCase
) {
}
