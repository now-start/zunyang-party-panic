package org.nowstart.zunyang.partypanic.application.finale;

import org.nowstart.zunyang.partypanic.application.dto.result.FinaleStageViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.InspectFinaleCheckpointUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadFinaleStageStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveFinaleStageStatePort;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleStageState;

public final class InspectFinaleCheckpointInteractor implements InspectFinaleCheckpointUseCase {

    private final LoadFinaleStageStatePort loadFinaleStageStatePort;
    private final SaveFinaleStageStatePort saveFinaleStageStatePort;

    public InspectFinaleCheckpointInteractor(
        LoadFinaleStageStatePort loadFinaleStageStatePort,
        SaveFinaleStageStatePort saveFinaleStageStatePort
    ) {
        this.loadFinaleStageStatePort = loadFinaleStageStatePort;
        this.saveFinaleStageStatePort = saveFinaleStageStatePort;
    }

    @Override
    public FinaleStageViewResult inspect() {
        FinaleStageState currentState = loadFinaleStageStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Finale stage has not been started"));
        FinaleStageState nextState = currentState.inspect();
        saveFinaleStageStatePort.save(nextState);
        return FinaleStageViewMapper.toView(nextState);
    }
}
