package org.nowstart.zunyang.partypanic.application.hub;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveHubActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.HubViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.MoveHubActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHubStatePort;
import org.nowstart.zunyang.partypanic.domain.hub.HubLayout;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public final class MoveHubActorInteractor implements MoveHubActorUseCase {

    private final LoadHubLayoutPort loadHubLayoutPort;
    private final LoadHubStatePort loadHubStatePort;
    private final SaveHubStatePort saveHubStatePort;
    private final LoadSessionSnapshotPort loadSessionSnapshotPort;

    public MoveHubActorInteractor(
        LoadHubLayoutPort loadHubLayoutPort,
        LoadHubStatePort loadHubStatePort,
        SaveHubStatePort saveHubStatePort,
        LoadSessionSnapshotPort loadSessionSnapshotPort
    ) {
        this.loadHubLayoutPort = loadHubLayoutPort;
        this.loadHubStatePort = loadHubStatePort;
        this.saveHubStatePort = saveHubStatePort;
        this.loadSessionSnapshotPort = loadSessionSnapshotPort;
    }

    @Override
    public HubViewResult move(MoveHubActorCommand command) {
        HubState state = loadOrCreateState();
        HubState movedState = state.move(command.direction());
        saveHubStatePort.save(movedState);
        return HubViewMapper.toView(movedState, loadProgress());
    }

    private HubState loadOrCreateState() {
        HubLayout layout = loadHubLayoutPort.load();
        return loadHubStatePort.load()
            .orElseGet(() -> HubState.initial(layout));
    }

    private RunProgress loadProgress() {
        return loadSessionSnapshotPort.load().orElseGet(RunProgress::initial);
    }
}
