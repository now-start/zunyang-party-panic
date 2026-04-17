package org.nowstart.zunyang.partypanic.adapter.out.save;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;

public final class InMemorySignalConsoleStateAdapter
    implements LoadSignalConsoleStatePort, SaveSignalConsoleStatePort {

    private SignalConsoleState signalConsoleState;

    @Override
    public Optional<SignalConsoleState> load() {
        return Optional.ofNullable(signalConsoleState);
    }

    @Override
    public void save(SignalConsoleState signalConsoleState) {
        this.signalConsoleState = signalConsoleState;
    }
}
