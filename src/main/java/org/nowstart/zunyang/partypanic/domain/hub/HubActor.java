package org.nowstart.zunyang.partypanic.domain.hub;

import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.Position;

public record HubActor(
    Position position,
    Direction facing
) {

    public HubActor face(Direction direction) {
        return new HubActor(position, direction);
    }

    public HubActor moveTo(Position nextPosition) {
        return new HubActor(nextPosition, facing);
    }

    public Position frontPosition() {
        return position.translate(facing);
    }
}
