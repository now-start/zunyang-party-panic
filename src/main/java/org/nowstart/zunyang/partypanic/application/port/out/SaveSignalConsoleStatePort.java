package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;

public interface SaveSignalConsoleStatePort {

    void save(SignalConsoleState signalConsoleState);
}
