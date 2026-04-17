package org.nowstart.zunyang.partypanic.adapter.out.save;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadCenterpieceTableStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveCenterpieceTableStatePort;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpieceTableState;

public final class InMemoryCenterpieceTableStateAdapter
    implements LoadCenterpieceTableStatePort, SaveCenterpieceTableStatePort {

    private CenterpieceTableState centerpieceTableState;

    @Override
    public Optional<CenterpieceTableState> load() {
        return Optional.ofNullable(centerpieceTableState);
    }

    @Override
    public void save(CenterpieceTableState centerpieceTableState) {
        this.centerpieceTableState = centerpieceTableState;
    }
}
