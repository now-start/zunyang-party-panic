package org.nowstart.zunyang.partypanic.domain.handover;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

class HandoverCorridorStateTest {

    @Test
    void collecting_required_clues_makes_handover_ready() {
        HandoverCorridorState state = HandoverCorridorState.initial();

        state = state.inspect();
        state = state.move(Direction.LEFT).move(Direction.LEFT).move(Direction.UP).inspect();
        state = state.move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT);
        state = state.move(Direction.UP).inspect();

        assertTrue(state.collected(HandoverClueId.PHOTO_FRAME));
        assertTrue(state.collected(HandoverClueId.OLD_CUESHEET));
        assertTrue(state.collected(HandoverClueId.MEMO_BOARD));
        assertTrue(state.readyToReturn());
    }

    @Test
    void optional_clue_is_left_for_later() {
        HandoverCorridorState state = HandoverCorridorState.initial()
            .move(Direction.LEFT)
            .move(Direction.LEFT)
            .move(Direction.DOWN)
            .inspect();

        assertFalse(state.collected(HandoverClueId.PROJECTOR));
        assertTrue(state.reviewedOptional(HandoverClueId.PROJECTOR));
        assertFalse(state.readyToReturn());
    }
}
