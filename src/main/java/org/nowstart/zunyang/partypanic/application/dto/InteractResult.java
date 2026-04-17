package org.nowstart.zunyang.partypanic.application.dto;

import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.model.GameState;

public record InteractResult(
        GameState state,
        boolean startedDialogue,
        boolean unlocked,
        ActivityId pendingActivityId
) {
}
