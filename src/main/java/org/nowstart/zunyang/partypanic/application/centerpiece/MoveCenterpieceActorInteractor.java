package org.nowstart.zunyang.partypanic.application.centerpiece;

import org.nowstart.zunyang.partypanic.application.dto.command.MoveCenterpieceActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.CenterpieceTableViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.MoveCenterpieceActorUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadCenterpieceTableStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveCenterpieceTableStatePort;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpieceTableState;

public final class MoveCenterpieceActorInteractor implements MoveCenterpieceActorUseCase {

    private final LoadCenterpieceTableStatePort loadCenterpieceTableStatePort;
    private final SaveCenterpieceTableStatePort saveCenterpieceTableStatePort;

    public MoveCenterpieceActorInteractor(
        LoadCenterpieceTableStatePort loadCenterpieceTableStatePort,
        SaveCenterpieceTableStatePort saveCenterpieceTableStatePort
    ) {
        this.loadCenterpieceTableStatePort = loadCenterpieceTableStatePort;
        this.saveCenterpieceTableStatePort = saveCenterpieceTableStatePort;
    }

    @Override
    public CenterpieceTableViewResult move(MoveCenterpieceActorCommand command) {
        CenterpieceTableState currentState = loadCenterpieceTableStatePort.load()
            .orElseThrow(() -> new IllegalStateException("Centerpiece table has not been started"));
        CenterpieceTableState nextState = currentState.move(command.direction());
        saveCenterpieceTableStatePort.save(nextState);
        return CenterpieceTableViewMapper.toView(nextState);
    }
}
