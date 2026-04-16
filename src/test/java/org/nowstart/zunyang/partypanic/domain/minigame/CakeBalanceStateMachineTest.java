package org.nowstart.zunyang.partypanic.domain.minigame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CakeBalanceStateMachineTest {

    @Test
    void entersActivePhaseWhenStarted() {
        CakeBalanceStateMachine stateMachine = new CakeBalanceStateMachine();

        stateMachine.start();

        assertEquals(CakeBalanceStateMachine.Phase.ACTIVE, stateMachine.getPhase());
        assertEquals(CakeBalanceStateMachine.ACTIVE_SECONDS, stateMachine.getSecondsRemaining());
        assertEquals(100f, stateMachine.getStability());
    }

    @Test
    void directionalNudgesMoveBalance() {
        CakeBalanceStateMachine stateMachine = new CakeBalanceStateMachine();
        stateMachine.start();

        stateMachine.nudgeRight();
        float afterRight = stateMachine.getBalance();
        stateMachine.nudgeLeft();

        assertTrue(afterRight > 0f);
        assertTrue(stateMachine.getBalance() < afterRight);
    }

    @Test
    void activeRunEventuallyFinishesWithScore() {
        CakeBalanceStateMachine stateMachine = new CakeBalanceStateMachine();
        stateMachine.start();

        for (int index = 0; index < 240 && !stateMachine.isResult(); index += 1) {
            stateMachine.update(0.1f);
            if (index % 8 == 0) {
                stateMachine.stabilize();
            }
        }

        assertTrue(stateMachine.isResult());
        assertTrue(stateMachine.getFinalScore() >= 0);
    }
}
