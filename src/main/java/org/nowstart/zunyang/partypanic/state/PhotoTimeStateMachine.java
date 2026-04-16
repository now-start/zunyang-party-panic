package org.nowstart.zunyang.partypanic.state;

public final class PhotoTimeStateMachine {
    public static final float ACTIVE_SECONDS = 18f;
    public static final int TOTAL_SHOTS = 3;
    private static final float FRAME_STEP = 0.16f;
    private static final float FRAME_LIMIT = 1.15f;

    private static final float[] ANCHOR_X = {-0.32f, 0.12f, 0.34f};
    private static final float[] ANCHOR_Y = {0.14f, -0.08f, 0.26f};

    public enum Phase {
        READY,
        ACTIVE,
        RESULT
    }

    private Phase phase = Phase.READY;
    private float secondsRemaining = ACTIVE_SECONDS;
    private float shotElapsed;
    private float totalElapsed;
    private int capturedShots;
    private int totalScore;
    private float frameX;
    private float frameY;
    private float targetX;
    private float targetY;
    private String lastJudgement = "준비";
    private int lastShotScore;

    public void start() {
        phase = Phase.ACTIVE;
        secondsRemaining = ACTIVE_SECONDS;
        shotElapsed = 0f;
        totalElapsed = 0f;
        capturedShots = 0;
        totalScore = 0;
        frameX = 0f;
        frameY = 0f;
        lastJudgement = "촬영 시작";
        lastShotScore = 0;
        updateTarget();
    }

    public void restart() {
        phase = Phase.READY;
        secondsRemaining = ACTIVE_SECONDS;
        shotElapsed = 0f;
        totalElapsed = 0f;
        capturedShots = 0;
        totalScore = 0;
        frameX = 0f;
        frameY = 0f;
        targetX = 0f;
        targetY = 0f;
        lastJudgement = "준비";
        lastShotScore = 0;
    }

    public void update(float delta) {
        if (phase != Phase.ACTIVE) {
            return;
        }

        totalElapsed += delta;
        shotElapsed += delta;
        secondsRemaining = Math.max(0f, ACTIVE_SECONDS - totalElapsed);
        updateTarget();

        if (secondsRemaining <= 0f || capturedShots >= TOTAL_SHOTS) {
            finish();
        }
    }

    public boolean moveLeft() {
        return move(-FRAME_STEP, 0f);
    }

    public boolean moveRight() {
        return move(FRAME_STEP, 0f);
    }

    public boolean moveUp() {
        return move(0f, FRAME_STEP);
    }

    public boolean moveDown() {
        return move(0f, -FRAME_STEP);
    }

    public boolean capture() {
        if (phase != Phase.ACTIVE) {
            return false;
        }

        float distance = distance(frameX, frameY, targetX, targetY);
        float rawScore = Math.max(0f, 120f - (distance * 105f));
        lastShotScore = Math.round(rawScore);
        totalScore += lastShotScore;
        lastJudgement = judgementFor(distance);
        capturedShots += 1;

        if (capturedShots >= TOTAL_SHOTS) {
            finish();
            return true;
        }

        shotElapsed = 0f;
        frameX *= 0.25f;
        frameY *= 0.25f;
        updateTarget();
        return true;
    }

    public Phase getPhase() {
        return phase;
    }

    public float getSecondsRemaining() {
        return secondsRemaining;
    }

    public int getCapturedShots() {
        return capturedShots;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public float getFrameX() {
        return frameX;
    }

    public float getFrameY() {
        return frameY;
    }

    public float getTargetX() {
        return targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    public String getLastJudgement() {
        return lastJudgement;
    }

    public int getLastShotScore() {
        return lastShotScore;
    }

    public int getFinalScore() {
        if (phase != Phase.RESULT) {
            return totalScore;
        }
        return totalScore + Math.max(0, Math.round(secondsRemaining * 4f));
    }

    public boolean isActive() {
        return phase == Phase.ACTIVE;
    }

    public boolean isResult() {
        return phase == Phase.RESULT;
    }

    private boolean move(float deltaX, float deltaY) {
        if (phase != Phase.ACTIVE) {
            return false;
        }

        frameX = clamp(frameX + deltaX, -FRAME_LIMIT, FRAME_LIMIT);
        frameY = clamp(frameY + deltaY, -FRAME_LIMIT, FRAME_LIMIT);
        return true;
    }

    private void finish() {
        phase = Phase.RESULT;
    }

    private void updateTarget() {
        if (capturedShots >= TOTAL_SHOTS) {
            return;
        }

        float anchorX = ANCHOR_X[capturedShots];
        float anchorY = ANCHOR_Y[capturedShots];
        targetX = clamp(anchorX + ((float) Math.sin(shotElapsed * 2.4f) * 0.16f), -FRAME_LIMIT, FRAME_LIMIT);
        targetY = clamp(anchorY + ((float) Math.cos(shotElapsed * 1.8f) * 0.14f), -FRAME_LIMIT, FRAME_LIMIT);
    }

    private String judgementFor(float distance) {
        if (distance <= 0.18f) {
            return "완벽";
        }
        if (distance <= 0.36f) {
            return "좋음";
        }
        if (distance <= 0.55f) {
            return "아슬아슬";
        }
        return "흔들림";
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt((dx * dx) + (dy * dy));
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
