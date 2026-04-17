package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.hub.HubState;

public interface SaveHubStatePort {

    void save(HubState hubState);
}
