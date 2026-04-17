package org.nowstart.zunyang.partypanic.adapter.out.save;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadPropsArchiveStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SavePropsArchiveStatePort;
import org.nowstart.zunyang.partypanic.domain.props.PropsArchiveState;

public final class InMemoryPropsArchiveStateAdapter
    implements LoadPropsArchiveStatePort, SavePropsArchiveStatePort {

    private PropsArchiveState propsArchiveState;

    @Override
    public Optional<PropsArchiveState> load() {
        return Optional.ofNullable(propsArchiveState);
    }

    @Override
    public void save(PropsArchiveState propsArchiveState) {
        this.propsArchiveState = propsArchiveState;
    }
}
