package org.nowstart.zunyang.partypanic.application.handover;

import org.nowstart.zunyang.partypanic.application.dto.result.HandoverCorridorViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartHandoverCorridorUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHandoverCorridorStatePort;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverCorridorState;

public final class StartHandoverCorridorInteractor implements StartHandoverCorridorUseCase {

    private final SaveHandoverCorridorStatePort saveHandoverCorridorStatePort;

    public StartHandoverCorridorInteractor(
        SaveHandoverCorridorStatePort saveHandoverCorridorStatePort
    ) {
        this.saveHandoverCorridorStatePort = saveHandoverCorridorStatePort;
    }

    @Override
    public HandoverCorridorViewResult start() {
        HandoverCorridorState state = HandoverCorridorState.initial();
        saveHandoverCorridorStatePort.save(state);
        return HandoverCorridorViewMapper.toView(state);
    }
}
