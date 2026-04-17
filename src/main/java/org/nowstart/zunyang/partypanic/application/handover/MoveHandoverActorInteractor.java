package org.nowstart.zunyang.partypanic.application.handover;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveHandoverActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.HandoverCorridorViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.MoveHandoverActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHandoverCorridorStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHandoverCorridorStatePort;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverCorridorState;

public final class MoveHandoverActorInteractor implements MoveHandoverActorUseCase {

    private final LoadHandoverCorridorStatePort loadHandoverCorridorStatePort;
    private final SaveHandoverCorridorStatePort saveHandoverCorridorStatePort;

    public MoveHandoverActorInteractor(
        LoadHandoverCorridorStatePort loadHandoverCorridorStatePort,
        SaveHandoverCorridorStatePort saveHandoverCorridorStatePort
    ) {
        this.loadHandoverCorridorStatePort = loadHandoverCorridorStatePort;
        this.saveHandoverCorridorStatePort = saveHandoverCorridorStatePort;
    }

    @Override
    public HandoverCorridorViewResult move(MoveHandoverActorCommand command) {
        HandoverCorridorState currentState = loadHandoverCorridorStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Handover corridor has not been started"));
        HandoverCorridorState nextState = currentState.move(command.direction());
        saveHandoverCorridorStatePort.save(nextState);
        return HandoverCorridorViewMapper.toView(nextState);
    }
}
