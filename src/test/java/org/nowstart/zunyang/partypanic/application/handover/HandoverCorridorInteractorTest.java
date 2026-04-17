package org.nowstart.zunyang.partypanic.application.handover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.dto.command.MoveHandoverActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.HandoverCorridorViewResult;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHandoverCorridorStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHandoverCorridorStatePort;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.handover.HandoverCorridorState;

class HandoverCorridorInteractorTest {

    @Test
    void start_initializes_corridor_state() {
        InMemoryHandoverCorridorPort statePort = new InMemoryHandoverCorridorPort();
        StartHandoverCorridorInteractor interactor = new StartHandoverCorridorInteractor(statePort);

        HandoverCorridorViewResult result = interactor.start();

        assertEquals("기록 복도", result.title());
        assertEquals(3, result.actorX());
        assertEquals(2, result.actorY());
        assertEquals(0, result.collectedRequiredCount());
        assertEquals(6, result.clues().size());
    }

    @Test
    void inspecting_required_clues_updates_count() {
        InMemoryHandoverCorridorPort statePort = new InMemoryHandoverCorridorPort();
        StartHandoverCorridorInteractor startInteractor = new StartHandoverCorridorInteractor(statePort);
        MoveHandoverActorInteractor moveInteractor = new MoveHandoverActorInteractor(statePort, statePort);
        InspectHandoverClueInteractor inspectInteractor = new InspectHandoverClueInteractor(statePort, statePort);

        startInteractor.start();
        inspectInteractor.inspect();
        moveInteractor.move(new MoveHandoverActorCommand(Direction.LEFT));
        moveInteractor.move(new MoveHandoverActorCommand(Direction.LEFT));
        moveInteractor.move(new MoveHandoverActorCommand(Direction.UP));
        HandoverCorridorViewResult result = inspectInteractor.inspect();

        assertEquals(2, result.collectedRequiredCount());
        assertFalse(result.readyToReturn());
        assertTrue(result.clues().stream().filter(HandoverClueView -> HandoverClueView.collected()).count() >= 2);
    }

    private static final class InMemoryHandoverCorridorPort
        implements LoadHandoverCorridorStatePort, SaveHandoverCorridorStatePort {

        private HandoverCorridorState handoverCorridorState;

        @Override
        public Optional<HandoverCorridorState> load() {
            return Optional.ofNullable(handoverCorridorState);
        }

        @Override
        public void save(HandoverCorridorState handoverCorridorState) {
            this.handoverCorridorState = handoverCorridorState;
        }
    }
}
