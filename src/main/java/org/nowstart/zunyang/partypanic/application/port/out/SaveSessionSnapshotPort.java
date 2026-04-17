package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public interface SaveSessionSnapshotPort {

    void save(RunProgress runProgress);
}
