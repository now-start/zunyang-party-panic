package org.nowstart.zunyang.partypanic.application.dto;

import org.nowstart.zunyang.partypanic.domain.model.Direction;

public record MovePlayerCommand(Direction direction, boolean attemptMove) {
    public MovePlayerCommand(Direction direction) {
        this(direction, true);
    }
}
