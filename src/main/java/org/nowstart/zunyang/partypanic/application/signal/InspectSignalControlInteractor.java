package org.nowstart.zunyang.partypanic.application.signal;

import org.nowstart.zunyang.partypanic.application.dto.result.SignalConsoleViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.InspectSignalControlUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;

public final class InspectSignalControlInteractor implements InspectSignalControlUseCase {

    private final LoadSignalConsoleStatePort loadSignalConsoleStatePort;
    private final SaveSignalConsoleStatePort saveSignalConsoleStatePort;

    public InspectSignalControlInteractor(
        LoadSignalConsoleStatePort loadSignalConsoleStatePort,
        SaveSignalConsoleStatePort saveSignalConsoleStatePort
    ) {
        this.loadSignalConsoleStatePort = loadSignalConsoleStatePort;
        this.saveSignalConsoleStatePort = saveSignalConsoleStatePort;
    }

    @Override
    public SignalConsoleViewResult inspect() {
        SignalConsoleState currentState = loadSignalConsoleStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Signal console has not been started"));
        SignalConsoleState nextState = currentState.inspect();
        saveSignalConsoleStatePort.save(nextState);
        return SignalConsoleViewMapper.toView(nextState);
    }
}
