package org.nowstart.zunyang.partypanic.adapter.out.save;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterActivityType;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterScript;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterStage;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;
import org.nowstart.zunyang.partypanic.domain.chapter.DialoguePage;

class PreferencesChapterStateAdapterTest {

    @Test
    void save_then_load_restores_chapter_state() {
        TestPreferences preferences = new TestPreferences();
        PreferencesChapterStateAdapter adapter = new PreferencesChapterStateAdapter(
            preferences,
            chapterId -> sampleScript()
        );
        ChapterState state = new ChapterState(sampleScript(), 2, ChapterStage.ACTIVITY_READY);

        adapter.save(state);

        ChapterState loaded = adapter.load().orElseThrow();

        assertEquals(ChapterId.SIGNAL, loaded.script().chapterId());
        assertEquals(2, loaded.pageIndex());
        assertEquals(ChapterStage.ACTIVITY_READY, loaded.stage());
    }

    @Test
    void reset_clears_saved_state() {
        TestPreferences preferences = new TestPreferences();
        PreferencesChapterStateAdapter adapter = new PreferencesChapterStateAdapter(
            preferences,
            chapterId -> sampleScript()
        );

        adapter.save(new ChapterState(sampleScript(), 1, ChapterStage.DIALOGUE));
        adapter.reset();

        assertTrue(adapter.load().isEmpty());
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
}
