package org.nowstart.zunyang.partypanic.adapter.out.save;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHubStatePort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.hub.HubActor;
import org.nowstart.zunyang.partypanic.domain.hub.HubHotspot;
import org.nowstart.zunyang.partypanic.domain.hub.HubLayout;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;

class RuntimeHubStateAdapterTest {

    @Test
    void bind_moves_existing_fallback_state_when_target_is_empty() {
        RuntimeHubStateAdapter adapter = new RuntimeHubStateAdapter();
        HubState state = sampleHubState();
        RecordingHubPort target = new RecordingHubPort();
        target.loaded = Optional.empty();

        adapter.save(state);
        adapter.bind(target, target);

        assertEquals(state, target.saved);
        assertEquals(state, adapter.load().orElseThrow());
    }

    @Test
    void bind_keeps_existing_target_state_without_overwriting_it() {
        RuntimeHubStateAdapter adapter = new RuntimeHubStateAdapter();
        RecordingHubPort target = new RecordingHubPort();
        target.loaded = Optional.of(sampleHubState().move(Direction.RIGHT));

        adapter.bind(target, target);

        assertEquals(Direction.RIGHT, adapter.load().orElseThrow().actor().facing());
        assertNull(target.saved);
    }

    @Test
    void save_after_bind_uses_bound_delegate() {
        RuntimeHubStateAdapter adapter = new RuntimeHubStateAdapter();
        RecordingHubPort target = new RecordingHubPort();
        HubState state = sampleHubState();

        adapter.bind(target, target);
        adapter.save(state);

        assertEquals(state, target.saved);
    }

    private static HubState sampleHubState() {
        HubLayout layout = new HubLayout(
            5,
            4,
            new Position(1, 1),
            List.of(new HubHotspot(
                ChapterId.SIGNAL,
                "첫 신호",
                new Position(1, 2),
                "첫 신호 큐를 맞추는 자리다.",
                "아직 이 자리는 잠겨 있다."
            ))
        );
        return new HubState(
            layout,
            new HubActor(new Position(2, 1), Direction.LEFT),
            ChapterId.SIGNAL,
            "첫 신호 쪽으로 이동한다."
        );
    }

    private static final class RecordingHubPort implements LoadHubStatePort, SaveHubStatePort {
        private Optional<HubState> loaded = Optional.empty();
        private HubState saved;

        @Override
        public Optional<HubState> load() {
            return loaded;
        }

        @Override
        public void save(HubState hubState) {
            this.saved = hubState;
            this.loaded = Optional.of(hubState);
        }
    }
}
