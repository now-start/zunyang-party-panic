package org.nowstart.zunyang.partypanic.domain.minigame;

public final class DeskSetupStateMachine {
    public static final float ACTIVE_SECONDS = 45f;

    private static final String[] TASK_TITLES = {
            "마이크 위치",
            "책상 조명",
            "오프닝 메모",
            "화면 무드"
    };
    private static final String[] TASK_HINTS = {
            "방송 시작할 때 목소리가 가장 잘 들어갈 위치로 맞추기",
            "너무 어둡지도, 너무 세지도 않게 무드 잡기",
            "첫 멘트가 꼬이지 않게 메모 순서 정리하기",
            "오늘 방송 첫 공기가 될 다이얼 맞추기"
    };
    private static final float[] DEFAULT_VALUES = {18f, 30f, 86f, 12f};
    private static final float[] TARGET_MIN = {46f, 66f, 28f, 56f};
    private static final float[] TARGET_MAX = {54f, 74f, 36f, 64f};

    private final float[] values = DEFAULT_VALUES.clone();
    private final boolean[] confirmed = new boolean[TASK_TITLES.length];

    private Phase phase = Phase.READY;
    private int selectedIndex;
    private float secondsRemaining = ACTIVE_SECONDS;
    private int adjustmentCount;
    private int finalScore;
    private String feedback = "SPACE를 눌러 책상 정리를 시작하세요.";

    public void update(float delta) {
        if (phase != Phase.ACTIVE) {
            return;
        }

        secondsRemaining = Math.max(0f, secondsRemaining - delta);
        if (allConfirmed() || secondsRemaining <= 0f) {
            finishRound();
        }
    }

    public void start() {
        if (phase == Phase.READY) {
            phase = Phase.ACTIVE;
            feedback = "첫 작업부터 맞춰 보자.";
        }
    }

    public void moveSelectionUp() {
        selectedIndex = (selectedIndex + TASK_TITLES.length - 1) % TASK_TITLES.length;
    }

    public void moveSelectionDown() {
        selectedIndex = (selectedIndex + 1) % TASK_TITLES.length;
    }

    public void adjustSelected(float delta) {
        if (confirmed[selectedIndex]) {
            feedback = TASK_TITLES[selectedIndex] + "은 이미 고정했습니다.";
            return;
        }

        values[selectedIndex] = Math.max(0f, Math.min(100f, values[selectedIndex] + delta));
        adjustmentCount += 1;
        feedback = TASK_TITLES[selectedIndex] + " 수치를 조정했습니다.";
    }

    public void confirmSelected() {
        if (confirmed[selectedIndex]) {
            feedback = TASK_TITLES[selectedIndex] + "은 이미 완료했습니다.";
            return;
        }

        if (!isWithinTarget(selectedIndex)) {
            feedback = TASK_TITLES[selectedIndex] + "이 아직 맞지 않습니다. 민트 구간에 맞춰 주세요.";
            return;
        }

        confirmed[selectedIndex] = true;
        feedback = TASK_TITLES[selectedIndex] + " 고정 완료.";
        if (!allConfirmed()) {
            selectedIndex = findNextIncompleteIndex();
        }
    }

    public void restart() {
        phase = Phase.READY;
        selectedIndex = 0;
        secondsRemaining = ACTIVE_SECONDS;
        adjustmentCount = 0;
        finalScore = 0;
        feedback = "SPACE를 눌러 책상 정리를 다시 시작하세요.";
        System.arraycopy(DEFAULT_VALUES, 0, values, 0, values.length);
        for (int index = 0; index < confirmed.length; index += 1) {
            confirmed[index] = false;
        }
    }

