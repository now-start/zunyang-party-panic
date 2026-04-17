package org.nowstart.zunyang.partypanic.domain.policy;

import org.nowstart.zunyang.partypanic.domain.model.GameMap;
import org.nowstart.zunyang.partypanic.domain.model.Position;

public final class MovementPolicy {
    public boolean canMove(GameMap map, Position target) {
        return map.isWalkable(target);
    }
}
