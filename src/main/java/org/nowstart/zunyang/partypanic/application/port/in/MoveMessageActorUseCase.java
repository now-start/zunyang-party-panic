package org.nowstart.zunyang.partypanic.application.port.in;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveMessageActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.MessageWallViewResult;

public interface MoveMessageActorUseCase {

    MessageWallViewResult move(MoveMessageActorCommand command);
}
