package org.nowstart.zunyang.partypanic.application.port.out;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoBayState;

public interface LoadPhotoBayStatePort {

    Optional<PhotoBayState> load();
}
