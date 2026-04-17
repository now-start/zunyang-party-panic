package org.nowstart.zunyang.partypanic.application.message;

import org.nowstart.zunyang.partypanic.application.dto.result.MessageWallViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.InspectMessageNoteUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadMessageWallStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveMessageWallStatePort;
import org.nowstart.zunyang.partypanic.domain.message.MessageWallState;

public final class InspectMessageNoteInteractor implements InspectMessageNoteUseCase {

    private final LoadMessageWallStatePort loadMessageWallStatePort;
    private final SaveMessageWallStatePort saveMessageWallStatePort;

    public InspectMessageNoteInteractor(
        LoadMessageWallStatePort loadMessageWallStatePort,
        SaveMessageWallStatePort saveMessageWallStatePort
    ) {
        this.loadMessageWallStatePort = loadMessageWallStatePort;
        this.saveMessageWallStatePort = saveMessageWallStatePort;
    }

    @Override
    public MessageWallViewResult inspect() {
        MessageWallState currentState = loadMessageWallStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Message wall has not been started"));
        MessageWallState nextState = currentState.inspect();
        saveMessageWallStatePort.save(nextState);
        return MessageWallViewMapper.toView(nextState);
    }
}
