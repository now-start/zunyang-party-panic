package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.photo.PhotoBayState;

public interface SavePhotoBayStatePort {

    void save(PhotoBayState photoBayState);
}
