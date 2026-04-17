package org.nowstart.zunyang.partypanic.adapter.out.save;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadMessageWallStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveMessageWallStatePort;
import org.nowstart.zunyang.partypanic.domain.message.MessageWallState;

public final class InMemoryMessageWallStateAdapter
    implements LoadMessageWallStatePort, SaveMessageWallStatePort {

    private MessageWallState messageWallState;

    @Override
    public Optional<MessageWallState> load() {
        return Optional.ofNullable(messageWallState);
    }

    @Override
    public void save(MessageWallState messageWallState) {
        this.messageWallState = messageWallState;
    }
}
