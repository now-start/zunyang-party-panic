package org.nowstart.zunyang.partypanic.application.handover;

import org.nowstart.zunyang.partypanic.application.dto.result.HandoverCorridorViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartHandoverCorridorUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadGridActivityLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHandoverCorridorStatePort;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverClueId;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverCorridorState;

public final class StartHandoverCorridorInteractor implements StartHandoverCorridorUseCase {

    private final LoadGridActivityLayoutPort<HandoverClueId> loadGridActivityLayoutPort;
    private final SaveHandoverCorridorStatePort saveHandoverCorridorStatePort;

    public StartHandoverCorridorInteractor(
        LoadGridActivityLayoutPort<HandoverClueId> loadGridActivityLayoutPort,
        SaveHandoverCorridorStatePort saveHandoverCorridorStatePort
    ) {
        this.loadGridActivityLayoutPort = loadGridActivityLayoutPort;
        this.saveHandoverCorridorStatePort = saveHandoverCorridorStatePort;
    }

    @Override
    public HandoverCorridorViewResult start() {
        HandoverCorridorState state = HandoverCorridorState.initial(loadGridActivityLayoutPort.load());
        saveHandoverCorridorStatePort.save(state);
        return HandoverCorridorViewMapper.toView(state);
    }
}
