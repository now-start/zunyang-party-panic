package org.nowstart.zunyang.partypanic.domain.message;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.support.ActivityTestLayouts;

class MessageWallStateTest {

    @Test
    void selecting_required_notes_makes_message_wall_ready() {
        MessageWallState state = MessageWallState.initial(ActivityTestLayouts.messageWall());

        state = state.move(Direction.LEFT).move(Direction.LEFT).move(Direction.UP).inspect();
        state = state.move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT);
        state = state.move(Direction.UP).inspect();

        assertTrue(state.selected(MessageNoteId.FIRST_GREETING));
        assertTrue(state.selected(MessageNoteId.QUIET_MOMENT));
        assertTrue(state.readyToReturn());
    }

    @Test
    void optional_note_is_left_for_later() {
        MessageWallState state = MessageWallState.initial(ActivityTestLayouts.messageWall())
            .move(Direction.LEFT)
            .move(Direction.LEFT)
            .move(Direction.DOWN)
            .inspect();

        assertFalse(state.selected(MessageNoteId.WAITING_LINE));
        assertTrue(state.reviewedOptional(MessageNoteId.WAITING_LINE));
        assertFalse(state.readyToReturn());
    }
}
