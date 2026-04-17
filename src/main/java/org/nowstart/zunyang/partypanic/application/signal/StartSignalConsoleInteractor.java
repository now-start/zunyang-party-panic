package org.nowstart.zunyang.partypanic.application.signal;

import org.nowstart.zunyang.partypanic.application.dto.result.SignalConsoleViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartSignalConsoleUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadGridActivityLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.domain.signal.SignalControlId;
import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;

public final class StartSignalConsoleInteractor implements StartSignalConsoleUseCase {

    private final LoadGridActivityLayoutPort<SignalControlId> loadGridActivityLayoutPort;
    private final SaveSignalConsoleStatePort saveSignalConsoleStatePort;

    public StartSignalConsoleInteractor(
        LoadGridActivityLayoutPort<SignalControlId> loadGridActivityLayoutPort,
        SaveSignalConsoleStatePort saveSignalConsoleStatePort
    ) {
        this.loadGridActivityLayoutPort = loadGridActivityLayoutPort;
        this.saveSignalConsoleStatePort = saveSignalConsoleStatePort;
    }

    @Override
    public SignalConsoleViewResult start() {
        SignalConsoleState state = SignalConsoleState.initial(loadGridActivityLayoutPort.load());
        saveSignalConsoleStatePort.save(state);
        return SignalConsoleViewMapper.toView(state);
    }
}
