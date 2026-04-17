package org.nowstart.zunyang.partypanic.application.port.in;

import org.nowstart.zunyang.partypanic.application.dto.command.MovePropsActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.PropsArchiveViewResult;

public interface MovePropsActorUseCase {

    PropsArchiveViewResult move(MovePropsActorCommand command);
}
