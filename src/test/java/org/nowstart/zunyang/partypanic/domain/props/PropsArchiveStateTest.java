package org.nowstart.zunyang.partypanic.domain.props;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

class PropsArchiveStateTest {

    @Test
    void collecting_only_required_items_makes_archive_ready() {
        PropsArchiveState state = PropsArchiveState.initial();

        state = state.inspect();
        state = state.move(Direction.LEFT).move(Direction.LEFT).move(Direction.UP).inspect();
        state = state.move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.UP).inspect();

        assertTrue(state.collected(PropsItemId.RIBBON_BOX));
        assertTrue(state.collected(PropsItemId.TOPPER_CASE));
        assertTrue(state.collected(PropsItemId.GEL_PACK));
        assertTrue(state.readyToReturn());
    }

    @Test
    void optional_item_is_left_in_place() {
        PropsArchiveState state = PropsArchiveState.initial()
            .move(Direction.LEFT)
            .move(Direction.LEFT)
            .move(Direction.DOWN)
            .inspect();

        assertFalse(state.collected(PropsItemId.FABRIC_ROLL));
        assertTrue(state.reviewedOptional(PropsItemId.FABRIC_ROLL));
        assertFalse(state.readyToReturn());
    }
}
