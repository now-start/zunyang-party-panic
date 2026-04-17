package org.nowstart.zunyang.partypanic.adapter.out.save;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.ResetChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveChapterStatePort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterActivityType;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterScript;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterStage;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;
import org.nowstart.zunyang.partypanic.domain.chapter.DialoguePage;

class RuntimeChapterStateAdapterTest {

    @Test
    void bind_moves_existing_fallback_state_when_target_is_empty() {
        RuntimeChapterStateAdapter adapter = new RuntimeChapterStateAdapter();
        ChapterState state = sampleChapterState();
        RecordingChapterPort target = new RecordingChapterPort();
        target.loaded = Optional.empty();

        adapter.save(state);
        adapter.bind(target, target, target);

        assertEquals(state, target.saved);
        assertEquals(state, adapter.load().orElseThrow());
    }

    @Test
    void bind_keeps_existing_target_state_without_overwriting_it() {
        RuntimeChapterStateAdapter adapter = new RuntimeChapterStateAdapter();
        RecordingChapterPort target = new RecordingChapterPort();
        target.loaded = Optional.of(new ChapterState(sampleScript(), 2, ChapterStage.ACTIVITY_READY));

        adapter.bind(target, target, target);

        assertEquals(ChapterStage.ACTIVITY_READY, adapter.load().orElseThrow().stage());
        assertNull(target.saved);
    }

    @Test
    void save_and_reset_after_bind_use_bound_delegate() {
        RuntimeChapterStateAdapter adapter = new RuntimeChapterStateAdapter();
        RecordingChapterPort target = new RecordingChapterPort();
        ChapterState state = sampleChapterState();

        adapter.bind(target, target, target);
        adapter.save(state);
        adapter.reset();

        assertEquals(state, target.saved);
        assertTrue(target.resetCalled);
        assertTrue(adapter.load().isEmpty());
    }

    private static ChapterState sampleChapterState() {
        return new ChapterState(sampleScript(), 1, ChapterStage.DIALOGUE);
    }

    private static ChapterScript sampleScript() {
        return new ChapterScript(
            ChapterId.SIGNAL,
            "첫 신호를 맞추다",
            "큐 부스 정비",
            "signal",
            ChapterActivityType.SIGNAL_CONSOLE,
            List.of(
                new DialoguePage("조력자", "첫 대사"),
                new DialoguePage("스트리머", "둘째 대사"),
                new DialoguePage("조력자", "셋째 대사")
            )
        );
    }

    private static final class RecordingChapterPort
        implements LoadChapterStatePort, SaveChapterStatePort, ResetChapterStatePort {

        private Optional<ChapterState> loaded = Optional.empty();
        private ChapterState saved;
        private boolean resetCalled;

        @Override
        public Optional<ChapterState> load() {
            return loaded;
        }

        @Override
        public void save(ChapterState chapterState) {
            this.saved = chapterState;
            this.loaded = Optional.of(chapterState);
        }

        @Override
        public void reset() {
            this.resetCalled = true;
            this.loaded = Optional.empty();
        }
    }
}
