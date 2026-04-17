package org.nowstart.zunyang.partypanic.application.hub;

import org.nowstart.zunyang.partypanic.application.dto.result.HubViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.LoadHubUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHubStatePort;
import org.nowstart.zunyang.partypanic.domain.hub.HubLayout;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public final class LoadHubInteractor implements LoadHubUseCase {

    private final LoadHubLayoutPort loadHubLayoutPort;
    private final LoadHubStatePort loadHubStatePort;
    private final SaveHubStatePort saveHubStatePort;
    private final LoadSessionSnapshotPort loadSessionSnapshotPort;

    public LoadHubInteractor(
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
    public HubViewResult load() {
        HubLayout layout = loadHubLayoutPort.load();
        HubState state = loadHubStatePort.load()
            .orElseGet(() -> {
                HubState initialState = HubState.initial(layout);
                saveHubStatePort.save(initialState);
                return initialState;
            });
        return HubViewMapper.toView(state, loadProgress());
    }

    private RunProgress loadProgress() {
        return loadSessionSnapshotPort.load().orElseGet(RunProgress::initial);
    }
}
