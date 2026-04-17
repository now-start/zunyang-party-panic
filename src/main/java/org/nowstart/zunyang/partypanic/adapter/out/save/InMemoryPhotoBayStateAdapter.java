package org.nowstart.zunyang.partypanic.adapter.out.save;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadPhotoBayStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SavePhotoBayStatePort;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoBayState;

public final class InMemoryPhotoBayStateAdapter
    implements LoadPhotoBayStatePort, SavePhotoBayStatePort {

    private PhotoBayState photoBayState;

    @Override
    public Optional<PhotoBayState> load() {
        return Optional.ofNullable(photoBayState);
    }

    @Override
    public void save(PhotoBayState photoBayState) {
        this.photoBayState = photoBayState;
    }
}
