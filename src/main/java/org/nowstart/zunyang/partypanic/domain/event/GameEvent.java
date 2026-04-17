package org.nowstart.zunyang.partypanic.domain.event;

import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.model.Dialogue;
import org.nowstart.zunyang.partypanic.domain.model.Position;

public interface GameEvent {
    ActivityId activityId();

    String title();

    Dialogue lockedDialogue();

    Dialogue interactionDialogue();

    EventVisual visual();

    Position position();
}
