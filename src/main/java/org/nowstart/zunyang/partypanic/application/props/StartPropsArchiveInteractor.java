package org.nowstart.zunyang.partypanic.application.props;

import org.nowstart.zunyang.partypanic.application.dto.result.PropsArchiveViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartPropsArchiveUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.SavePropsArchiveStatePort;
import org.nowstart.zunyang.partypanic.domain.props.PropsArchiveState;

public final class StartPropsArchiveInteractor implements StartPropsArchiveUseCase {

    private final SavePropsArchiveStatePort savePropsArchiveStatePort;

    public StartPropsArchiveInteractor(SavePropsArchiveStatePort savePropsArchiveStatePort) {
        this.savePropsArchiveStatePort = savePropsArchiveStatePort;
    }

    @Override
    public PropsArchiveViewResult start() {
        PropsArchiveState state = PropsArchiveState.initial();
        savePropsArchiveStatePort.save(state);
        return PropsArchiveViewMapper.toView(state);
    }
}
