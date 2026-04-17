package org.nowstart.zunyang.partypanic.application.props;

import org.nowstart.zunyang.partypanic.application.dto.result.PropsArchiveViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.InspectPropsItemUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadPropsArchiveStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SavePropsArchiveStatePort;
import org.nowstart.zunyang.partypanic.domain.props.PropsArchiveState;

public final class InspectPropsItemInteractor implements InspectPropsItemUseCase {

    private final LoadPropsArchiveStatePort loadPropsArchiveStatePort;
    private final SavePropsArchiveStatePort savePropsArchiveStatePort;

    public InspectPropsItemInteractor(
        LoadPropsArchiveStatePort loadPropsArchiveStatePort,
        SavePropsArchiveStatePort savePropsArchiveStatePort
    ) {
        this.loadPropsArchiveStatePort = loadPropsArchiveStatePort;
        this.savePropsArchiveStatePort = savePropsArchiveStatePort;
    }

    @Override
    public PropsArchiveViewResult inspect() {
        PropsArchiveState currentState = loadPropsArchiveStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Props archive has not been started"));
        PropsArchiveState nextState = currentState.inspect();
        savePropsArchiveStatePort.save(nextState);
        return PropsArchiveViewMapper.toView(nextState);
    }
}
