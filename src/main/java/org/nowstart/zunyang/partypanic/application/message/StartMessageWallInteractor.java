package org.nowstart.zunyang.partypanic.application.message;

import org.nowstart.zunyang.partypanic.application.dto.result.MessageWallViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartMessageWallUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.SaveMessageWallStatePort;
import org.nowstart.zunyang.partypanic.domain.message.MessageWallState;

public final class StartMessageWallInteractor implements StartMessageWallUseCase {

    private final SaveMessageWallStatePort saveMessageWallStatePort;

    public StartMessageWallInteractor(
        SaveMessageWallStatePort saveMessageWallStatePort
    ) {
        this.saveMessageWallStatePort = saveMessageWallStatePort;
    }

    @Override
    public MessageWallViewResult start() {
        MessageWallState state = MessageWallState.initial();
        saveMessageWallStatePort.save(state);
        return MessageWallViewMapper.toView(state);
    }
}
