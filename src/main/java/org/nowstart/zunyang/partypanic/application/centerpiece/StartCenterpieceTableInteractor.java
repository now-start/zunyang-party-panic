package org.nowstart.zunyang.partypanic.application.centerpiece;

import org.nowstart.zunyang.partypanic.application.dto.result.CenterpieceTableViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartCenterpieceTableUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadGridActivityLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveCenterpieceTableStatePort;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpiecePlacementId;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpieceTableState;

public final class StartCenterpieceTableInteractor implements StartCenterpieceTableUseCase {

    private final LoadGridActivityLayoutPort<CenterpiecePlacementId> loadGridActivityLayoutPort;
    private final SaveCenterpieceTableStatePort saveCenterpieceTableStatePort;

    public StartCenterpieceTableInteractor(
        LoadGridActivityLayoutPort<CenterpiecePlacementId> loadGridActivityLayoutPort,
        SaveCenterpieceTableStatePort saveCenterpieceTableStatePort
    ) {
        this.loadGridActivityLayoutPort = loadGridActivityLayoutPort;
        this.saveCenterpieceTableStatePort = saveCenterpieceTableStatePort;
    }

    @Override
    public CenterpieceTableViewResult start() {
        CenterpieceTableState state = CenterpieceTableState.initial(loadGridActivityLayoutPort.load());
        saveCenterpieceTableStatePort.save(state);
        return CenterpieceTableViewMapper.toView(state);
    }
}
