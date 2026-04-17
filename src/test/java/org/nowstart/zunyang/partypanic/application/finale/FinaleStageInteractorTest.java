package org.nowstart.zunyang.partypanic.application.finale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.dto.command.MoveFinaleActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.FinaleStageViewResult;
import org.nowstart.zunyang.partypanic.application.port.out.LoadFinaleStageStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveFinaleStageStatePort;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleStageState;

class FinaleStageInteractorTest {

    @Test
    void start_initializes_finale_stage_state() {
        InMemoryFinaleStagePort statePort = new InMemoryFinaleStagePort();
        StartFinaleStageInteractor interactor = new StartFinaleStageInteractor(statePort);

        FinaleStageViewResult result = interactor.start();

        assertEquals("메인 스테이지", result.title());
        assertEquals(3, result.actorX());
        assertEquals(2, result.actorY());
        assertEquals(0, result.checkedRequiredCount());
        assertEquals(5, result.checkpoints().size());
    }

    @Test
    void checking_required_points_updates_count() {
        InMemoryFinaleStagePort statePort = new InMemoryFinaleStagePort();
        StartFinaleStageInteractor startInteractor = new StartFinaleStageInteractor(statePort);
        MoveFinaleActorInteractor moveInteractor = new MoveFinaleActorInteractor(statePort, statePort);
        InspectFinaleCheckpointInteractor inspectInteractor = new InspectFinaleCheckpointInteractor(statePort, statePort);

        startInteractor.start();
        inspectInteractor.inspect();
        moveInteractor.move(new MoveFinaleActorCommand(Direction.LEFT));
        moveInteractor.move(new MoveFinaleActorCommand(Direction.LEFT));
        moveInteractor.move(new MoveFinaleActorCommand(Direction.UP));
        inspectInteractor.inspect();
        moveInteractor.move(new MoveFinaleActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveFinaleActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveFinaleActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveFinaleActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveFinaleActorCommand(Direction.UP));
        FinaleStageViewResult result = inspectInteractor.inspect();

        assertEquals(3, result.checkedRequiredCount());
        assertTrue(result.readyToReturn());
        assertFalse(result.checkpoints().stream().filter(FinaleCheckpointView -> !FinaleCheckpointView.required()).allMatch(FinaleCheckpointView -> FinaleCheckpointView.checked()));
    }

    private static final class InMemoryFinaleStagePort
        implements LoadFinaleStageStatePort, SaveFinaleStageStatePort {

        private FinaleStageState finaleStageState;

        @Override
        public Optional<FinaleStageState> load() {
            return Optional.ofNullable(finaleStageState);
        }

        @Override
        public void save(FinaleStageState finaleStageState) {
            this.finaleStageState = finaleStageState;
        }
    }
}
