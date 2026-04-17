package org.nowstart.zunyang.partypanic.application.centerpiece;

import org.nowstart.zunyang.partypanic.application.dto.result.CenterpieceTableViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartCenterpieceTableUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.SaveCenterpieceTableStatePort;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpieceTableState;

public final class StartCenterpieceTableInteractor implements StartCenterpieceTableUseCase {

    private final SaveCenterpieceTableStatePort saveCenterpieceTableStatePort;

    public StartCenterpieceTableInteractor(
        SaveCenterpieceTableStatePort saveCenterpieceTableStatePort
    ) {
        this.saveCenterpieceTableStatePort = saveCenterpieceTableStatePort;
    }

    @Override
    public CenterpieceTableViewResult start() {
        CenterpieceTableState state = CenterpieceTableState.initial();
        saveCenterpieceTableStatePort.save(state);
        return CenterpieceTableViewMapper.toView(state);
    }
}
