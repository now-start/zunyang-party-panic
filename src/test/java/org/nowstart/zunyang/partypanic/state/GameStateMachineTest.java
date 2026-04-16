package org.nowstart.zunyang.partypanic.state;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.content.GameContent;
import org.nowstart.zunyang.partypanic.model.GameState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStateMachineTest {

    @Test
    void advancesToFirstChoiceAfterPreShow() {
        GameStateMachine stateMachine = new GameStateMachine(GameContent.defaultContent());

        stateMachine.startRound();
        assertEquals(GameState.PRE_SHOW, stateMachine.getState());

        stateMachine.update(GameStateMachine.PRE_SHOW_SECONDS + 0.1f);

        assertEquals(GameState.CHOICE_1_ACTIVE, stateMachine.getState());
        assertEquals("1차 투표", stateMachine.getPhaseTitle());
    }

    @Test
    void streamerPickCanSwingChoiceResult() {
        GameStateMachine stateMachine = advanceToFirstChoice();

        stateMachine.submitChoice("viewer-1", 1);
        stateMachine.submitChoice("viewer-2", 1);
        stateMachine.submitChoice("viewer-3", 0);
        stateMachine.useTodayPick(0);
        stateMachine.update(GameStateMachine.CHOICE_SECONDS + 0.1f);

        assertEquals(GameState.CHOICE_1_RESOLVE, stateMachine.getState());
        assertEquals(0, stateMachine.getWinningOptionIndex());
        assertEquals(0, stateMachine.getResolvedChoiceIndex(0));
        assertNotNull(stateMachine.getResolvedChoiceAction(0));
        assertTrue(stateMachine.getSummaryMessage().contains("선택"));
    }

    @Test
    void emergencyCallCompletesFirstTroubleWindow() {
        GameStateMachine stateMachine = advanceToFirstTrouble();

        stateMachine.registerTroubleResponse("viewer-1");
        stateMachine.registerTroubleResponse("viewer-2");
        stateMachine.triggerEmergencyCall();
        stateMachine.update(GameStateMachine.TROUBLE_SECONDS + 0.1f);

        assertEquals(GameState.TROUBLE_1_RESOLVE, stateMachine.getState());
        assertTrue(stateMachine.getRoundScore() > 0);
    }

    @Test
    void unlocksFinaleTriggerWhenEnoughUniqueParticipantsCheer() {
        GameStateMachine stateMachine = advanceToFinale();

        for (int index = 0; index < GameStateMachine.FINALE_TRIGGER_THRESHOLD; index += 1) {
            stateMachine.submitFinaleCheer("viewer-" + index);
        }

        assertEquals(GameState.FINALE_TRIGGER_READY, stateMachine.getState());
        assertTrue(stateMachine.canTriggerFinale());
        assertEquals(GameStateMachine.FINALE_TRIGGER_THRESHOLD, stateMachine.getFinaleCheerCount());
    }

    private GameStateMachine advanceToFirstChoice() {
        GameStateMachine stateMachine = new GameStateMachine(GameContent.defaultContent());
        stateMachine.startRound();
        stateMachine.update(GameStateMachine.PRE_SHOW_SECONDS + 0.1f);
        return stateMachine;
    }

    private GameStateMachine advanceToFirstTrouble() {
        GameStateMachine stateMachine = advanceToFirstChoice();
        stateMachine.update(GameStateMachine.CHOICE_SECONDS + 0.1f);
        stateMachine.update(GameStateMachine.RESOLVE_SECONDS + 0.1f);
        stateMachine.update(GameStateMachine.CHOICE_SECONDS + 0.1f);
        stateMachine.update(GameStateMachine.RESOLVE_SECONDS + 0.1f);
        return stateMachine;
    }

    private GameStateMachine advanceToFinale() {
        GameStateMachine stateMachine = advanceToFirstTrouble();

        stateMachine.update(GameStateMachine.TROUBLE_SECONDS + 0.1f);
        stateMachine.update(GameStateMachine.RESOLVE_SECONDS + 0.1f);
        stateMachine.update(GameStateMachine.TROUBLE_SECONDS + 0.1f);
        stateMachine.update(GameStateMachine.RESOLVE_SECONDS + 0.1f);
        stateMachine.update(GameStateMachine.TROUBLE_SECONDS + 0.1f);
        stateMachine.update(GameStateMachine.RESOLVE_SECONDS + 0.1f);

        return stateMachine;
    }
}
