package org.nowstart.zunyang.partypanic.application.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.dto.command.MoveMessageActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.MessageWallViewResult;
import org.nowstart.zunyang.partypanic.application.port.out.LoadMessageWallStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveMessageWallStatePort;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.message.MessageWallState;
import org.nowstart.zunyang.partypanic.support.ActivityTestLayouts;

class MessageWallInteractorTest {

    @Test
    void start_initializes_message_wall_state() {
        InMemoryMessageWallPort statePort = new InMemoryMessageWallPort();
        StartMessageWallInteractor interactor = new StartMessageWallInteractor(
            ActivityTestLayouts::messageWall,
            statePort
        );

        MessageWallViewResult result = interactor.start();

        assertEquals("메시지 월", result.title());
        assertEquals(3, result.actorX());
        assertEquals(2, result.actorY());
        assertEquals(0, result.selectedRequiredCount());
        assertEquals(5, result.notes().size());
    }

    @Test
    void selecting_required_notes_updates_count() {
        InMemoryMessageWallPort statePort = new InMemoryMessageWallPort();
        StartMessageWallInteractor startInteractor = new StartMessageWallInteractor(
            ActivityTestLayouts::messageWall,
            statePort
        );
        MoveMessageActorInteractor moveInteractor = new MoveMessageActorInteractor(statePort, statePort);
        InspectMessageNoteInteractor inspectInteractor = new InspectMessageNoteInteractor(statePort, statePort);

        startInteractor.start();
        moveInteractor.move(new MoveMessageActorCommand(Direction.LEFT));
        moveInteractor.move(new MoveMessageActorCommand(Direction.LEFT));
        moveInteractor.move(new MoveMessageActorCommand(Direction.UP));
        inspectInteractor.inspect();
        moveInteractor.move(new MoveMessageActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveMessageActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveMessageActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveMessageActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveMessageActorCommand(Direction.UP));
        MessageWallViewResult result = inspectInteractor.inspect();

        assertEquals(2, result.selectedRequiredCount());
        assertTrue(result.readyToReturn());
        assertFalse(result.notes().stream().filter(MessageNoteView -> !MessageNoteView.required()).allMatch(MessageNoteView -> MessageNoteView.selected()));
    }

    private static final class InMemoryMessageWallPort
        implements LoadMessageWallStatePort, SaveMessageWallStatePort {

        private MessageWallState messageWallState;

        @Override
        public Optional<MessageWallState> load() {
            return Optional.ofNullable(messageWallState);
        }

        @Override
        public void save(MessageWallState messageWallState) {
            this.messageWallState = messageWallState;
        }
    }
}
