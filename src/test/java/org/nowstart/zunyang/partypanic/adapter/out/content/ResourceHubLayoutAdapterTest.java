package org.nowstart.zunyang.partypanic.adapter.out.content;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.hub.HubHotspot;
import org.nowstart.zunyang.partypanic.domain.hub.HubLayout;

class ResourceHubLayoutAdapterTest {

    @Test
    void load_reads_authored_hub_layout() {
        ResourceHubLayoutAdapter adapter = new ResourceHubLayoutAdapter();

        HubLayout layout = adapter.load();
        HubHotspot signalHotspot = layout.hotspots().stream()
            .filter(hotspot -> hotspot.chapterId() == ChapterId.SIGNAL)
            .findFirst()
            .orElseThrow();

        assertEquals(7, layout.width());
        assertEquals(5, layout.height());
        assertEquals(new Position(3, 2), layout.startPosition());
        assertEquals(7, layout.hotspots().size());
        assertEquals(new Position(1, 4), signalHotspot.position());
    }
}
