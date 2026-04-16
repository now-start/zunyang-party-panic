package org.nowstart.zunyang.partypanic.state;

import org.nowstart.zunyang.partypanic.content.GameContent;
import org.nowstart.zunyang.partypanic.model.ChoiceSet;
import org.nowstart.zunyang.partypanic.model.GameState;
import org.nowstart.zunyang.partypanic.model.PartyAction;
import org.nowstart.zunyang.partypanic.model.TroubleEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GameStateMachine {
    public static final float PRE_SHOW_SECONDS = 5f;
    public static final float CHOICE_SECONDS = 18f;
    public static final float RESOLVE_SECONDS = 1.5f;
    public static final float TROUBLE_SECONDS = 6f;
    public static final float FINALE_SECONDS = 20f;
    public static final int STREAMER_PICK_BOOST = 3;
    public static final int EMERGENCY_CALL_BOOST = 2;
    public static final int FINALE_TRIGGER_THRESHOLD = 8;

    private final GameContent content;
    private final Map<String, Integer> currentVotesByViewer = new LinkedHashMap<>();
    private final Set<String> uniqueRoundParticipants = new LinkedHashSet<>();
    private final Set<String> currentTroubleResponders = new LinkedHashSet<>();
    private final Set<String> finaleCheerers = new LinkedHashSet<>();

    private GameState state = GameState.IDLE;
    private float stateElapsed;
    private int currentChoiceIndex = -1;
    private int currentTroubleIndex = -1;
    private List<Integer> currentVoteCounts = List.of();
    private String phaseTitle = "대기";
    private String phaseMessage = "파티 시작 버튼을 누르면 치즈냥 생일 방송 라운드가 시작됩니다.";
    private String summaryMessage = "시작 대기";
    private int troubleProgress;
    private int winningOptionIndex = -1;
    private int roundScore;
    private boolean todayPickUsed;
    private boolean emergencyCallUsed;
    private boolean finaleTriggered;

    public GameStateMachine(GameContent content) {
        this.content = content;
    }

    public void startRound() {
        if (state != GameState.IDLE && state != GameState.ROUND_COMPLETE) {
            return;
        }

        resetRound();
        phaseTitle = "오프닝";
        phaseMessage = "치즈냥의 생일 방송 준비가 시작됩니다.";
        summaryMessage = "카운트다운 진행 중";
        changeState(GameState.PRE_SHOW);
    }

    public void update(float deltaSeconds) {
        if (state == GameState.IDLE || state == GameState.ROUND_COMPLETE) {
            return;
        }

        stateElapsed += deltaSeconds;

        switch (state) {
            case PRE_SHOW -> advanceWhenReady(GameState.CHOICE_1_ACTIVE, PRE_SHOW_SECONDS, () -> enterChoice(0));
            case CHOICE_1_ACTIVE, CHOICE_2_ACTIVE -> advanceWhenReady(resolveChoiceState(), CHOICE_SECONDS, this::resolveChoice);
            case CHOICE_1_RESOLVE -> advanceWhenReady(GameState.CHOICE_2_ACTIVE, RESOLVE_SECONDS, () -> enterChoice(1));
            case CHOICE_2_RESOLVE -> advanceWhenReady(GameState.TROUBLE_1_ACTIVE, RESOLVE_SECONDS, () -> enterTrouble(0));
            case TROUBLE_1_ACTIVE, TROUBLE_2_ACTIVE, TROUBLE_3_ACTIVE -> advanceWhenReady(resolveTroubleState(), TROUBLE_SECONDS, this::resolveTrouble);
            case TROUBLE_1_RESOLVE -> advanceWhenReady(GameState.TROUBLE_2_ACTIVE, RESOLVE_SECONDS, () -> enterTrouble(1));
            case TROUBLE_2_RESOLVE -> advanceWhenReady(GameState.TROUBLE_3_ACTIVE, RESOLVE_SECONDS, () -> enterTrouble(2));
            case TROUBLE_3_RESOLVE -> advanceWhenReady(GameState.FINALE_ACTIVE, RESOLVE_SECONDS, this::enterFinale);
            case FINALE_ACTIVE, FINALE_TRIGGER_READY -> advanceWhenReady(GameState.FINALE_RESOLVE, FINALE_SECONDS, this::resolveFinaleByTimeout);
            case FINALE_RESOLVE -> advanceWhenReady(GameState.ROUND_COMPLETE, RESOLVE_SECONDS, this::finishRound);
            default -> {
            }
        }
    }

    public boolean submitChoice(String viewerId, int optionIndex) {
        if (!isChoiceInputOpen() || optionIndex < 0 || optionIndex >= currentVoteCounts.size()) {
            return false;
        }

        Integer previousIndex = currentVotesByViewer.put(viewerId, optionIndex);
        if (previousIndex != null && previousIndex == optionIndex) {
            return false;
        }

        if (previousIndex != null) {
            currentVoteCounts.set(previousIndex, currentVoteCounts.get(previousIndex) - 1);
        }

        currentVoteCounts.set(optionIndex, currentVoteCounts.get(optionIndex) + 1);
        uniqueRoundParticipants.add(viewerId);
        summaryMessage = "채팅이 " + getCurrentChoiceSet().actions().get(optionIndex).title() + " 쪽으로 기울고 있습니다.";
        return true;
    }

    public boolean useTodayPick(int optionIndex) {
        if (!canUseTodayPick() || optionIndex < 0 || optionIndex >= currentVoteCounts.size()) {
            return false;
        }

        currentVoteCounts.set(optionIndex, currentVoteCounts.get(optionIndex) + STREAMER_PICK_BOOST);
        todayPickUsed = true;
        summaryMessage = "치즈냥의 오늘의 픽이 " + getCurrentChoiceSet().actions().get(optionIndex).title() + " 에 적용되었습니다.";
        return true;
    }

    public boolean registerTroubleResponse(String viewerId) {
        if (!isTroubleInputOpen() || !currentTroubleResponders.add(viewerId)) {
            return false;
        }

        uniqueRoundParticipants.add(viewerId);
        troubleProgress += 1;
        summaryMessage = "시청자 대응이 쌓이는 중입니다.";
        return true;
    }

    public boolean triggerEmergencyCall() {
        if (!canTriggerEmergencyCall()) {
            return false;
        }

        troubleProgress += EMERGENCY_CALL_BOOST;
        emergencyCallUsed = true;
        summaryMessage = "치즈냥이 긴급 정리 콜을 외쳤습니다.";
        return true;
    }

    public boolean submitFinaleCheer(String viewerId) {
        if (!isFinaleInputOpen() || !finaleCheerers.add(viewerId)) {
            return false;
        }

        uniqueRoundParticipants.add(viewerId);

        if (state == GameState.FINALE_ACTIVE && finaleCheerers.size() >= FINALE_TRIGGER_THRESHOLD) {
            phaseTitle = "피날레 준비 완료";
            phaseMessage = "응원은 충분합니다. 이제 스트리머가 생일 소원을 발동하면 됩니다.";
            summaryMessage = "생일 소원 버튼이 열렸습니다.";
            changeState(GameState.FINALE_TRIGGER_READY, stateElapsed);
            return true;
        }

        summaryMessage = "응원 수가 " + finaleCheerers.size() + "명까지 모였습니다.";
        return true;
    }

    public boolean triggerFinale() {
        if (!canTriggerFinale()) {
            return false;
        }

        finaleTriggered = true;
        applyScore(10);
        phaseTitle = "피날레 발동";
        phaseMessage = "치즈냥이 생일 소원을 직접 발동합니다.";
        summaryMessage = "케이크 초가 점화되며 파티가 대성공 분위기로 전환됩니다.";
        changeState(GameState.FINALE_RESOLVE);
        return true;
    }

    public GameState getState() {
        return state;
    }

    public String getPhaseTitle() {
        return phaseTitle;
    }

    public String getPhaseMessage() {
        return phaseMessage;
    }

    public String getSummaryMessage() {
        return summaryMessage;
    }

    public float getSecondsRemaining() {
        float duration = durationFor(state);
        if (duration <= 0f) {
            return 0f;
        }
        return Math.max(0f, duration - stateElapsed);
    }

    public int getCurrentChoiceIndex() {
        return currentChoiceIndex;
    }

    public int getCurrentTroubleIndex() {
        return currentTroubleIndex;
    }

    public ChoiceSet getCurrentChoiceSet() {
        if (currentChoiceIndex < 0 || currentChoiceIndex >= content.choiceSets().size()) {
            return null;
        }
        return content.choiceSets().get(currentChoiceIndex);
    }

    public TroubleEvent getCurrentTroubleEvent() {
        if (currentTroubleIndex < 0 || currentTroubleIndex >= content.troubleEvents().size()) {
            return null;
        }
        return content.troubleEvents().get(currentTroubleIndex);
    }

    public List<Integer> getVoteCounts() {
        return List.copyOf(currentVoteCounts);
    }

    public int getLeadingOptionIndex() {
        if (currentVoteCounts.isEmpty()) {
            return -1;
        }

        int bestIndex = 0;
        int bestScore = currentVoteCounts.get(0);
        for (int index = 1; index < currentVoteCounts.size(); index += 1) {
            int currentScore = currentVoteCounts.get(index);
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestIndex = index;
            }
        }
        return bestIndex;
    }

    public int getWinningOptionIndex() {
        return winningOptionIndex;
    }

    public int getUniqueParticipantCount() {
        return uniqueRoundParticipants.size();
    }

    public int getTroubleProgress() {
        return troubleProgress;
    }

    public int getCurrentTroubleRequiredResponses() {
        TroubleEvent troubleEvent = getCurrentTroubleEvent();
        return troubleEvent == null ? 0 : troubleEvent.requiredResponses();
    }

    public int getFinaleCheerCount() {
        return finaleCheerers.size();
    }

    public int getFinaleTriggerThreshold() {
        return FINALE_TRIGGER_THRESHOLD;
    }

    public int getRoundScore() {
        return roundScore;
    }

    public boolean isChoicePhase() {
        return switch (state) {
            case CHOICE_1_ACTIVE, CHOICE_1_RESOLVE, CHOICE_2_ACTIVE, CHOICE_2_RESOLVE -> true;
            default -> false;
        };
    }

    public boolean isChoiceInputOpen() {
        return state == GameState.CHOICE_1_ACTIVE || state == GameState.CHOICE_2_ACTIVE;
    }

    public boolean isTroublePhase() {
        return switch (state) {
            case TROUBLE_1_ACTIVE, TROUBLE_1_RESOLVE, TROUBLE_2_ACTIVE, TROUBLE_2_RESOLVE, TROUBLE_3_ACTIVE, TROUBLE_3_RESOLVE -> true;
            default -> false;
        };
    }

    public boolean isTroubleInputOpen() {
        return state == GameState.TROUBLE_1_ACTIVE || state == GameState.TROUBLE_2_ACTIVE || state == GameState.TROUBLE_3_ACTIVE;
    }

    public boolean isFinaleInputOpen() {
        return state == GameState.FINALE_ACTIVE || state == GameState.FINALE_TRIGGER_READY;
    }

    public boolean canUseTodayPick() {
        return isChoiceInputOpen() && !todayPickUsed;
    }

    public boolean canTriggerEmergencyCall() {
        return isTroubleInputOpen() && !emergencyCallUsed;
    }

    public boolean canTriggerFinale() {
        return state == GameState.FINALE_TRIGGER_READY;
    }

    public boolean isInteractiveWindow() {
        return isChoiceInputOpen() || isTroubleInputOpen() || isFinaleInputOpen();
    }

    private void resetRound() {
        currentVotesByViewer.clear();
        uniqueRoundParticipants.clear();
        currentTroubleResponders.clear();
        finaleCheerers.clear();
        currentVoteCounts = List.of();
        currentChoiceIndex = -1;
        currentTroubleIndex = -1;
        troubleProgress = 0;
        winningOptionIndex = -1;
        roundScore = 0;
        todayPickUsed = false;
        emergencyCallUsed = false;
        finaleTriggered = false;
    }

    private void advanceWhenReady(GameState nextState, float duration, Runnable transition) {
        if (stateElapsed < duration) {
            return;
        }
        transition.run();
        if (state != GameState.ROUND_COMPLETE && state != nextState) {
            changeState(nextState);
        }
    }

    private void enterChoice(int choiceIndex) {
        ChoiceSet choiceSet = content.choiceSets().get(choiceIndex);
        currentChoiceIndex = choiceIndex;
        currentVotesByViewer.clear();
        currentVoteCounts = new ArrayList<>(Collections.nCopies(choiceSet.actions().size(), 0));
        winningOptionIndex = -1;
        todayPickUsed = false;
        phaseTitle = choiceSet.roundLabel();
        phaseMessage = choiceSet.prompt();
        summaryMessage = "채팅 투표를 모아 주세요.";
        changeState(choiceIndex == 0 ? GameState.CHOICE_1_ACTIVE : GameState.CHOICE_2_ACTIVE);
    }

    private void resolveChoice() {
        ChoiceSet choiceSet = getCurrentChoiceSet();
        winningOptionIndex = getLeadingOptionIndex();
        PartyAction winner = choiceSet.actions().get(winningOptionIndex);

        applyScore(Math.max(2, currentVoteCounts.get(winningOptionIndex) + 1));
        phaseTitle = "선택 확정";
        phaseMessage = winner.description();
        summaryMessage = winner.title() + " 선택. " + choiceSet.resolutionText();
        changeState(resolveChoiceState());
    }

    private void enterTrouble(int troubleIndex) {
        TroubleEvent troubleEvent = content.troubleEvents().get(troubleIndex);
        currentTroubleIndex = troubleIndex;
        currentTroubleResponders.clear();
        troubleProgress = 0;
        emergencyCallUsed = false;
        phaseTitle = troubleEvent.title();
        phaseMessage = troubleEvent.instruction();
        summaryMessage = "대응을 모으는 중입니다.";
        changeState(activeTroubleState());
    }

    private void resolveTrouble() {
        TroubleEvent troubleEvent = getCurrentTroubleEvent();
        boolean success = troubleProgress >= troubleEvent.requiredResponses();
        applyScore(success ? 6 : -2);

        phaseTitle = success ? "문제 수습 성공" : "문제 수습 실패";
        phaseMessage = troubleEvent.title();
        summaryMessage = success ? troubleEvent.successText() : troubleEvent.failureText();
        changeState(resolveTroubleState());
    }

    private void enterFinale() {
        finaleCheerers.clear();
        finaleTriggered = false;
        phaseTitle = "피날레 응원";
        phaseMessage = "응원을 8명 이상 모으면 스트리머가 생일 소원을 직접 발동할 수 있습니다.";
        summaryMessage = "응원 채팅을 모아 주세요.";
        changeState(GameState.FINALE_ACTIVE);
    }

    private void resolveFinaleByTimeout() {
        if (finaleTriggered) {
            return;
        }

        phaseTitle = "피날레 종료";
        phaseMessage = "생일 라운드가 마무리됩니다.";

        if (finaleCheerers.size() >= FINALE_TRIGGER_THRESHOLD) {
            applyScore(4);
            summaryMessage = "응원은 충분했지만 생일 소원 발동 타이밍을 놓쳐 일반 엔딩으로 종료되었습니다.";
        } else {
            applyScore(-1);
            summaryMessage = "응원이 부족해 조용한 엔딩으로 종료되었습니다.";
        }

        changeState(GameState.FINALE_RESOLVE);
    }

    private void finishRound() {
        phaseTitle = "라운드 종료";
        phaseMessage = "다음 생일 연출을 다시 시작할 수 있습니다.";
        changeState(GameState.ROUND_COMPLETE);
    }

    private void changeState(GameState nextState) {
        changeState(nextState, 0f);
    }

    private void changeState(GameState nextState, float carryElapsed) {
        state = nextState;
        stateElapsed = carryElapsed;
    }

    private void applyScore(int delta) {
        roundScore = Math.max(0, roundScore + delta);
    }

    private float durationFor(GameState gameState) {
        return switch (gameState) {
            case PRE_SHOW -> PRE_SHOW_SECONDS;
            case CHOICE_1_ACTIVE, CHOICE_2_ACTIVE -> CHOICE_SECONDS;
            case CHOICE_1_RESOLVE, CHOICE_2_RESOLVE, TROUBLE_1_RESOLVE, TROUBLE_2_RESOLVE, TROUBLE_3_RESOLVE, FINALE_RESOLVE -> RESOLVE_SECONDS;
            case TROUBLE_1_ACTIVE, TROUBLE_2_ACTIVE, TROUBLE_3_ACTIVE -> TROUBLE_SECONDS;
            case FINALE_ACTIVE, FINALE_TRIGGER_READY -> FINALE_SECONDS;
            default -> 0f;
        };
    }

    private GameState resolveChoiceState() {
        return currentChoiceIndex == 0 ? GameState.CHOICE_1_RESOLVE : GameState.CHOICE_2_RESOLVE;
    }

    private GameState activeTroubleState() {
        return switch (currentTroubleIndex) {
            case 0 -> GameState.TROUBLE_1_ACTIVE;
            case 1 -> GameState.TROUBLE_2_ACTIVE;
            case 2 -> GameState.TROUBLE_3_ACTIVE;
            default -> throw new IllegalStateException("Unknown trouble index: " + currentTroubleIndex);
        };
    }

    private GameState resolveTroubleState() {
        return switch (currentTroubleIndex) {
            case 0 -> GameState.TROUBLE_1_RESOLVE;
            case 1 -> GameState.TROUBLE_2_RESOLVE;
            case 2 -> GameState.TROUBLE_3_RESOLVE;
            default -> throw new IllegalStateException("Unknown trouble index: " + currentTroubleIndex);
        };
    }
}
