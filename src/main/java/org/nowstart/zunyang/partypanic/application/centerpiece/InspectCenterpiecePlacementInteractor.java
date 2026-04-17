package org.nowstart.zunyang.partypanic.application.centerpiece;

import org.nowstart.zunyang.partypanic.application.dto.result.CenterpieceTableViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.InspectCenterpiecePlacementUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadCenterpieceTableStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveCenterpieceTableStatePort;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpieceTableState;

public final class InspectCenterpiecePlacementInteractor
    implements InspectCenterpiecePlacementUseCase {

    private final LoadCenterpieceTableStatePort loadCenterpieceTableStatePort;
    private final SaveCenterpieceTableStatePort saveCenterpieceTableStatePort;

    public InspectCenterpiecePlacementInteractor(
        LoadCenterpieceTableStatePort loadCenterpieceTableStatePort,
        SaveCenterpieceTableStatePort saveCenterpieceTableStatePort
    ) {
        this.loadCenterpieceTableStatePort = loadCenterpieceTableStatePort;
        this.saveCenterpieceTableStatePort = saveCenterpieceTableStatePort;
    }

    @Override
    public CenterpieceTableViewResult inspect() {
        CenterpieceTableState currentState = loadCenterpieceTableStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Centerpiece table has not been started"));
        CenterpieceTableState nextState = currentState.inspect();
        saveCenterpieceTableStatePort.save(nextState);
        return CenterpieceTableViewMapper.toView(nextState);
    }
}
