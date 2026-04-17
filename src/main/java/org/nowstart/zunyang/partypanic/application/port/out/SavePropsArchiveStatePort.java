package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.props.PropsArchiveState;

public interface SavePropsArchiveStatePort {

    void save(PropsArchiveState propsArchiveState);
}
