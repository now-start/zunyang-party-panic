package org.nowstart.zunyang.partypanic.application.chapter;

import org.nowstart.zunyang.partypanic.application.dto.result.ChapterViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.SkipChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public final class SkipChapterInteractor implements SkipChapterUseCase {

    private final LoadChapterStatePort loadChapterStatePort;
    private final SaveChapterStatePort saveChapterStatePort;
    private final LoadSessionSnapshotPort loadSessionSnapshotPort;
    private final SaveSessionSnapshotPort saveSessionSnapshotPort;

    public SkipChapterInteractor(
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
    public ChapterViewResult skip() {
        ChapterState currentState = loadChapterStatePort.load()
            .orElseThrow(() -> new IllegalStateException("No active chapter state"));
        ChapterState skippedState = currentState.skipDialogue();
        saveChapterStatePort.save(skippedState);

        if (!currentState.completed() && skippedState.completed()) {
            RunProgress runProgress = loadSessionSnapshotPort.load().orElseGet(RunProgress::initial);
            saveSessionSnapshotPort.save(runProgress.markCompleted(skippedState.script().chapterId()));
        }
        return ChapterViewMapper.toView(skippedState);
    }
}
