package org.nowstart.zunyang.partypanic.application.port.in;

import org.nowstart.zunyang.partypanic.application.dto.command.AdjustSignalSettingCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.SignalConsoleViewResult;

public interface AdjustSignalSettingUseCase {

    SignalConsoleViewResult adjust(AdjustSignalSettingCommand command);
}
