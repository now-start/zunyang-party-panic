package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ActivityScreenScaffoldTest {

    @Test
    void frame_calculates_shared_grid_metrics() {
        ActivityScreenScaffold scaffold = new ActivityScreenScaffold();

        ActivityScreenScaffold.ActivityFrame frame = scaffold.frame(1280f, 720f, 4, 3);

        assertEquals(252f, frame.gridBottom(), 0.001f);
        assertEquals(1184f, frame.gridWidth(), 0.001f);
        assertEquals(296f, frame.cellWidth(), 0.001f);
        assertEquals(140f, frame.cellHeight(), 0.001f);
        assertEquals(672f, frame.roomTop(), 0.001f);
        assertEquals(1184f, frame.statusWidth(), 0.001f);
    }

    @Test
    void card_bounds_apply_shared_inset_and_scale() {
        ActivityScreenScaffold scaffold = new ActivityScreenScaffold();
        ActivityScreenScaffold.ActivityFrame frame = scaffold.frame(1280f, 720f, 4, 3);

        ActivityScreenScaffold.GridCardBounds bounds = scaffold.cardBounds(frame, 1, 2);

        assertEquals(379.52f, bounds.x(), 0.001f);
        assertEquals(548.8f, bounds.y(), 0.001f);
        assertEquals(224.96f, bounds.width(), 0.001f);
        assertEquals(106.4f, bounds.height(), 0.001f);
    }
}
