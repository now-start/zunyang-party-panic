package org.nowstart.zunyang.partypanic.application.port.in;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveCenterpieceActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.CenterpieceTableViewResult;

public interface MoveCenterpieceActorUseCase {

    CenterpieceTableViewResult move(MoveCenterpieceActorCommand command);
}
