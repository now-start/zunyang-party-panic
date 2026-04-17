package org.nowstart.zunyang.partypanic.domain.finale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

class FinaleStageStateTest {

    @Test
    void checking_required_points_makes_finale_ready() {
        FinaleStageState state = FinaleStageState.initial();

        state = state.inspect();
        state = state.move(Direction.LEFT).move(Direction.LEFT).move(Direction.UP).inspect();
        state = state.move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT);
        state = state.move(Direction.UP).inspect();

        assertTrue(state.checked(FinaleCheckpointId.STREAMER_MARK));
        assertTrue(state.checked(FinaleCheckpointId.COUNTDOWN_LAMP));
        assertTrue(state.checked(FinaleCheckpointId.GO_CUE_PANEL));
        assertTrue(state.readyToReturn());
    }

    @Test
    void optional_point_is_left_for_later() {
        FinaleStageState state = FinaleStageState.initial()
            .move(Direction.LEFT)
            .move(Direction.LEFT)
            .move(Direction.DOWN)
            .inspect();

        assertFalse(state.checked(FinaleCheckpointId.SIDE_CURTAIN));
        assertTrue(state.reviewedOptional(FinaleCheckpointId.SIDE_CURTAIN));
        assertFalse(state.readyToReturn());
    }
}
