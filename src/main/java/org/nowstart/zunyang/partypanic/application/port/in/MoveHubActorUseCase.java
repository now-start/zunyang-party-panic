package org.nowstart.zunyang.partypanic.application.port.in;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveHubActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.HubViewResult;

public interface MoveHubActorUseCase {

    HubViewResult move(MoveHubActorCommand command);
}
