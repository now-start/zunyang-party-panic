package org.nowstart.zunyang.partypanic.application.port.out;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleStageState;

public interface LoadFinaleStageStatePort {

    Optional<FinaleStageState> load();
}
