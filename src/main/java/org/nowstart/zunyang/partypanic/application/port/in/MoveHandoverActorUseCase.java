package org.nowstart.zunyang.partypanic.application.port.in;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveHandoverActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.HandoverCorridorViewResult;

public interface MoveHandoverActorUseCase {

    HandoverCorridorViewResult move(MoveHandoverActorCommand command);
}
