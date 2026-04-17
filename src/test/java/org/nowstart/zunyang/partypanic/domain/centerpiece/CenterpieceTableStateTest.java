package org.nowstart.zunyang.partypanic.domain.centerpiece;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.support.ActivityTestLayouts;

class CenterpieceTableStateTest {

    @Test
    void placing_required_points_makes_table_ready() {
        CenterpieceTableState state = CenterpieceTableState.initial(ActivityTestLayouts.centerpieceTable());

        state = state.move(Direction.UP).inspect();
        state = state.move(Direction.DOWN).move(Direction.LEFT).inspect();
        state = state.move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT).inspect();

        assertTrue(state.placed(CenterpiecePlacementId.TOPPER_SLOT));
        assertTrue(state.placed(CenterpiecePlacementId.CANDLE_ARC));
        assertTrue(state.placed(CenterpiecePlacementId.RIBBON_LINE));
        assertTrue(state.readyToReturn());
    }

    @Test
    void optional_point_is_left_for_later() {
        CenterpieceTableState state = CenterpieceTableState.initial(ActivityTestLayouts.centerpieceTable())
            .move(Direction.DOWN)
            .inspect();

        assertFalse(state.placed(CenterpiecePlacementId.CLOTH_FOLD));
        assertTrue(state.reviewedOptional(CenterpiecePlacementId.CLOTH_FOLD));
        assertFalse(state.readyToReturn());
    }
}
