package org.nowstart.zunyang.partypanic.application.props;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.dto.command.MovePropsActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.PropsArchiveViewResult;
import org.nowstart.zunyang.partypanic.application.port.out.LoadPropsArchiveStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SavePropsArchiveStatePort;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.props.PropsArchiveState;

class PropsArchiveInteractorTest {

    @Test
    void start_initializes_archive_state() {
        InMemoryPropsArchivePort statePort = new InMemoryPropsArchivePort();
        StartPropsArchiveInteractor interactor = new StartPropsArchiveInteractor(statePort);

        PropsArchiveViewResult result = interactor.start();

        assertEquals("소품 아카이브", result.title());
        assertEquals(3, result.actorX());
        assertEquals(2, result.actorY());
        assertEquals(0, result.collectedRequiredCount());
        assertEquals(6, result.items().size());
    }

    @Test
    void inspecting_required_items_updates_inventory_count() {
        InMemoryPropsArchivePort statePort = new InMemoryPropsArchivePort();
        StartPropsArchiveInteractor startInteractor = new StartPropsArchiveInteractor(statePort);
        MovePropsActorInteractor moveInteractor = new MovePropsActorInteractor(statePort, statePort);
        InspectPropsItemInteractor inspectInteractor = new InspectPropsItemInteractor(statePort, statePort);

        startInteractor.start();
        inspectInteractor.inspect();
        moveInteractor.move(new MovePropsActorCommand(Direction.LEFT));
        moveInteractor.move(new MovePropsActorCommand(Direction.LEFT));
        moveInteractor.move(new MovePropsActorCommand(Direction.UP));
        PropsArchiveViewResult result = inspectInteractor.inspect();

        assertEquals(2, result.collectedRequiredCount());
        assertFalse(result.readyToReturn());
        assertTrue(result.items().stream().filter(item -> item.collected()).count() >= 2);
    }

    private static final class InMemoryPropsArchivePort
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
}
