package org.nowstart.zunyang.partypanic.application.port.out;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public interface LoadSessionSnapshotPort {

    Optional<RunProgress> load();
}
