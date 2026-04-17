package org.nowstart.zunyang.partypanic.application.port.in;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveSignalActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.SignalConsoleViewResult;

public interface MoveSignalActorUseCase {

    SignalConsoleViewResult move(MoveSignalActorCommand command);
}
