package org.nowstart.zunyang.partypanic.adapter.out.save;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHandoverCorridorStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHandoverCorridorStatePort;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverCorridorState;

public final class InMemoryHandoverCorridorStateAdapter
    implements LoadHandoverCorridorStatePort, SaveHandoverCorridorStatePort {

    private HandoverCorridorState handoverCorridorState;

    @Override
    public Optional<HandoverCorridorState> load() {
        return Optional.ofNullable(handoverCorridorState);
    }

    @Override
    public void save(HandoverCorridorState handoverCorridorState) {
        this.handoverCorridorState = handoverCorridorState;
    }
}
