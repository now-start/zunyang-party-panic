package org.nowstart.zunyang.partypanic.application.dto;

import org.nowstart.zunyang.partypanic.domain.model.GameState;

public record MovePlayerResult(
        GameState state,
        boolean moved
) {
}
