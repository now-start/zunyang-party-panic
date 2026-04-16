package org.nowstart.zunyang.partypanic.domain.minigame;

public final class CakeBalanceStateMachine {
    public static final float ACTIVE_SECONDS = 18f;
    private static final float SAFE_THRESHOLD = 20f;
    private static final float DANGER_THRESHOLD = 34f;
    private static final float FAIL_THRESHOLD = 100f;
    private static final float NUDGE_AMOUNT = 16f;
    private static final float STABILIZE_MULTIPLIER = 0.68f;

    public enum Phase {
        READY,
        ACTIVE,
        RESULT
    }

    private Phase phase = Phase.READY;
    private float secondsRemaining = ACTIVE_SECONDS;
    private float elapsedActive;
    private float balance;
    private float stability = 100f;
    private boolean inDanger;
    private int recoveryCount;
    private int finalScore;

    public void start() {
        phase = Phase.ACTIVE;
        secondsRemaining = ACTIVE_SECONDS;
        elapsedActive = 0f;
        balance = 0f;
        stability = 100f;
        inDanger = false;
        recoveryCount = 0;
        finalScore = 0;
    }

    public void restart() {
        phase = Phase.READY;
        secondsRemaining = ACTIVE_SECONDS;
        elapsedActive = 0f;
        balance = 0f;
        stability = 100f;
        inDanger = false;
        recoveryCount = 0;
        finalScore = 0;
    }

    public void update(float delta) {
        if (phase != Phase.ACTIVE) {
            return;
        }

        elapsedActive += delta;
        secondsRemaining = Math.max(0f, ACTIVE_SECONDS - elapsedActive);

        float drift = calculateDrift(elapsedActive);
        balance += drift * delta;

        float absoluteBalance = Math.abs(balance);
        if (absoluteBalance > SAFE_THRESHOLD) {
            stability -= (absoluteBalance - SAFE_THRESHOLD) * delta * 0.95f;
        } else {
            stability = Math.min(100f, stability + delta * 3.5f);
        }

        if (absoluteBalance > DANGER_THRESHOLD) {
            inDanger = true;
        } else if (inDanger && absoluteBalance < SAFE_THRESHOLD) {
            inDanger = false;
            recoveryCount += 1;
        }

        balance = clamp(balance, -FAIL_THRESHOLD, FAIL_THRESHOLD);
        stability = clamp(stability, 0f, 100f);

        if (secondsRemaining <= 0f || stability <= 0f) {
            finish();
        }
    }

    public boolean nudgeLeft() {
        if (phase != Phase.ACTIVE) {
            return false;
        }
        balance -= NUDGE_AMOUNT;
        return true;
    }

    public boolean nudgeRight() {
        if (phase != Phase.ACTIVE) {
            return false;
        }
        balance += NUDGE_AMOUNT;
        return true;
    }

    public boolean stabilize() {
        if (phase != Phase.ACTIVE) {
            return false;
        }
        balance *= STABILIZE_MULTIPLIER;
        stability = Math.min(100f, stability + 4f);
        return true;
    }

    public Phase getPhase() {
        return phase;
    }

    public float getSecondsRemaining() {
        return secondsRemaining;
    }

    public float getBalance() {
        return balance;
    }

    public float getStability() {
        return stability;
    }

    public int getRecoveryCount() {
        return recoveryCount;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public boolean isActive() {
        return phase == Phase.ACTIVE;
    }

    public boolean isResult() {
        return phase == Phase.RESULT;
    }

    private void finish() {
        phase = Phase.RESULT;
        finalScore = calculateScore();
    }

    private int calculateScore() {
        int stabilityScore = Math.round(stability);
        int centeredBonus = Math.max(0, 24 - Math.round(Math.abs(balance) / 4f));
        int timeBonus = Math.max(0, Math.round(secondsRemaining * 2f));
        int recoveryBonus = recoveryCount * 6;
        return Math.max(0, stabilityScore + centeredBonus + timeBonus + recoveryBonus);
    }

    private float calculateDrift(float elapsedSeconds) {
        return (float) ((Math.sin(elapsedSeconds * 2.15f) * 25.0) + (Math.cos(elapsedSeconds * 0.85f) * 10.0));
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