    public Phase phase() {
        return phase;
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public float secondsRemaining() {
        return secondsRemaining;
    }

    public int finalScore() {
        return finalScore;
    }

    public String feedback() {
        return feedback;
    }

    public int taskCount() {
        return TASK_TITLES.length;
    }

    public String taskTitle(int index) {
        return TASK_TITLES[index];
    }

    public String taskHint(int index) {
        return TASK_HINTS[index];
    }

    public float value(int index) {
        return values[index];
    }

    public float targetMin(int index) {
        return TARGET_MIN[index];
    }

    public float targetMax(int index) {
        return TARGET_MAX[index];
    }

    public boolean confirmed(int index) {
        return confirmed[index];
    }

    public boolean isWithinTarget(int index) {
        return values[index] >= TARGET_MIN[index] && values[index] <= TARGET_MAX[index];
    }

    public int confirmedCount() {
        int count = 0;
        for (boolean value : confirmed) {
            if (value) {
                count += 1;
            }
        }
        return count;
    }

    public String phaseDescription() {
        return switch (phase) {
            case READY -> "방송 시작 직전 책상이 아직 덜 맞춰져 있습니다. 마이크, 조명, 메모, 무드를 순서대로 정리하세요.";
            case ACTIVE -> "민트 구간에 맞춘 뒤 SPACE로 고정하면 됩니다. 네 항목을 빠르게 정리할수록 점수가 올라갑니다.";
            case RESULT -> "책상 정리가 끝났습니다. 저장하고 허브로 돌아가면 다음 챕터가 열립니다.";
        };
    }

    public String phaseHint() {
        return switch (phase) {
            case READY -> "SPACE 또는 ENTER로 시작";
            case ACTIVE -> "UP / DOWN 선택, LEFT / RIGHT 조정, SPACE 확정";
            case RESULT -> "H 또는 ENTER 저장, R 재시작, ESC 허브 복귀";
        };
    }

    public String commandHint() {
        return switch (phase) {
            case READY -> "SPACE / ENTER 시작 | ESC 허브 복귀";
            case ACTIVE -> "UP / DOWN 작업 선택 | LEFT / RIGHT 값 조정 | SPACE 확정 | ESC 허브 복귀";
            case RESULT -> "H / ENTER 저장 | R 재시작 | ESC 허브 복귀";
        };
    }

    public String liveHint() {
        return switch (phase) {
            case READY -> "SPACE 시작";
            case ACTIVE -> "UP DOWN 선택  LEFT RIGHT 조정  SPACE 확정";
            case RESULT -> "H 저장  R 재시작  ESC 복귀";
        };
    }

    public boolean isReady() {
        return phase == Phase.READY;
    }

    public boolean isActive() {
        return phase == Phase.ACTIVE;
    }

    public boolean isResult() {
        return phase == Phase.RESULT;
    }

    private int findNextIncompleteIndex() {
        for (int index = 0; index < confirmed.length; index += 1) {
            if (!confirmed[index]) {
                return index;
            }
        }
        return selectedIndex;
    }

    private boolean allConfirmed() {
        for (boolean value : confirmed) {
            if (!value) {
                return false;
            }
        }
        return true;
    }

    private void finishRound() {
        phase = Phase.RESULT;
        finalScore = calculateFinalScore();
        if (allConfirmed()) {
            feedback = "좋아. 적어도 방송은 시작할 수 있겠다.";
        } else {
            feedback = "시간이 다 됐다. 그래도 시작할 정도는 정리됐다.";
        }
    }

    private int calculateFinalScore() {
        int completedCount = 0;
        int accuracyBonus = 0;
        for (int index = 0; index < TASK_TITLES.length; index += 1) {
            if (confirmed[index]) {
                completedCount += 1;
            }
            float center = (TARGET_MIN[index] + TARGET_MAX[index]) * 0.5f;
            accuracyBonus += Math.max(0, 18 - Math.round(Math.abs(values[index] - center)));
        }

        int timeBonus = Math.round(secondsRemaining);
        int efficiencyBonus = Math.max(0, 24 - adjustmentCount);
        return Math.max(0, (completedCount * 24) + accuracyBonus + timeBonus + efficiencyBonus);
    }

    public enum Phase {
        READY,
        ACTIVE,
        RESULT
    }
}
