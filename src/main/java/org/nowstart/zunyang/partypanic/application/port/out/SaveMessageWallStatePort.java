package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.message.MessageWallState;

public interface SaveMessageWallStatePort {

    void save(MessageWallState messageWallState);
}
