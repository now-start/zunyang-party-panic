package org.nowstart.zunyang.partypanic.application.port.out;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.domain.message.MessageWallState;

public interface LoadMessageWallStatePort {

    Optional<MessageWallState> load();
}
