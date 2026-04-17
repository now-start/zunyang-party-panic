package org.nowstart.zunyang.partypanic.domain.event;

import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.model.Dialogue;
import org.nowstart.zunyang.partypanic.domain.model.Position;

public record DialogueEvent(
        ActivityId activityId,
        String title,
        Dialogue lockedDialogue,
        Dialogue interactionDialogue,
        EventVisual visual,
        Position position
) implements GameEvent {
}
