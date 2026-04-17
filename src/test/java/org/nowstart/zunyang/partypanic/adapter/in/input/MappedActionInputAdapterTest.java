package org.nowstart.zunyang.partypanic.adapter.in.input;

import com.badlogic.gdx.Input;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MappedActionInputAdapterTest {

    @Test
    void queuesMappedActionsInOrder() {
        MappedActionInputAdapter<String> adapter = new MappedActionInputAdapter<>(Map.of(
                Input.Keys.ENTER, "confirm",
                Input.Keys.ESCAPE, "back"
        ));

        adapter.keyDown(Input.Keys.ENTER);
        adapter.keyUp(Input.Keys.ENTER);
        adapter.keyDown(Input.Keys.ESCAPE);

        assertEquals("confirm", adapter.pollAction());
        assertEquals("back", adapter.pollAction());
        assertNull(adapter.pollAction());
    }

    @Test
    void suppressesRepeatedKeyDownUntilKeyIsReleased() {
        MappedActionInputAdapter<String> adapter = new MappedActionInputAdapter<>(Map.of(
                Input.Keys.SPACE, "confirm"
        ));

        adapter.keyDown(Input.Keys.SPACE);
        adapter.keyDown(Input.Keys.SPACE);
        adapter.keyUp(Input.Keys.SPACE);
        adapter.keyDown(Input.Keys.SPACE);

        assertEquals("confirm", adapter.pollAction());
        assertEquals("confirm", adapter.pollAction());
        assertNull(adapter.pollAction());
    }

    @Test
    void ignoresUnmappedKeysWithoutChangingQueueState() {
        MappedActionInputAdapter<String> adapter = new MappedActionInputAdapter<>(Map.of(
                Input.Keys.ENTER, "confirm"
        ));

        adapter.keyDown(Input.Keys.LEFT);
        adapter.keyUp(Input.Keys.LEFT);
        adapter.keyDown(Input.Keys.ENTER);

        assertEquals("confirm", adapter.pollAction());
        assertNull(adapter.pollAction());
    }
}
