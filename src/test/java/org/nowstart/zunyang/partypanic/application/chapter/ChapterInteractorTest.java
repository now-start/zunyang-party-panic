package org.nowstart.zunyang.partypanic.application.chapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.dto.result.ChapterViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.CompleteChapterUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterScriptPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadEndingSignalsPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterActivityType;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterScript;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;
import org.nowstart.zunyang.partypanic.domain.chapter.DialoguePage;
import org.nowstart.zunyang.partypanic.domain.session.EndingGrade;
import org.nowstart.zunyang.partypanic.domain.session.EndingSignals;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

class ChapterInteractorTest {

    @Test
    void start_loads_first_dialogue_page() {
        InMemoryChapterStatePort statePort = new InMemoryChapterStatePort();
        LoadChapterScriptPort scriptPort = chapterId -> sampleScript();

        StartChapterInteractor interactor = new StartChapterInteractor(scriptPort, statePort);

        ChapterViewResult result = interactor.start(ChapterId.SIGNAL);

        assertEquals("SIGNAL", result.chapterId());
        assertEquals("첫 신호를 맞추다", result.title());
        assertEquals("조력자", result.speaker());
        assertEquals("큐 부스부터 안정시켜야 한다.", result.text());
        assertEquals(1, result.pageNumber());
        assertEquals(3, result.totalPages());
        assertFalse(result.activityReady());
        assertFalse(result.completed());
    }

    @Test
    void start_resumes_saved_state_for_same_chapter_when_not_completed() {
        InMemoryChapterStatePort statePort = new InMemoryChapterStatePort();
        LoadChapterScriptPort scriptPort = chapterId -> sampleActivityScript(ChapterId.SIGNAL, ChapterActivityType.SIGNAL_CONSOLE);
        StartChapterInteractor interactor = new StartChapterInteractor(scriptPort, statePort, statePort);
        ChapterState resumedState = new ChapterState(sampleActivityScript(ChapterId.SIGNAL, ChapterActivityType.SIGNAL_CONSOLE), 2, org.nowstart.zunyang.partypanic.domain.chapter.ChapterStage.ACTIVITY_READY);
        statePort.save(resumedState);

        ChapterViewResult result = interactor.start(ChapterId.SIGNAL);

        assertEquals(3, result.pageNumber());
        assertTrue(result.activityReady());
        assertFalse(result.completed());
        assertEquals("좋아. 이제 실제 신호를 맞춘다.", result.text());
    }

    @Test
    void start_ignores_completed_saved_state_and_restarts_chapter() {
        InMemoryChapterStatePort statePort = new InMemoryChapterStatePort();
        LoadChapterScriptPort scriptPort = chapterId -> sampleActivityScript(ChapterId.SIGNAL, ChapterActivityType.SIGNAL_CONSOLE);
        StartChapterInteractor interactor = new StartChapterInteractor(scriptPort, statePort, statePort);
        statePort.save(new ChapterState(
            sampleActivityScript(ChapterId.SIGNAL, ChapterActivityType.SIGNAL_CONSOLE),
            2,
            org.nowstart.zunyang.partypanic.domain.chapter.ChapterStage.COMPLETED
        ));

        ChapterViewResult result = interactor.start(ChapterId.SIGNAL);

        assertEquals(1, result.pageNumber());
        assertFalse(result.activityReady());
        assertFalse(result.completed());
        assertEquals("큐 부스부터 안정시켜야 한다.", result.text());
    }

    @Test
    void advance_moves_to_next_page() {
        InMemoryChapterStatePort statePort = new InMemoryChapterStatePort();
        LoadChapterScriptPort scriptPort = chapterId -> sampleScript();
        InMemoryRunProgressPort progressPort = new InMemoryRunProgressPort();
        StartChapterInteractor startInteractor = new StartChapterInteractor(scriptPort, statePort);
        AdvanceChapterInteractor advanceInteractor = new AdvanceChapterInteractor(statePort, statePort, progressPort, progressPort);

        startInteractor.start(ChapterId.SIGNAL);
        ChapterViewResult result = advanceInteractor.advance();

        assertEquals("스트리머", result.speaker());
        assertEquals("첫 숨만 안정적이면 바로 들어갈 수 있어.", result.text());
        assertEquals(2, result.pageNumber());
        assertFalse(result.activityReady());
        assertFalse(result.completed());
    }

