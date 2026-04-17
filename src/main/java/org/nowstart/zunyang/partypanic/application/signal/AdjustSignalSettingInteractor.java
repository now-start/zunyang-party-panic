package org.nowstart.zunyang.partypanic.application.signal;

import org.nowstart.zunyang.partypanic.application.dto.command.AdjustSignalSettingCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.SignalConsoleViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.AdjustSignalSettingUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;

public final class AdjustSignalSettingInteractor implements AdjustSignalSettingUseCase {

    private final LoadSignalConsoleStatePort loadSignalConsoleStatePort;
    private final SaveSignalConsoleStatePort saveSignalConsoleStatePort;

    public AdjustSignalSettingInteractor(
        LoadSignalConsoleStatePort loadSignalConsoleStatePort,
        SaveSignalConsoleStatePort saveSignalConsoleStatePort
    ) {
        this.loadSignalConsoleStatePort = loadSignalConsoleStatePort;
        this.saveSignalConsoleStatePort = saveSignalConsoleStatePort;
    }

    @Override
    public SignalConsoleViewResult adjust(AdjustSignalSettingCommand command) {
        SignalConsoleState currentState = loadSignalConsoleStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Signal console has not been started"));
        SignalConsoleState nextState = currentState.adjustActive(command.delta());
        saveSignalConsoleStatePort.save(nextState);
        return SignalConsoleViewMapper.toView(nextState);
    }
}
