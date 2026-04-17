package org.nowstart.zunyang.partypanic.adapter.out.save;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubLayoutPort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.hub.HubActor;
import org.nowstart.zunyang.partypanic.domain.hub.HubHotspot;
import org.nowstart.zunyang.partypanic.domain.hub.HubLayout;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;

class PreferencesHubStateAdapterTest {

    @Test
    void save_then_load_restores_hub_state() {
        TestPreferences preferences = new TestPreferences();
        LoadHubLayoutPort layoutPort = PreferencesHubStateAdapterTest::sampleLayout;
        PreferencesHubStateAdapter adapter = new PreferencesHubStateAdapter(preferences, layoutPort);
        HubState state = new HubState(
            sampleLayout(),
            new HubActor(new Position(2, 1), Direction.LEFT),
            ChapterId.SIGNAL,
            "첫 신호 쪽으로 이동한다."
        );

        adapter.save(state);

        HubState loaded = adapter.load().orElseThrow();

        assertEquals(new Position(2, 1), loaded.actor().position());
        assertEquals(Direction.LEFT, loaded.actor().facing());
        assertEquals(ChapterId.SIGNAL, loaded.activeHotspot());
        assertEquals("첫 신호 쪽으로 이동한다.", loaded.currentMessage());
    }

    @Test
    void load_without_required_keys_returns_empty() {
        PreferencesHubStateAdapter adapter = new PreferencesHubStateAdapter(
            new TestPreferences(),
            PreferencesHubStateAdapterTest::sampleLayout
        );

        assertTrue(adapter.load().isEmpty());
    }

    private static HubLayout sampleLayout() {
        return new HubLayout(
            4,
            3,
            new Position(1, 1),
            List.of(new HubHotspot(
                ChapterId.SIGNAL,
                "첫 신호",
                new Position(1, 2),
                "첫 신호 큐를 맞추는 자리다.",
                "아직 이 자리는 잠겨 있다."
            ))
        );
    }
}
