package org.nowstart.zunyang.partypanic.application.port.out;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;

public interface LoadHubStatePort {

    Optional<HubState> load();
}
