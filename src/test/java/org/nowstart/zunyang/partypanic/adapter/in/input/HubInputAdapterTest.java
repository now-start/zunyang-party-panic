package org.nowstart.zunyang.partypanic.adapter.in.input;

import com.badlogic.gdx.Input;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.model.Direction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HubInputAdapterTest {

    @Test
    void tracksDirectionalKeyStateUntilRelease() {
        HubInputAdapter adapter = new HubInputAdapter();

        adapter.keyDown(Input.Keys.D);
        assertEquals(Direction.RIGHT, adapter.pressedDirection());

        adapter.keyDown(Input.Keys.W);
        assertEquals(Direction.RIGHT, adapter.pressedDirection());

        adapter.keyUp(Input.Keys.D);
        assertEquals(Direction.UP, adapter.pressedDirection());

        adapter.keyUp(Input.Keys.W);
        assertNull(adapter.pressedDirection());
    }

    @Test
    void consumesConfirmAndBackRequestsOncePerPress() {
        HubInputAdapter adapter = new HubInputAdapter();

        adapter.keyDown(Input.Keys.ENTER);
        adapter.keyDown(Input.Keys.ESCAPE);

        assertTrue(adapter.consumeConfirmRequested());
        assertFalse(adapter.consumeConfirmRequested());
        assertTrue(adapter.consumeBackRequested());
        assertFalse(adapter.consumeBackRequested());
    }
}
