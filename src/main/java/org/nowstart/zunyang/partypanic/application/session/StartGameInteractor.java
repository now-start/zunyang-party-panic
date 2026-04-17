package org.nowstart.zunyang.partypanic.application.session;

import org.nowstart.zunyang.partypanic.application.port.in.StartGameUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public final class StartGameInteractor implements StartGameUseCase {

    private final LoadSessionSnapshotPort loadSessionSnapshotPort;
    private final SaveSessionSnapshotPort saveSessionSnapshotPort;

    public StartGameInteractor(
        LoadSessionSnapshotPort loadSessionSnapshotPort,
        SaveSessionSnapshotPort saveSessionSnapshotPort
    ) {
        this.loadSessionSnapshotPort = loadSessionSnapshotPort;
        this.saveSessionSnapshotPort = saveSessionSnapshotPort;
    }

    @Override
    public RunProgress start() {
        return loadSessionSnapshotPort.load()
            .orElseGet(() -> {
                RunProgress initial = RunProgress.initial();
                saveSessionSnapshotPort.save(initial);
                return initial;
            });
    }
}
