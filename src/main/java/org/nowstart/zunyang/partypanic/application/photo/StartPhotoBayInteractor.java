package org.nowstart.zunyang.partypanic.application.photo;

import org.nowstart.zunyang.partypanic.application.dto.result.PhotoBayViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartPhotoBayUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.SavePhotoBayStatePort;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoBayState;

public final class StartPhotoBayInteractor implements StartPhotoBayUseCase {

    private final SavePhotoBayStatePort savePhotoBayStatePort;

    public StartPhotoBayInteractor(SavePhotoBayStatePort savePhotoBayStatePort) {
        this.savePhotoBayStatePort = savePhotoBayStatePort;
    }

    @Override
    public PhotoBayViewResult start() {
        PhotoBayState state = PhotoBayState.initial();
        savePhotoBayStatePort.save(state);
        return PhotoBayViewMapper.toView(state);
    }
}
