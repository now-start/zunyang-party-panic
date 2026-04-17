package org.nowstart.zunyang.partypanic.application.signal;

import org.nowstart.zunyang.partypanic.application.dto.result.SignalConsoleViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartSignalConsoleUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;

public final class StartSignalConsoleInteractor implements StartSignalConsoleUseCase {

    private final SaveSignalConsoleStatePort saveSignalConsoleStatePort;

    public StartSignalConsoleInteractor(SaveSignalConsoleStatePort saveSignalConsoleStatePort) {
        this.saveSignalConsoleStatePort = saveSignalConsoleStatePort;
    }

    @Override
    public SignalConsoleViewResult start() {
        SignalConsoleState state = SignalConsoleState.initial();
        saveSignalConsoleStatePort.save(state);
        return SignalConsoleViewMapper.toView(state);
    }
}
