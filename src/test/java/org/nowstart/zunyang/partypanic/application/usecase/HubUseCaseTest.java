package org.nowstart.zunyang.partypanic.application.usecase;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.adapter.out.state.InMemoryGameStateAdapter;
import org.nowstart.zunyang.partypanic.application.dto.AdvanceDialogueResult;
import org.nowstart.zunyang.partypanic.application.dto.InteractResult;
import org.nowstart.zunyang.partypanic.application.dto.MovePlayerCommand;
import org.nowstart.zunyang.partypanic.application.dto.MovePlayerResult;
import org.nowstart.zunyang.partypanic.application.service.EventResolver;
import org.nowstart.zunyang.partypanic.application.service.MovementPolicy;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.event.DialogueEvent;
import org.nowstart.zunyang.partypanic.domain.event.EventVisual;
import org.nowstart.zunyang.partypanic.domain.model.Dialogue;
import org.nowstart.zunyang.partypanic.domain.model.Direction;
import org.nowstart.zunyang.partypanic.domain.model.GameMap;
import org.nowstart.zunyang.partypanic.domain.model.GameState;
import org.nowstart.zunyang.partypanic.domain.model.Player;
import org.nowstart.zunyang.partypanic.domain.model.Position;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HubUseCaseTest {

    @Test
    void movePlayerMovesOnWalkableTileAndUpdatesFacing() {
        InMemoryGameStateAdapter statePort = new InMemoryGameStateAdapter(baseState());
        MovePlayerInteractor interactor = new MovePlayerInteractor(statePort, new MovementPolicy());

        MovePlayerResult result = interactor.move(new MovePlayerCommand(Direction.RIGHT));

        assertTrue(result.moved());
        assertEquals(new Position(2, 1), result.state().player().position());
        assertEquals(Direction.RIGHT, result.state().player().facing());
    }

    @Test
    void movePlayerStopsAtWallButStillFacesDirection() {
        InMemoryGameStateAdapter statePort = new InMemoryGameStateAdapter(baseState());
        MovePlayerInteractor interactor = new MovePlayerInteractor(statePort, new MovementPolicy());

        MovePlayerResult result = interactor.move(new MovePlayerCommand(Direction.UP));

        assertFalse(result.moved());
        assertEquals(new Position(1, 1), result.state().player().position());
        assertEquals(Direction.UP, result.state().player().facing());
    }

    @Test
    void interactOpensLockedDialogueWhenEventIsStillBlocked() {
        DialogueEvent storageRoom = new DialogueEvent(
                ActivityId.STORAGE_ROOM,
                "장식 창고",
                Dialogue.singleSpeaker("치즈냥", List.of("먼저 방송 책상을 정리해야 한다.")),
                Dialogue.singleSpeaker("치즈냥", List.of("창고 문이 열렸다.")),
                EventVisual.DOOR,
                new Position(2, 1)
        );
        InMemoryGameStateAdapter statePort = new InMemoryGameStateAdapter(stateWithEvent(storageRoom));
        InteractInteractor interactor = new InteractInteractor(statePort, new EventResolver(), new GameProgress());

        InteractResult result = interactor.interact();

        assertTrue(result.startedDialogue());
        assertFalse(result.unlocked());
        assertNull(result.pendingActivityId());
        assertEquals("먼저 방송 책상을 정리해야 한다.", result.state().activeDialogue().currentLine().text());
    }

    @Test
    void interactStartsEventDialogueAndQueuesActivityWhenUnlocked() {
        DialogueEvent deskEvent = new DialogueEvent(
                ActivityId.BROADCAST_DESK,
                "방송 책상",
                Dialogue.singleSpeaker("치즈냥", List.of("잠겨 있다.")),
                Dialogue.singleSpeaker("치즈냥", List.of("책상 앞이다.", "오늘 방송 첫 화면부터 맞춰 보자.")),
                EventVisual.DESK,
                new Position(2, 1)
        );
        InMemoryGameStateAdapter statePort = new InMemoryGameStateAdapter(stateWithEvent(deskEvent));
        InteractInteractor interactor = new InteractInteractor(statePort, new EventResolver(), new GameProgress());

        InteractResult result = interactor.interact();

        assertTrue(result.startedDialogue());
        assertTrue(result.unlocked());
        assertEquals(ActivityId.BROADCAST_DESK, result.pendingActivityId());
        assertEquals("책상 앞이다.", result.state().activeDialogue().currentLine().text());
    }

    @Test
    void advanceDialogueMovesToNextLineBeforeCompleting() {
        Dialogue dialogue = Dialogue.singleSpeaker("치즈냥", List.of("첫 줄", "둘째 줄"));
        GameState state = baseState().startDialogue(dialogue, ActivityId.BROADCAST_DESK);
        InMemoryGameStateAdapter statePort = new InMemoryGameStateAdapter(state);
        AdvanceDialogueInteractor interactor = new AdvanceDialogueInteractor(statePort);

        AdvanceDialogueResult result = interactor.advance();

        assertTrue(result.hasActiveDialogue());
        assertNull(result.completedActivityId());
        assertEquals("둘째 줄", result.state().activeDialogue().currentLine().text());
    }

    @Test
    void advanceDialogueClearsStateAndReturnsPendingActivityAtTheEnd() {
        Dialogue dialogue = Dialogue.singleSpeaker("치즈냥", List.of("마지막 줄"));
        GameState state = baseState().startDialogue(dialogue, ActivityId.BROADCAST_DESK);
        InMemoryGameStateAdapter statePort = new InMemoryGameStateAdapter(state);
        AdvanceDialogueInteractor interactor = new AdvanceDialogueInteractor(statePort);

        AdvanceDialogueResult result = interactor.advance();

        assertFalse(result.hasActiveDialogue());
        assertEquals(ActivityId.BROADCAST_DESK, result.completedActivityId());
        assertNull(result.state().activeDialogue());
        assertNull(result.state().pendingActivityId());
    }

    private GameState baseState() {
        return new GameState(
                new GameMap(
                        List.of(
                                "#####",
                                "#...#",
                                "#####"
                        ),
                        List.of(),
                        new Position(1, 1)
                ),
                new Player(new Position(1, 1), Direction.RIGHT),
                null,
                null
        );
    }

    private GameState stateWithEvent(DialogueEvent event) {
        return new GameState(
                new GameMap(
                        List.of(
                                "#####",
                                "#...#",
                                "#####"
                        ),
                        List.of(event),
                        new Position(1, 1)
                ),
                new Player(new Position(1, 1), Direction.RIGHT),
                null,
                null
        );
    }
}
