package org.nowstart.zunyang.partypanic.adapter.out.save;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHubStatePort;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;

public final class InMemoryHubStateAdapter implements LoadHubStatePort, SaveHubStatePort {

    private HubState hubState;

    @Override
    public Optional<HubState> load() {
        return Optional.ofNullable(hubState);
    }

    @Override
    public void save(HubState hubState) {
        this.hubState = hubState;
    }
}
