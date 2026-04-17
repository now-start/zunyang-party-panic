package org.nowstart.zunyang.partypanic.application.port.out;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.domain.props.PropsArchiveState;

public interface LoadPropsArchiveStatePort {

    Optional<PropsArchiveState> load();
}
