package org.nowstart.zunyang.partypanic.application.finale;

import org.nowstart.zunyang.partypanic.application.dto.result.FinaleStageViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartFinaleStageUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.SaveFinaleStageStatePort;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleStageState;

public final class StartFinaleStageInteractor implements StartFinaleStageUseCase {

    private final SaveFinaleStageStatePort saveFinaleStageStatePort;

    public StartFinaleStageInteractor(
        SaveFinaleStageStatePort saveFinaleStageStatePort
    ) {
        this.saveFinaleStageStatePort = saveFinaleStageStatePort;
    }

    @Override
    public FinaleStageViewResult start() {
        FinaleStageState state = FinaleStageState.initial();
        saveFinaleStageStatePort.save(state);
        return FinaleStageViewMapper.toView(state);
    }
}
