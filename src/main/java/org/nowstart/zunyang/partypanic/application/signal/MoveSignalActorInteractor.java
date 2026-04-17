package org.nowstart.zunyang.partypanic.application.signal;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveSignalActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.SignalConsoleViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.MoveSignalActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;

public final class MoveSignalActorInteractor implements MoveSignalActorUseCase {

    private final LoadSignalConsoleStatePort loadSignalConsoleStatePort;
    private final SaveSignalConsoleStatePort saveSignalConsoleStatePort;

    public MoveSignalActorInteractor(
        LoadSignalConsoleStatePort loadSignalConsoleStatePort,
        SaveSignalConsoleStatePort saveSignalConsoleStatePort
    ) {
        this.loadSignalConsoleStatePort = loadSignalConsoleStatePort;
        this.saveSignalConsoleStatePort = saveSignalConsoleStatePort;
    }

    @Override
    public SignalConsoleViewResult move(MoveSignalActorCommand command) {
        SignalConsoleState currentState = loadSignalConsoleStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Signal console has not been started"));
        SignalConsoleState nextState = currentState.move(command.direction());
        saveSignalConsoleStatePort.save(nextState);
        return SignalConsoleViewMapper.toView(nextState);
    }
}
