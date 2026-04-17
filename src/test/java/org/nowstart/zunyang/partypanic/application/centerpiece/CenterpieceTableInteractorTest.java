package org.nowstart.zunyang.partypanic.application.centerpiece;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.dto.command.MoveCenterpieceActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.CenterpieceTableViewResult;
import org.nowstart.zunyang.partypanic.application.port.out.LoadCenterpieceTableStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveCenterpieceTableStatePort;
import org.nowstart.zunyang.partypanic.domain.centerpiece.CenterpieceTableState;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

class CenterpieceTableInteractorTest {

    @Test
    void start_initializes_table_state() {
        InMemoryCenterpieceTablePort statePort = new InMemoryCenterpieceTablePort();
        StartCenterpieceTableInteractor interactor = new StartCenterpieceTableInteractor(statePort);

        CenterpieceTableViewResult result = interactor.start();

        assertEquals("중앙 테이블", result.title());
        assertEquals(3, result.actorX());
        assertEquals(2, result.actorY());
        assertEquals(0, result.placedRequiredCount());
        assertEquals(5, result.placements().size());
    }

    @Test
    void inspecting_required_points_updates_layout_count() {
        InMemoryCenterpieceTablePort statePort = new InMemoryCenterpieceTablePort();
        StartCenterpieceTableInteractor startInteractor = new StartCenterpieceTableInteractor(statePort);
        MoveCenterpieceActorInteractor moveInteractor = new MoveCenterpieceActorInteractor(statePort, statePort);
        InspectCenterpiecePlacementInteractor inspectInteractor = new InspectCenterpiecePlacementInteractor(statePort, statePort);

        startInteractor.start();
        moveInteractor.move(new MoveCenterpieceActorCommand(Direction.UP));
        inspectInteractor.inspect();
        moveInteractor.move(new MoveCenterpieceActorCommand(Direction.DOWN));
        moveInteractor.move(new MoveCenterpieceActorCommand(Direction.LEFT));
        CenterpieceTableViewResult result = inspectInteractor.inspect();

        assertEquals(2, result.placedRequiredCount());
        assertFalse(result.readyToReturn());
        assertTrue(result.placements().stream().filter(placement -> placement.placed()).count() >= 2);
    }

    private static final class InMemoryCenterpieceTablePort
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
}
