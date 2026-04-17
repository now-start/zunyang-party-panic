package org.nowstart.zunyang.partypanic.application.finale;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveFinaleActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.FinaleStageViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.MoveFinaleActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadFinaleStageStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveFinaleStageStatePort;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleStageState;

public final class MoveFinaleActorInteractor implements MoveFinaleActorUseCase {

    private final LoadFinaleStageStatePort loadFinaleStageStatePort;
    private final SaveFinaleStageStatePort saveFinaleStageStatePort;

    public MoveFinaleActorInteractor(
        LoadFinaleStageStatePort loadFinaleStageStatePort,
        SaveFinaleStageStatePort saveFinaleStageStatePort
    ) {
        this.loadFinaleStageStatePort = loadFinaleStageStatePort;
        this.saveFinaleStageStatePort = saveFinaleStageStatePort;
    }

    @Override
    public FinaleStageViewResult move(MoveFinaleActorCommand command) {
        FinaleStageState currentState = loadFinaleStageStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Finale stage has not been started"));
        FinaleStageState nextState = currentState.move(command.direction());
        saveFinaleStageStatePort.save(nextState);
        return FinaleStageViewMapper.toView(nextState);
    }
}
