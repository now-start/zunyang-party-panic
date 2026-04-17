package org.nowstart.zunyang.partypanic.adapter.out.save;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

class RuntimeSessionSnapshotAdapterTest {

    @Test
    void bind_moves_existing_fallback_progress_when_target_is_empty() {
        RuntimeSessionSnapshotAdapter adapter = new RuntimeSessionSnapshotAdapter();
        RunProgress progress = RunProgress.initial().markCompleted(ChapterId.SIGNAL);
        RecordingSessionPort target = new RecordingSessionPort();
        target.loaded = Optional.empty();

        adapter.save(progress);
        adapter.bind(target, target);

        assertTrue(target.saved.isCompleted(ChapterId.SIGNAL));
        assertTrue(adapter.load().orElseThrow().isCompleted(ChapterId.SIGNAL));
    }

    @Test
    void bind_keeps_existing_target_snapshot_without_overwriting_it() {
        RuntimeSessionSnapshotAdapter adapter = new RuntimeSessionSnapshotAdapter();
        RecordingSessionPort target = new RecordingSessionPort();
        target.loaded = Optional.of(
            RunProgress.initial()
                .markCompleted(ChapterId.SIGNAL)
                .markCompleted(ChapterId.PROPS)
        );

        adapter.bind(target, target);

        assertTrue(adapter.load().orElseThrow().isCompleted(ChapterId.PROPS));
        assertNull(target.saved);
    }

    @Test
    void save_after_bind_uses_bound_delegate() {
        RuntimeSessionSnapshotAdapter adapter = new RuntimeSessionSnapshotAdapter();
        RecordingSessionPort target = new RecordingSessionPort();
        adapter.bind(target, target);

        RunProgress progress = RunProgress.initial().markCompleted(ChapterId.SIGNAL);
        adapter.save(progress);

        assertEquals(progress, target.saved);
    }

    private static final class RecordingSessionPort
        implements LoadSessionSnapshotPort, SaveSessionSnapshotPort {

        private Optional<RunProgress> loaded = Optional.empty();
        private RunProgress saved;

        @Override
        public Optional<RunProgress> load() {
            return loaded;
        }

        @Override
        public void save(RunProgress runProgress) {
            this.saved = runProgress;
            this.loaded = Optional.of(runProgress);
        }
    }
}