    @Test
    void third_advance_marks_view_complete_after_last_page_is_shown() {
        InMemoryChapterStatePort statePort = new InMemoryChapterStatePort();
        LoadChapterScriptPort scriptPort = chapterId -> sampleScript();
        InMemoryRunProgressPort progressPort = new InMemoryRunProgressPort();
        StartChapterInteractor startInteractor = new StartChapterInteractor(scriptPort, statePort);
        AdvanceChapterInteractor advanceInteractor = new AdvanceChapterInteractor(statePort, statePort, progressPort, progressPort);

        startInteractor.start(ChapterId.SIGNAL);
        advanceInteractor.advance();
        ChapterViewResult lastPage = advanceInteractor.advance();
        ChapterViewResult result = advanceInteractor.advance();

        assertEquals(3, lastPage.pageNumber());
        assertEquals("좋아. 첫 신호는 흔들리지 않겠다.", lastPage.text());
        assertFalse(lastPage.activityReady());
        assertFalse(lastPage.completed());
        assertEquals(3, result.pageNumber());
        assertFalse(result.activityReady());
        assertTrue(result.completed());
        assertTrue(progressPort.load().orElseThrow().isCompleted(ChapterId.SIGNAL));
        assertTrue(progressPort.load().orElseThrow().isUnlocked(ChapterId.PROPS));
    }

    @Test
    void activity_chapter_becomes_ready_before_completion() {
        InMemoryChapterStatePort statePort = new InMemoryChapterStatePort();
        LoadChapterScriptPort scriptPort = chapterId -> sampleActivityScript(ChapterId.SIGNAL, ChapterActivityType.SIGNAL_CONSOLE);
        InMemoryRunProgressPort progressPort = new InMemoryRunProgressPort();
        StartChapterInteractor startInteractor = new StartChapterInteractor(scriptPort, statePort);
        AdvanceChapterInteractor advanceInteractor = new AdvanceChapterInteractor(statePort, statePort, progressPort, progressPort);

        startInteractor.start(ChapterId.SIGNAL);
        advanceInteractor.advance();
        advanceInteractor.advance();
        ChapterViewResult result = advanceInteractor.advance();

        assertEquals(3, result.pageNumber());
        assertTrue(result.activityReady());
        assertFalse(result.completed());
        assertFalse(progressPort.load().orElseThrow().isCompleted(ChapterId.SIGNAL));
    }

    @Test
    void complete_marks_activity_chapter_and_updates_progress() {
        InMemoryChapterStatePort statePort = new InMemoryChapterStatePort();
        LoadChapterScriptPort scriptPort = chapterId -> sampleActivityScript(ChapterId.SIGNAL, ChapterActivityType.SIGNAL_CONSOLE);
        InMemoryRunProgressPort progressPort = new InMemoryRunProgressPort();
        LoadEndingSignalsPort endingSignalsPort = () -> new EndingSignals(0, 0, 0);
        StartChapterInteractor startInteractor = new StartChapterInteractor(scriptPort, statePort);
        AdvanceChapterInteractor advanceInteractor = new AdvanceChapterInteractor(statePort, statePort, progressPort, progressPort);
        CompleteChapterUseCase completeInteractor = new CompleteChapterInteractor(
            statePort,
            statePort,
            progressPort,
            progressPort,
            endingSignalsPort
        );

        startInteractor.start(ChapterId.SIGNAL);
        advanceInteractor.advance();
        advanceInteractor.advance();
        advanceInteractor.advance();
        ChapterViewResult result = completeInteractor.complete();

        assertFalse(result.activityReady());
        assertTrue(result.completed());
        assertTrue(progressPort.load().orElseThrow().isCompleted(ChapterId.SIGNAL));
        assertTrue(progressPort.load().orElseThrow().isUnlocked(ChapterId.PROPS));
    }

    @Test
    void complete_finale_assigns_ending_grade() {
        InMemoryChapterStatePort statePort = new InMemoryChapterStatePort();
        LoadChapterScriptPort scriptPort = chapterId -> sampleActivityScript(ChapterId.FINALE, ChapterActivityType.FINALE_STAGE);
        InMemoryRunProgressPort progressPort = new InMemoryRunProgressPort();
        LoadEndingSignalsPort endingSignalsPort = () -> new EndingSignals(4, 2, 2);
        StartChapterInteractor startInteractor = new StartChapterInteractor(scriptPort, statePort);
        AdvanceChapterInteractor advanceInteractor = new AdvanceChapterInteractor(statePort, statePort, progressPort, progressPort);
        CompleteChapterUseCase completeInteractor = new CompleteChapterInteractor(
            statePort,
            statePort,
            progressPort,
            progressPort,
            endingSignalsPort
        );

        startInteractor.start(ChapterId.FINALE);
        advanceInteractor.advance();
        advanceInteractor.advance();
        advanceInteractor.advance();
        completeInteractor.complete();

        assertEquals(EndingGrade.SHARED_STAGE, progressPort.load().orElseThrow().endingGrade());
    }

