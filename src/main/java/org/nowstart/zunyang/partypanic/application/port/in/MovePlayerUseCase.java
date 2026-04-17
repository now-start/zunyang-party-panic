package org.nowstart.zunyang.partypanic.application.port.in;

import org.nowstart.zunyang.partypanic.application.dto.MovePlayerCommand;
import org.nowstart.zunyang.partypanic.application.dto.MovePlayerResult;

public interface MovePlayerUseCase {
    MovePlayerResult move(MovePlayerCommand command);
}
