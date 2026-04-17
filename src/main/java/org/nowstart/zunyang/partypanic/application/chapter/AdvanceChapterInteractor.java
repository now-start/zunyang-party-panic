package org.nowstart.zunyang.partypanic.application.chapter;

import org.nowstart.zunyang.partypanic.application.dto.result.ChapterViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.AdvanceChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public final class AdvanceChapterInteractor implements AdvanceChapterUseCase {

    private final LoadChapterStatePort loadChapterStatePort;
    private final SaveChapterStatePort saveChapterStatePort;
    private final LoadSessionSnapshotPort loadSessionSnapshotPort;
    private final SaveSessionSnapshotPort saveSessionSnapshotPort;

    public AdvanceChapterInteractor(
        LoadChapterStatePort loadChapterStatePort,
        SaveChapterStatePort saveChapterStatePort,
        LoadSessionSnapshotPort loadSessionSnapshotPort,
        SaveSessionSnapshotPort saveSessionSnapshotPort
    ) {
        this.loadChapterStatePort = loadChapterStatePort;
        this.saveChapterStatePort = saveChapterStatePort;
        this.loadSessionSnapshotPort = loadSessionSnapshotPort;
        this.saveSessionSnapshotPort = saveSessionSnapshotPort;
    }

    @Override
    public ChapterViewResult advance() {
        ChapterState currentState = loadChapterStatePort.load()
            .orElseThrow(() -> new IllegalStateException("No active chapter state"));
        ChapterState nextState = currentState.advance();
        saveChapterStatePort.save(nextState);
        if (!currentState.completed() && nextState.completed()) {
            RunProgress runProgress = loadSessionSnapshotPort.load().orElseGet(RunProgress::initial);
            saveSessionSnapshotPort.save(runProgress.markCompleted(nextState.script().chapterId()));
        }
        return ChapterViewMapper.toView(nextState);
    }
}
