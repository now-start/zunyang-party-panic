package org.nowstart.zunyang.partypanic.adapter.out.save;

import com.badlogic.gdx.Preferences;
import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterScriptPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.ResetChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveChapterStatePort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterScript;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterStage;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;

public final class PreferencesChapterStateAdapter
    implements LoadChapterStatePort, SaveChapterStatePort, ResetChapterStatePort {

    private static final String KEY_CHAPTER_ID = "chapter.id";
    private static final String KEY_PAGE_INDEX = "chapter.pageIndex";
    private static final String KEY_STAGE = "chapter.stage";

    private final Preferences preferences;
    private final LoadChapterScriptPort loadChapterScriptPort;

    public PreferencesChapterStateAdapter(
        Preferences preferences,
        LoadChapterScriptPort loadChapterScriptPort
    ) {
        this.preferences = preferences;
        this.loadChapterScriptPort = loadChapterScriptPort;
    }

    @Override
    public Optional<ChapterState> load() {
        if (!preferences.contains(KEY_CHAPTER_ID)
            || !preferences.contains(KEY_PAGE_INDEX)
            || !preferences.contains(KEY_STAGE)) {
            return Optional.empty();
        }

        ChapterId chapterId = ChapterId.valueOf(preferences.getString(KEY_CHAPTER_ID));
        ChapterScript script = loadChapterScriptPort.load(chapterId);
        int pageIndex = preferences.getInteger(KEY_PAGE_INDEX);
        if (pageIndex < 0 || pageIndex >= script.pages().size()) {
            throw new IllegalStateException("Saved chapter page index is out of range: " + pageIndex);
        }

        ChapterStage stage = ChapterStage.valueOf(preferences.getString(KEY_STAGE));
        return Optional.of(new ChapterState(script, pageIndex, stage));
    }

    @Override
    public void save(ChapterState chapterState) {
        preferences.putString(KEY_CHAPTER_ID, chapterState.script().chapterId().name());
        preferences.putInteger(KEY_PAGE_INDEX, chapterState.pageIndex());
        preferences.putString(KEY_STAGE, chapterState.stage().name());
        preferences.flush();
    }

    @Override
    public void reset() {
        preferences.remove(KEY_CHAPTER_ID);
        preferences.remove(KEY_PAGE_INDEX);
        preferences.remove(KEY_STAGE);
        preferences.flush();
    }
}
