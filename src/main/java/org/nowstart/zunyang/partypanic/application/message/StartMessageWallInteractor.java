package org.nowstart.zunyang.partypanic.application.message;

import org.nowstart.zunyang.partypanic.application.dto.result.MessageWallViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartMessageWallUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadGridActivityLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveMessageWallStatePort;
import org.nowstart.zunyang.partypanic.domain.message.MessageNoteId;
import org.nowstart.zunyang.partypanic.domain.message.MessageWallState;

public final class StartMessageWallInteractor implements StartMessageWallUseCase {

    private final LoadGridActivityLayoutPort<MessageNoteId> loadGridActivityLayoutPort;
    private final SaveMessageWallStatePort saveMessageWallStatePort;

    public StartMessageWallInteractor(
        LoadGridActivityLayoutPort<MessageNoteId> loadGridActivityLayoutPort,
        SaveMessageWallStatePort saveMessageWallStatePort
    ) {
        this.loadGridActivityLayoutPort = loadGridActivityLayoutPort;
        this.saveMessageWallStatePort = saveMessageWallStatePort;
    }

    @Override
    public MessageWallViewResult start() {
        MessageWallState state = MessageWallState.initial(loadGridActivityLayoutPort.load());
        saveMessageWallStatePort.save(state);
        return MessageWallViewMapper.toView(state);
    }
}
