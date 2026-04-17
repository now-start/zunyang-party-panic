package org.nowstart.zunyang.partypanic.application.props;

import org.nowstart.zunyang.partypanic.application.dto.command.MovePropsActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.PropsArchiveViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.MovePropsActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadPropsArchiveStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SavePropsArchiveStatePort;
import org.nowstart.zunyang.partypanic.domain.props.PropsArchiveState;

public final class MovePropsActorInteractor implements MovePropsActorUseCase {

    private final LoadPropsArchiveStatePort loadPropsArchiveStatePort;
    private final SavePropsArchiveStatePort savePropsArchiveStatePort;

    public MovePropsActorInteractor(
        LoadPropsArchiveStatePort loadPropsArchiveStatePort,
        SavePropsArchiveStatePort savePropsArchiveStatePort
    ) {
        this.loadPropsArchiveStatePort = loadPropsArchiveStatePort;
        this.savePropsArchiveStatePort = savePropsArchiveStatePort;
    }

    @Override
    public PropsArchiveViewResult move(MovePropsActorCommand command) {
        PropsArchiveState currentState = loadPropsArchiveStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Props archive has not been started"));
        PropsArchiveState nextState = currentState.move(command.direction());
        savePropsArchiveStatePort.save(nextState);
        return PropsArchiveViewMapper.toView(nextState);
    }
}
