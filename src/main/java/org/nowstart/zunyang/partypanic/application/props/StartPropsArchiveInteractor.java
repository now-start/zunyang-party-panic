package org.nowstart.zunyang.partypanic.application.props;

import org.nowstart.zunyang.partypanic.application.dto.result.PropsArchiveViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartPropsArchiveUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadGridActivityLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.SavePropsArchiveStatePort;
import org.nowstart.zunyang.partypanic.domain.props.PropsArchiveState;
import org.nowstart.zunyang.partypanic.domain.props.PropsItemId;

public final class StartPropsArchiveInteractor implements StartPropsArchiveUseCase {

    private final LoadGridActivityLayoutPort<PropsItemId> loadGridActivityLayoutPort;
    private final SavePropsArchiveStatePort savePropsArchiveStatePort;

    public StartPropsArchiveInteractor(
        LoadGridActivityLayoutPort<PropsItemId> loadGridActivityLayoutPort,
        SavePropsArchiveStatePort savePropsArchiveStatePort
    ) {
        this.loadGridActivityLayoutPort = loadGridActivityLayoutPort;
        this.savePropsArchiveStatePort = savePropsArchiveStatePort;
    }

    @Override
    public PropsArchiveViewResult start() {
        PropsArchiveState state = PropsArchiveState.initial(loadGridActivityLayoutPort.load());
        savePropsArchiveStatePort.save(state);
        return PropsArchiveViewMapper.toView(state);
    }
}
