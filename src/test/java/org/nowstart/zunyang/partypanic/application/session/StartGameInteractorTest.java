package org.nowstart.zunyang.partypanic.application.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;
import org.nowstart.zunyang.partypanic.domain.session.SessionPhase;

class StartGameInteractorTest {

    @Test
    void start_saves_initial_progress_when_snapshot_is_missing() {
        InMemoryRunProgressPort progressPort = new InMemoryRunProgressPort();
        progressPort.runProgress = null;
        StartGameInteractor interactor = new StartGameInteractor(progressPort, progressPort);

        RunProgress result = interactor.start();

        assertEquals(SessionPhase.PREP_CALL, result.phase());
        assertTrue(progressPort.load().isPresent());
    }

    @Test
    void start_keeps_existing_progress_when_snapshot_exists() {
        InMemoryRunProgressPort progressPort = new InMemoryRunProgressPort();
        progressPort.runProgress = RunProgress.initial().markCompleted(ChapterId.SIGNAL);
        StartGameInteractor interactor = new StartGameInteractor(progressPort, progressPort);

        RunProgress result = interactor.start();

        assertTrue(result.isCompleted(ChapterId.SIGNAL));
        assertEquals(SessionPhase.HUB_EXPLORATION, result.phase());
    }

    private static final class InMemoryRunProgressPort
        implements LoadSessionSnapshotPort, SaveSessionSnapshotPort {

        private RunProgress runProgress = RunProgress.initial();

        @Override
        public Optional<RunProgress> load() {
            return Optional.ofNullable(runProgress);
        }

        @Override
        public void save(RunProgress runProgress) {
            this.runProgress = runProgress;
        }
    }
}
