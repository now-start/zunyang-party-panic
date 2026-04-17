package org.nowstart.zunyang.partypanic.application.photo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.dto.command.MovePhotoActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.PhotoBayViewResult;
import org.nowstart.zunyang.partypanic.application.port.out.LoadPhotoBayStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SavePhotoBayStatePort;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.photo.PhotoBayState;

class PhotoBayInteractorTest {

    @Test
    void start_initializes_photo_bay_state() {
        InMemoryPhotoBayPort statePort = new InMemoryPhotoBayPort();
        StartPhotoBayInteractor interactor = new StartPhotoBayInteractor(statePort);

        PhotoBayViewResult result = interactor.start();

        assertEquals("포토 베이", result.title());
        assertEquals(3, result.actorX());
        assertEquals(2, result.actorY());
        assertEquals(0, result.lockedRequiredCount());
        assertEquals(5, result.focuses().size());
    }

    @Test
    void inspecting_required_focuses_updates_frame_count() {
        InMemoryPhotoBayPort statePort = new InMemoryPhotoBayPort();
        StartPhotoBayInteractor startInteractor = new StartPhotoBayInteractor(statePort);
        MovePhotoActorInteractor moveInteractor = new MovePhotoActorInteractor(statePort, statePort);
        InspectPhotoFocusInteractor inspectInteractor = new InspectPhotoFocusInteractor(statePort, statePort);

        startInteractor.start();
        moveInteractor.move(new MovePhotoActorCommand(Direction.UP));
        inspectInteractor.inspect();
        moveInteractor.move(new MovePhotoActorCommand(Direction.DOWN));
        moveInteractor.move(new MovePhotoActorCommand(Direction.LEFT));
        moveInteractor.move(new MovePhotoActorCommand(Direction.LEFT));
        PhotoBayViewResult result = inspectInteractor.inspect();

        assertEquals(2, result.lockedRequiredCount());
        assertFalse(result.readyToReturn());
        assertTrue(result.focuses().stream().filter(PhotoFocusView -> PhotoFocusView.locked()).count() >= 2);
    }

    private static final class InMemoryPhotoBayPort
        implements LoadPhotoBayStatePort, SavePhotoBayStatePort {

        private PhotoBayState photoBayState;

        @Override
        public Optional<PhotoBayState> load() {
            return Optional.ofNullable(photoBayState);
        }

        @Override
        public void save(PhotoBayState photoBayState) {
            this.photoBayState = photoBayState;
        }
    }
}
