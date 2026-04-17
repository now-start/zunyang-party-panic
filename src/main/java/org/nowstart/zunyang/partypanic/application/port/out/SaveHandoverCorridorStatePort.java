package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.handover.HandoverCorridorState;

public interface SaveHandoverCorridorStatePort {

    void save(HandoverCorridorState handoverCorridorState);
}
