package org.nowstart.zunyang.partypanic.application.session;

import org.nowstart.zunyang.partypanic.application.port.in.RestartSessionUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.ResetChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHubStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public final class RestartSessionInteractor implements RestartSessionUseCase {

    private final LoadHubLayoutPort loadHubLayoutPort;
    private final SaveHubStatePort saveHubStatePort;
    private final ResetChapterStatePort resetChapterStatePort;
    private final SaveSessionSnapshotPort saveSessionSnapshotPort;

    public RestartSessionInteractor(
        LoadHubLayoutPort loadHubLayoutPort,
        SaveHubStatePort saveHubStatePort,
        ResetChapterStatePort resetChapterStatePort,
        SaveSessionSnapshotPort saveSessionSnapshotPort
    ) {
        this.loadHubLayoutPort = loadHubLayoutPort;
        this.saveHubStatePort = saveHubStatePort;
        this.resetChapterStatePort = resetChapterStatePort;
        this.saveSessionSnapshotPort = saveSessionSnapshotPort;
    }

    @Override
    public void restart() {
        saveSessionSnapshotPort.save(RunProgress.initial());
        saveHubStatePort.save(HubState.initial(loadHubLayoutPort.load()));
        resetChapterStatePort.reset();
    }
}
