package org.nowstart.zunyang.partypanic.application.port.in;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveFinaleActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.FinaleStageViewResult;

public interface MoveFinaleActorUseCase {

    FinaleStageViewResult move(MoveFinaleActorCommand command);
}
