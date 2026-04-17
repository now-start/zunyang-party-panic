package org.nowstart.zunyang.partypanic.adapter.out.save;

import com.badlogic.gdx.Preferences;
import java.util.Objects;
import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterScriptPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.ResetChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveChapterStatePort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;

public final class RuntimeChapterStateAdapter
    implements LoadChapterStatePort, SaveChapterStatePort, ResetChapterStatePort {

    private final InMemoryChapterStateAdapter fallbackAdapter = new InMemoryChapterStateAdapter();

    private LoadChapterStatePort loadDelegate = fallbackAdapter;
    private SaveChapterStatePort saveDelegate = fallbackAdapter;
    private ResetChapterStatePort resetDelegate = fallbackAdapter;

    @Override
    public Optional<ChapterState> load() {
        return loadDelegate.load();
    }

    @Override
    public void save(ChapterState chapterState) {
        saveDelegate.save(chapterState);
    }

    @Override
    public void reset() {
        resetDelegate.reset();
    }

    public void bindToPreferences(
        Preferences preferences,
        LoadChapterScriptPort loadChapterScriptPort
    ) {
        Objects.requireNonNull(preferences, "preferences must not be null");
        Objects.requireNonNull(loadChapterScriptPort, "loadChapterScriptPort must not be null");
        PreferencesChapterStateAdapter adapter = new PreferencesChapterStateAdapter(preferences, loadChapterScriptPort);
        bind(adapter, adapter, adapter);
    }

    void bind(
        LoadChapterStatePort loadChapterStatePort,
        SaveChapterStatePort saveChapterStatePort,
        ResetChapterStatePort resetChapterStatePort
    ) {
        Objects.requireNonNull(loadChapterStatePort, "loadChapterStatePort must not be null");
        Objects.requireNonNull(saveChapterStatePort, "saveChapterStatePort must not be null");
        Objects.requireNonNull(resetChapterStatePort, "resetChapterStatePort must not be null");

        Optional<ChapterState> currentState = loadDelegate.load();
        loadDelegate = loadChapterStatePort;
        saveDelegate = saveChapterStatePort;
        resetDelegate = resetChapterStatePort;

        if (loadDelegate.load().isEmpty() && currentState.isPresent()) {
            saveDelegate.save(currentState.orElseThrow());
        }
    }
}
