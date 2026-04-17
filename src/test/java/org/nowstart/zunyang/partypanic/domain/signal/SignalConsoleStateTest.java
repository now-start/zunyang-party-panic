package org.nowstart.zunyang.partypanic.domain.signal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.support.ActivityTestLayouts;

class SignalConsoleStateTest {

    @Test
    void moving_and_adjusting_controls_can_stabilize_signal() {
        SignalConsoleState state = SignalConsoleState.initial(ActivityTestLayouts.signalConsole());

        state = state.move(Direction.LEFT).move(Direction.UP).inspect().adjustActive(1);
        state = state.move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.UP).inspect().adjustActive(-1);
        state = state.move(Direction.LEFT).move(Direction.LEFT).move(Direction.DOWN).inspect().adjustActive(1);
        state = state.move(Direction.RIGHT).move(Direction.RIGHT).move(Direction.DOWN).inspect().adjustActive(-1);

        assertTrue(state.stabilized());
        assertEquals("좋아. 첫 신호가 흔들리지 않겠다.", state.statusMessage());
    }
}
