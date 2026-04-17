package org.nowstart.zunyang.partypanic.application.chapter;

import org.nowstart.zunyang.partypanic.application.dto.result.ChapterViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadEndingSignalsPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;
import org.nowstart.zunyang.partypanic.domain.session.EndingGrade;
import org.nowstart.zunyang.partypanic.domain.session.EndingGradePolicy;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public final class CompleteChapterInteractor implements CompleteChapterUseCase {

    private final LoadChapterStatePort loadChapterStatePort;
    private final SaveChapterStatePort saveChapterStatePort;
    private final LoadSessionSnapshotPort loadSessionSnapshotPort;
    private final SaveSessionSnapshotPort saveSessionSnapshotPort;
    private final LoadEndingSignalsPort loadEndingSignalsPort;

    public CompleteChapterInteractor(
        LoadChapterStatePort loadChapterStatePort,
        SaveChapterStatePort saveChapterStatePort,
        LoadSessionSnapshotPort loadSessionSnapshotPort,
        SaveSessionSnapshotPort saveSessionSnapshotPort,
        LoadEndingSignalsPort loadEndingSignalsPort
    ) {
        this.loadChapterStatePort = loadChapterStatePort;
        this.saveChapterStatePort = saveChapterStatePort;
        this.loadSessionSnapshotPort = loadSessionSnapshotPort;
        this.saveSessionSnapshotPort = saveSessionSnapshotPort;
        this.loadEndingSignalsPort = loadEndingSignalsPort;
    }

    @Override
    public ChapterViewResult complete() {
        ChapterState currentState = loadChapterStatePort.load()
            .orElseThrow(() -> new IllegalStateException("No active chapter state"));
        ChapterState completedState = currentState.completeActivity();
        saveChapterStatePort.save(completedState);

        ChapterId completedChapterId = completedState.script().chapterId();
        RunProgress runProgress = loadSessionSnapshotPort.load().orElseGet(RunProgress::initial);
        RunProgress nextProgress = runProgress.markCompleted(completedChapterId);
        if (completedChapterId == ChapterId.FINALE) {
            EndingGrade endingGrade = EndingGradePolicy.evaluate(loadEndingSignalsPort.load());
            nextProgress = nextProgress.withEndingGrade(endingGrade);
        }
        saveSessionSnapshotPort.save(nextProgress);
        return ChapterViewMapper.toView(completedState);
    }
}
