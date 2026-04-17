package org.nowstart.zunyang.partypanic.application.port.in;

import org.nowstart.zunyang.partypanic.application.dto.command.MovePhotoActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.PhotoBayViewResult;

public interface MovePhotoActorUseCase {

    PhotoBayViewResult move(MovePhotoActorCommand command);
}
