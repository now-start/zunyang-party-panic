package org.nowstart.zunyang.partypanic.application.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.port.out.LoadChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.ResetChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveChapterStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHubStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterActivityType;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterScript;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterState;
import org.nowstart.zunyang.partypanic.domain.chapter.DialoguePage;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.hub.HubHotspot;
import org.nowstart.zunyang.partypanic.domain.hub.HubLayout;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;
import org.nowstart.zunyang.partypanic.domain.session.SessionPhase;

class RestartSessionInteractorTest {

    @Test
    void restart_resets_progress_and_clears_active_chapter() {
        LoadHubLayoutPort layoutPort = RestartSessionInteractorTest::sampleLayout;
        InMemoryHubStatePort hubStatePort = new InMemoryHubStatePort();
        InMemoryChapterStatePort chapterStatePort = new InMemoryChapterStatePort();
        InMemoryRunProgressPort progressPort = new InMemoryRunProgressPort();
        hubStatePort.save(HubState.initial(sampleLayout()).move(Direction.RIGHT));
        chapterStatePort.save(ChapterState.start(sampleScript()));
        progressPort.save(RunProgress.initial().markCompleted(ChapterId.SIGNAL));

        RestartSessionInteractor interactor = new RestartSessionInteractor(
            layoutPort,
            hubStatePort,
            chapterStatePort,
            progressPort
        );

        interactor.restart();

        RunProgress resetProgress = progressPort.runProgress;
        HubState resetHubState = hubStatePort.load().orElseThrow();

        assertEquals(SessionPhase.PREP_CALL, resetProgress.phase());
        assertTrue(resetProgress.completedChapters().isEmpty());
        assertEquals(1, resetHubState.actor().position().x());
        assertEquals(1, resetHubState.actor().position().y());
        assertFalse(chapterStatePort.load().isPresent());
    }

    private static HubLayout sampleLayout() {
        return new HubLayout(
            4,
            3,
            new Position(1, 1),
            List.of(new HubHotspot(
                ChapterId.SIGNAL,
                "첫 신호",
                new Position(1, 2),
                "첫 신호 큐를 맞추는 자리다.",
                "아직 이 자리는 잠겨 있다."
            ))
        );
    }

    private static ChapterScript sampleScript() {
        return new ChapterScript(
            ChapterId.SIGNAL,
            "첫 신호를 맞추다",
            "큐 부스 정비",
            "signal",
            ChapterActivityType.SIGNAL_CONSOLE,
            List.of(new DialoguePage("조력자", "큐를 점검한다."))
        );
    }

    private static final class InMemoryHubStatePort implements LoadHubStatePort, SaveHubStatePort {
        private HubState hubState;

        @Override
        public Optional<HubState> load() {
            return Optional.ofNullable(hubState);
        }

        @Override
        public void save(HubState hubState) {
            this.hubState = hubState;
        }
    }

    private static final class InMemoryChapterStatePort
        implements LoadChapterStatePort, SaveChapterStatePort, ResetChapterStatePort {

        private ChapterState chapterState;

        @Override
        public Optional<ChapterState> load() {
            return Optional.ofNullable(chapterState);
        }

        @Override
        public void save(ChapterState chapterState) {
            this.chapterState = chapterState;
        }

        @Override
        public void reset() {
            this.chapterState = null;
        }
    }

    private static final class InMemoryRunProgressPort implements SaveSessionSnapshotPort {
        private RunProgress runProgress = RunProgress.initial();

        @Override
        public void save(RunProgress runProgress) {
            this.runProgress = runProgress;
        }
    }
}
