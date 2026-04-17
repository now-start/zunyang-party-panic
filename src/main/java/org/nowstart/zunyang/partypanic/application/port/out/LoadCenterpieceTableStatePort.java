package org.nowstart.zunyang.partypanic.application.port.out;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpieceTableState;

public interface LoadCenterpieceTableStatePort {

    Optional<CenterpieceTableState> load();
}
