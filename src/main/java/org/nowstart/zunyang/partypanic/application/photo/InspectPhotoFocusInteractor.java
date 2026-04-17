package org.nowstart.zunyang.partypanic.application.photo;

import org.nowstart.zunyang.partypanic.application.dto.result.PhotoBayViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.InspectPhotoFocusUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadPhotoBayStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SavePhotoBayStatePort;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoBayState;

public final class InspectPhotoFocusInteractor implements InspectPhotoFocusUseCase {

    private final LoadPhotoBayStatePort loadPhotoBayStatePort;
    private final SavePhotoBayStatePort savePhotoBayStatePort;

    public InspectPhotoFocusInteractor(
        LoadPhotoBayStatePort loadPhotoBayStatePort,
        SavePhotoBayStatePort savePhotoBayStatePort
    ) {
        this.loadPhotoBayStatePort = loadPhotoBayStatePort;
        this.savePhotoBayStatePort = savePhotoBayStatePort;
    }

    @Override
    public PhotoBayViewResult inspect() {
        PhotoBayState currentState = loadPhotoBayStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Photo bay has not been started"));
        PhotoBayState nextState = currentState.inspect();
        savePhotoBayStatePort.save(nextState);
        return PhotoBayViewMapper.toView(nextState);
    }
}
