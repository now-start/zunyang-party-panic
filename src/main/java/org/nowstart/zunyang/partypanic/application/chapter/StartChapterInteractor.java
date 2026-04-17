package org.nowstart.zunyang.partypanic.application.chapter;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.dto.result.ChapterViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.StartChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterScriptPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveChapterStatePort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterScript;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;

public final class StartChapterInteractor implements StartChapterUseCase {

    private final LoadChapterScriptPort loadChapterScriptPort;
    private final LoadChapterStatePort loadChapterStatePort;
    private final SaveChapterStatePort saveChapterStatePort;

    public StartChapterInteractor(
        LoadChapterScriptPort loadChapterScriptPort,
        LoadChapterStatePort loadChapterStatePort,
        SaveChapterStatePort saveChapterStatePort
    ) {
        this.loadChapterScriptPort = loadChapterScriptPort;
        this.loadChapterStatePort = loadChapterStatePort;
        this.saveChapterStatePort = saveChapterStatePort;
    }

    public StartChapterInteractor(
        LoadChapterScriptPort loadChapterScriptPort,
        SaveChapterStatePort saveChapterStatePort
    ) {
        this(loadChapterScriptPort, () -> Optional.empty(), saveChapterStatePort);
    }

    @Override
    public ChapterViewResult start(ChapterId chapterId) {
        Optional<ChapterState> savedState = loadChapterStatePort.load()
            .filter(state -> state.script().chapterId() == chapterId)
            .filter(state -> !state.completed());
        if (savedState.isPresent()) {
            return ChapterViewMapper.toView(savedState.orElseThrow());
        }

        ChapterScript script = loadChapterScriptPort.load(chapterId);
        ChapterState state = ChapterState.start(script);
        saveChapterStatePort.save(state);
        return ChapterViewMapper.toView(state);
    }
}
