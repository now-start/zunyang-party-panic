package org.nowstart.zunyang.partypanic.adapter.out.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.common.GridActivityLayout;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.signal.SignalControlId;

class ResourceGridActivityLayoutAdapterTest {

    @Test
    void load_reads_signal_console_layout() {
        ResourceGridActivityLayoutAdapter<SignalControlId> adapter =
            new ResourceGridActivityLayoutAdapter<>("content/layouts/signal-console.json", SignalControlId.class);

        GridActivityLayout<SignalControlId> layout = adapter.load();

        assertEquals(5, layout.width());
        assertEquals(5, layout.height());
        assertEquals(new Position(2, 2), layout.actorStart());
        assertEquals(new Position(1, 3), layout.positionOf(SignalControlId.MIC));
        assertTrue(layout.isWalkable(new Position(2, 2)));
        assertFalse(layout.isWalkable(new Position(1, 3)));
    }
}
