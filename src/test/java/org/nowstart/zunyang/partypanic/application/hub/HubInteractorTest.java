package org.nowstart.zunyang.partypanic.application.hub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.dto.command.MoveHubActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.HubViewResult;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHubStatePort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.hub.HubHotspot;
import org.nowstart.zunyang.partypanic.domain.hub.HubLayout;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;
import org.nowstart.zunyang.partypanic.domain.session.EndingGrade;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;
import org.nowstart.zunyang.partypanic.domain.session.SessionPhase;

class HubInteractorTest {

    @Test
    void load_initializes_hub_when_no_saved_state_exists() {
        InMemoryHubStatePort statePort = new InMemoryHubStatePort();
        LoadHubLayoutPort layoutPort = () -> sampleLayout();
        LoadSessionSnapshotPort progressPort = () -> Optional.of(RunProgress.initial());

        LoadHubInteractor interactor = new LoadHubInteractor(layoutPort, statePort, statePort, progressPort);

        HubViewResult result = interactor.load();

        assertEquals(4, result.width());
        assertEquals(3, result.height());
        assertEquals(1, result.actorX());
        assertEquals(1, result.actorY());
        assertEquals(Direction.UP.name(), result.facing());
        assertEquals(SessionPhase.PREP_CALL.name(), result.sessionPhase());
        assertEquals(0, result.completedChapterCount());
        assertTrue(result.placeholderArtEnabled());
        assertNull(result.endingGradeTitle());
        assertEquals(1, result.hotspots().size());
        assertTrue(result.hotspots().getFirst().unlocked());
        assertTrue(statePort.load().isPresent());
    }

    @Test
    void load_exposes_saved_ending_grade_title() {
        InMemoryHubStatePort statePort = new InMemoryHubStatePort();
        LoadHubLayoutPort layoutPort = HubInteractorTest::sampleLayout;
        LoadSessionSnapshotPort progressPort = () -> Optional.of(
            RunProgress.initial().withEndingGrade(EndingGrade.WARM_NIGHT)
        );

        LoadHubInteractor interactor = new LoadHubInteractor(layoutPort, statePort, statePort, progressPort);

        HubViewResult result = interactor.load();

        assertEquals(EndingGrade.WARM_NIGHT.title(), result.endingGradeTitle());
    }

    @Test
    void move_changes_position_when_target_tile_is_walkable() {
        InMemoryHubStatePort statePort = new InMemoryHubStatePort();
        LoadHubLayoutPort layoutPort = () -> sampleLayout();
        LoadSessionSnapshotPort progressPort = () -> Optional.of(RunProgress.initial());
        statePort.save(HubState.initial(sampleLayout()));

        MoveHubActorInteractor interactor = new MoveHubActorInteractor(layoutPort, statePort, statePort, progressPort);

        HubViewResult result = interactor.move(new MoveHubActorCommand(Direction.RIGHT));

        assertEquals(2, result.actorX());
        assertEquals(1, result.actorY());
        assertEquals(Direction.RIGHT.name(), result.facing());
        assertNull(result.currentMessage());
        assertNull(result.activeHotspotId());
    }

    @Test
    void move_only_turns_when_target_tile_contains_hotspot() {
        InMemoryHubStatePort statePort = new InMemoryHubStatePort();
        LoadHubLayoutPort layoutPort = () -> sampleLayout();
        LoadSessionSnapshotPort progressPort = () -> Optional.of(RunProgress.initial());
        statePort.save(HubState.initial(sampleLayout()));

        MoveHubActorInteractor interactor = new MoveHubActorInteractor(layoutPort, statePort, statePort, progressPort);

        HubViewResult result = interactor.move(new MoveHubActorCommand(Direction.UP));

        assertEquals(1, result.actorX());
        assertEquals(1, result.actorY());
        assertEquals(Direction.UP.name(), result.facing());
        assertNull(result.currentMessage());
        assertNull(result.activeHotspotId());
    }

    @Test
    void interact_reads_hotspot_from_front_tile() {
        InMemoryHubStatePort statePort = new InMemoryHubStatePort();
        LoadHubLayoutPort layoutPort = () -> sampleLayout();
        LoadSessionSnapshotPort progressPort = () -> Optional.of(RunProgress.initial());
        statePort.save(HubState.initial(sampleLayout()));

        InteractHubInteractor interactor = new InteractHubInteractor(layoutPort, statePort, statePort, progressPort);

        HubViewResult result = interactor.interact();

        assertEquals("SIGNAL", result.activeHotspotId());
        assertTrue(result.currentMessage().contains("첫 신호"));
        assertFalse(result.currentMessage().isBlank());
    }

    @Test
    void interact_returns_locked_message_for_unavailable_hotspot() {
        InMemoryHubStatePort statePort = new InMemoryHubStatePort();
        LoadHubLayoutPort layoutPort = () -> new HubLayout(
            4,
            3,
            new Position(1, 1),
            List.of(new HubHotspot(
                ChapterId.PROPS,
                "소품 회수",
                new Position(1, 2),
                "소품 아카이브로 들어간다.",
                "아직 첫 신호가 정리되지 않았다. 이 문은 나중에 연다."
            ))
        );
        LoadSessionSnapshotPort progressPort = () -> Optional.of(RunProgress.initial());
        statePort.save(HubState.initial(layoutPort.load()));

        InteractHubInteractor interactor = new InteractHubInteractor(layoutPort, statePort, statePort, progressPort);

        HubViewResult result = interactor.interact();

        assertNull(result.activeHotspotId());
        assertTrue(result.currentMessage().contains("아직 첫 신호"));
        assertFalse(result.hotspots().getFirst().unlocked());
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
                "첫 신호 큐를 맞추는 자리다. 여기부터 밤의 리듬이 시작된다.",
                "아직 이 자리는 잠겨 있다."
            ))
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
}
