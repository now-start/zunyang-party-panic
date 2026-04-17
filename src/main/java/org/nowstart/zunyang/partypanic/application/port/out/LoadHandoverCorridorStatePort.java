package org.nowstart.zunyang.partypanic.application.port.out;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverCorridorState;

public interface LoadHandoverCorridorStatePort {

    Optional<HandoverCorridorState> load();
}