    @Test
    void skip_dialogue_only_chapter_marks_completion_and_unlocks_next() {
        InMemoryChapterStatePort statePort = new InMemoryChapterStatePort();
        LoadChapterScriptPort scriptPort = chapterId -> sampleScript();
        InMemoryRunProgressPort progressPort = new InMemoryRunProgressPort();
        StartChapterInteractor startInteractor = new StartChapterInteractor(scriptPort, statePort);
        SkipChapterInteractor skipInteractor = new SkipChapterInteractor(
            statePort,
            statePort,
            progressPort,
            progressPort
        );

        startInteractor.start(ChapterId.SIGNAL);
        ChapterViewResult result = skipInteractor.skip();

        assertEquals(3, result.pageNumber());
        assertTrue(result.completed());
        assertFalse(result.activityReady());
        assertTrue(progressPort.load().orElseThrow().isCompleted(ChapterId.SIGNAL));
        assertTrue(progressPort.load().orElseThrow().isUnlocked(ChapterId.PROPS));
    }

    @Test
    void skip_activity_chapter_stops_at_activity_ready_without_completing() {
        InMemoryChapterStatePort statePort = new InMemoryChapterStatePort();
        LoadChapterScriptPort scriptPort = chapterId -> sampleActivityScript(ChapterId.SIGNAL, ChapterActivityType.SIGNAL_CONSOLE);
        InMemoryRunProgressPort progressPort = new InMemoryRunProgressPort();
        StartChapterInteractor startInteractor = new StartChapterInteractor(scriptPort, statePort);
        SkipChapterInteractor skipInteractor = new SkipChapterInteractor(
            statePort,
            statePort,
            progressPort,
            progressPort
        );

        startInteractor.start(ChapterId.SIGNAL);
        ChapterViewResult result = skipInteractor.skip();

        assertEquals(3, result.pageNumber());
        assertTrue(result.activityReady());
        assertFalse(result.completed());
        assertFalse(progressPort.load().orElseThrow().isCompleted(ChapterId.SIGNAL));
    }

    private static ChapterScript sampleScript() {
        return new ChapterScript(
            ChapterId.SIGNAL,
            "첫 신호를 맞추다",
            "큐 부스 정비",
            "signal",
            ChapterActivityType.NONE,
            List.of(
                new DialoguePage("조력자", "큐 부스부터 안정시켜야 한다."),
                new DialoguePage("스트리머", "첫 숨만 안정적이면 바로 들어갈 수 있어."),
                new DialoguePage("조력자", "좋아. 첫 신호는 흔들리지 않겠다.")
            )
        );
    }

    private static ChapterScript sampleActivityScript(ChapterId chapterId, ChapterActivityType activityType) {
        return new ChapterScript(
            chapterId,
            "첫 신호를 맞추다",
            "큐 부스 정비",
            "signal",
            activityType,
            List.of(
                new DialoguePage("조력자", "큐 부스부터 안정시켜야 한다."),
                new DialoguePage("스트리머", "첫 숨만 안정적이면 바로 들어갈 수 있어."),
                new DialoguePage("조력자", "좋아. 이제 실제 신호를 맞춘다.")
            )
        );
    }

    private static final class InMemoryChapterStatePort implements LoadChapterStatePort, SaveChapterStatePort {
        private ChapterState chapterState;

        @Override
        public Optional<ChapterState> load() {
            return Optional.ofNullable(chapterState);
        }

        @Override
        public void save(ChapterState chapterState) {
            this.chapterState = chapterState;
        }
    }

    private static final class InMemoryRunProgressPort implements LoadSessionSnapshotPort, SaveSessionSnapshotPort {
        private RunProgress runProgress = RunProgress.initial();

        @Override
        public Optional<RunProgress> load() {
            return Optional.of(runProgress);
        }

        @Override
        public void save(RunProgress runProgress) {
            this.runProgress = runProgress;
        }
    }
}
