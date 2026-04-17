package org.nowstart.zunyang.partypanic.application.finale;

import org.nowstart.zunyang.partypanic.application.dto.result.FinaleStageViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartFinaleStageUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadGridActivityLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveFinaleStageStatePort;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleCheckpointId;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleStageState;

public final class StartFinaleStageInteractor implements StartFinaleStageUseCase {

    private final LoadGridActivityLayoutPort<FinaleCheckpointId> loadGridActivityLayoutPort;
    private final SaveFinaleStageStatePort saveFinaleStageStatePort;

    public StartFinaleStageInteractor(
        LoadGridActivityLayoutPort<FinaleCheckpointId> loadGridActivityLayoutPort,
        SaveFinaleStageStatePort saveFinaleStageStatePort
    ) {
        this.loadGridActivityLayoutPort = loadGridActivityLayoutPort;
        this.saveFinaleStageStatePort = saveFinaleStageStatePort;
    }

    @Override
    public FinaleStageViewResult start() {
        FinaleStageState state = FinaleStageState.initial(loadGridActivityLayoutPort.load());
        saveFinaleStageStatePort.save(state);
        return FinaleStageViewMapper.toView(state);
    }
}
