package org.nowstart.zunyang.partypanic.application.handover;

import org.nowstart.zunyang.partypanic.application.dto.result.HandoverCorridorViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.InspectHandoverClueUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHandoverCorridorStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHandoverCorridorStatePort;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverCorridorState;

public final class InspectHandoverClueInteractor implements InspectHandoverClueUseCase {

    private final LoadHandoverCorridorStatePort loadHandoverCorridorStatePort;
    private final SaveHandoverCorridorStatePort saveHandoverCorridorStatePort;

    public InspectHandoverClueInteractor(
        LoadHandoverCorridorStatePort loadHandoverCorridorStatePort,
        SaveHandoverCorridorStatePort saveHandoverCorridorStatePort
    ) {
        this.loadHandoverCorridorStatePort = loadHandoverCorridorStatePort;
        this.saveHandoverCorridorStatePort = saveHandoverCorridorStatePort;
    }

    @Override
    public HandoverCorridorViewResult inspect() {
        HandoverCorridorState currentState = loadHandoverCorridorStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Handover corridor has not been started"));
        HandoverCorridorState nextState = currentState.inspect();
        saveHandoverCorridorStatePort.save(nextState);
        return HandoverCorridorViewMapper.toView(nextState);
    }
}
