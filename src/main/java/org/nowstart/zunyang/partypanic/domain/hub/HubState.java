package org.nowstart.zunyang.partypanic.domain.hub;

import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public record HubState(
    HubLayout layout,
    HubActor actor,
    ChapterId activeHotspot,
    String currentMessage
) {

    private static final String EMPTY_INTERACTION_MESSAGE = "여긴 지금 건드릴 게 없다.";

    public static HubState initial(HubLayout layout) {
        return new HubState(
            layout,
            new HubActor(layout.startPosition(), Direction.UP),
            null,
            null
        );
    }

    public HubState move(Direction direction) {
        HubActor turnedActor = actor.face(direction);
        Position nextPosition = turnedActor.position().translate(direction);
        HubActor nextActor = layout.isWalkable(nextPosition)
            ? turnedActor.moveTo(nextPosition)
            : turnedActor;
        return new HubState(layout, nextActor, null, null);
    }

    public HubState interact(RunProgress runProgress) {
        return layout.hotspotAt(actor.frontPosition())
            .map(hotspot -> runProgress.isUnlocked(hotspot.chapterId())
                ? new HubState(
                    layout,
                    actor,
                    hotspot.chapterId(),
                    hotspot.interactionText()
                )
                : new HubState(
                    layout,
                    actor,
                    null,
                    hotspot.lockedText()
                ))
            .orElseGet(() -> new HubState(layout, actor, null, EMPTY_INTERACTION_MESSAGE));
    }
}
