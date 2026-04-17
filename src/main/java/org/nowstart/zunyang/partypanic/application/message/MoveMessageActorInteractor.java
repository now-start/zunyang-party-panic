package org.nowstart.zunyang.partypanic.application.message;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveMessageActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.MessageWallViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.MoveMessageActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadMessageWallStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveMessageWallStatePort;
import org.nowstart.zunyang.partypanic.domain.message.MessageWallState;

public final class MoveMessageActorInteractor implements MoveMessageActorUseCase {

    private final LoadMessageWallStatePort loadMessageWallStatePort;
    private final SaveMessageWallStatePort saveMessageWallStatePort;

    public MoveMessageActorInteractor(
        LoadMessageWallStatePort loadMessageWallStatePort,
        SaveMessageWallStatePort saveMessageWallStatePort
    ) {
        this.loadMessageWallStatePort = loadMessageWallStatePort;
        this.saveMessageWallStatePort = saveMessageWallStatePort;
    }

    @Override
    public MessageWallViewResult move(MoveMessageActorCommand command) {
        MessageWallState currentState = loadMessageWallStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Message wall has not been started"));
        MessageWallState nextState = currentState.move(command.direction());
        saveMessageWallStatePort.save(nextState);
        return MessageWallViewMapper.toView(nextState);
    }
}
