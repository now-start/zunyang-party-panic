package org.nowstart.zunyang.partypanic.adapter.out.save;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public final class InMemoryRunProgressAdapter
    implements LoadSessionSnapshotPort, SaveSessionSnapshotPort {

    private RunProgress runProgress = RunProgress.initial();

    @Override
    public Optional<RunProgress> load() {
        return Optional.ofNullable(runProgress);
    }

    @Override
    public void save(RunProgress runProgress) {
        this.runProgress = runProgress;
    }
}
