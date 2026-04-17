package org.nowstart.zunyang.partypanic.application.photo;

import org.nowstart.zunyang.partypanic.application.dto.result.PhotoBayViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartPhotoBayUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadGridActivityLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.SavePhotoBayStatePort;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoBayState;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoFocusId;

public final class StartPhotoBayInteractor implements StartPhotoBayUseCase {

    private final LoadGridActivityLayoutPort<PhotoFocusId> loadGridActivityLayoutPort;
    private final SavePhotoBayStatePort savePhotoBayStatePort;

    public StartPhotoBayInteractor(
        LoadGridActivityLayoutPort<PhotoFocusId> loadGridActivityLayoutPort,
        SavePhotoBayStatePort savePhotoBayStatePort
    ) {
        this.loadGridActivityLayoutPort = loadGridActivityLayoutPort;
        this.savePhotoBayStatePort = savePhotoBayStatePort;
    }

    @Override
    public PhotoBayViewResult start() {
        PhotoBayState state = PhotoBayState.initial(loadGridActivityLayoutPort.load());
        savePhotoBayStatePort.save(state);
        return PhotoBayViewMapper.toView(state);
    }
}
