package org.nowstart.zunyang.partypanic.domain.photo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

class PhotoBayStateTest {

    @Test
    void locking_required_focuses_makes_photo_ready() {
        PhotoBayState state = PhotoBayState.initial();

        state = state.move(Direction.UP).inspect();
        state = state.move(Direction.DOWN).move(Direction.LEFT).move(Direction.LEFT).inspect();
        state = state.move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.RIGHT).inspect();

        assertTrue(state.locked(PhotoFocusId.FRAME_GUIDE));
        assertTrue(state.locked(PhotoFocusId.STOOL_MARK));
        assertTrue(state.locked(PhotoFocusId.KEY_LIGHT));
        assertTrue(state.readyToReturn());
    }

    @Test
    void optional_focus_is_left_for_later() {
        PhotoBayState state = PhotoBayState.initial()
            .move(Direction.DOWN)
            .inspect();

        assertFalse(state.locked(PhotoFocusId.BACKDROP_LINE));
        assertTrue(state.reviewedOptional(PhotoFocusId.BACKDROP_LINE));
        assertFalse(state.readyToReturn());
    }
}
