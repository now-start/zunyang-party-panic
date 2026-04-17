package org.nowstart.zunyang.partypanic.application.port.out;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;

public interface LoadSignalConsoleStatePort {

    Optional<SignalConsoleState> load();
}
