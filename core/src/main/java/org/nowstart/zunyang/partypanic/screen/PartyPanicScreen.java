package org.nowstart.zunyang.partypanic.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.content.GameContent;
import org.nowstart.zunyang.partypanic.model.ChoiceSet;
import org.nowstart.zunyang.partypanic.model.GameState;
import org.nowstart.zunyang.partypanic.model.PartyAction;
import org.nowstart.zunyang.partypanic.model.TroubleEvent;
import org.nowstart.zunyang.partypanic.state.GameStateMachine;

import java.util.List;
import java.util.Locale;

public final class PartyPanicScreen extends ScreenAdapter {
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final GameStateMachine stateMachine = new GameStateMachine(GameContent.defaultContent());

    private int syntheticViewerSequence = 1;

    public PartyPanicScreen() {
        font.getData().setScale(1.1f);
        font.setColor(Color.valueOf("F4EAD5"));
    }

    @Override
    public void render(float delta) {
        handleInput();
        stateMachine.update(delta);

        ScreenUtils.clear(0.11f, 0.09f, 0.09f, 1f);

        batch.begin();
        drawHeader();
        drawPhaseInfo();
        drawContentArea();
        drawControls();
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            stateMachine.startRound();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && stateMachine.getState() == GameState.ROUND_COMPLETE) {
            stateMachine.startRound();
        }

        if (stateMachine.isChoiceInputOpen()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                stateMachine.submitChoice(nextSyntheticViewerId(), 0);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                stateMachine.submitChoice(nextSyntheticViewerId(), 1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                stateMachine.submitChoice(nextSyntheticViewerId(), 2);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                int targetIndex = Math.max(0, stateMachine.getLeadingOptionIndex());
                stateMachine.useTodayPick(targetIndex);
            }
        }

        if (stateMachine.isTroubleInputOpen()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
                stateMachine.registerTroubleResponse(nextSyntheticViewerId());
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                stateMachine.triggerEmergencyCall();
            }
        }

        if (stateMachine.isFinaleInputOpen()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
                stateMachine.submitFinaleCheer(nextSyntheticViewerId());
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                stateMachine.triggerFinale();
            }
        }
    }

    private void drawHeader() {
        drawLine("zunyang-party-panic", 48f, 860f, 1.4f);
        drawLine("Windows libGDX prototype baseline for ZuNyang birthday fan game", 48f, 828f, 0.95f);
    }

    private void drawPhaseInfo() {
        String phase = "현재 단계: " + stateMachine.getPhaseTitle();
        String timer = String.format(Locale.ROOT, "남은 시간: %.1f초", stateMachine.getSecondsRemaining());
        String metrics = String.format(
                Locale.ROOT,
                "고유 참여자 %d명 | 점수 %d | 피날레 응원 %d/%d",
                stateMachine.getUniqueParticipantCount(),
                stateMachine.getRoundScore(),
                stateMachine.getFinaleCheerCount(),
                stateMachine.getFinaleTriggerThreshold()
        );

        drawLine(phase, 48f, 770f, 1.1f);
        drawLine(timer, 48f, 742f, 1.0f);
        drawLine(metrics, 48f, 714f, 1.0f);
        drawParagraph("상황: " + stateMachine.getPhaseMessage(), 48f, 674f, 680f);
        drawParagraph("요약: " + stateMachine.getSummaryMessage(), 48f, 620f, 680f);
    }

    private void drawContentArea() {
        float leftX = 48f;
        float rightX = 840f;
        float baseY = 520f;

        if (stateMachine.isChoicePhase()) {
            ChoiceSet choiceSet = stateMachine.getCurrentChoiceSet();
            drawLine(choiceSet.prompt(), leftX, baseY, 1.05f);
            List<PartyAction> actions = choiceSet.actions();
            List<Integer> voteCounts = stateMachine.getVoteCounts();
            int leadingIndex = stateMachine.getLeadingOptionIndex();

            for (int index = 0; index < actions.size(); index += 1) {
                PartyAction action = actions.get(index);
                int votes = index < voteCounts.size() ? voteCounts.get(index) : 0;
                String leaderMark = leadingIndex == index ? " <- 선두" : "";
                float optionY = baseY - 46f - (index * 82f);

                drawLine((index + 1) + ". " + action.title() + leaderMark, leftX, optionY, 1.0f);
                drawParagraph(action.description(), leftX + 18f, optionY - 22f, 680f);
                drawLine("채팅 키워드 " + action.chatCommand() + " | 테스트 득표 " + votes, leftX + 18f, optionY - 52f, 0.95f);
            }

            drawParagraph("P 키를 누르면 스트리머의 오늘의 픽이 현재 선두 혹은 첫 번째 선택지에 적용됩니다.", rightX, baseY, 440f);
            return;
        }

        if (stateMachine.isTroublePhase()) {
            TroubleEvent troubleEvent = stateMachine.getCurrentTroubleEvent();
            drawLine(troubleEvent.title(), leftX, baseY, 1.05f);
            drawParagraph(troubleEvent.instruction(), leftX, baseY - 34f, 720f);
            drawLine(
                    "현재 대응 수: " + stateMachine.getTroubleProgress() + " / " + stateMachine.getCurrentTroubleRequiredResponses(),
                    leftX,
                    baseY - 118f,
                    1.0f
            );
            drawParagraph("E 키는 스트리머의 긴급 정리 콜입니다. 한 번에 추가 대응 2를 넣습니다.", rightX, baseY, 420f);
            return;
        }

        if (stateMachine.isFinaleInputOpen() || stateMachine.getState().name().contains("FINALE")) {
            drawLine("피날레 구간", leftX, baseY, 1.05f);
            drawParagraph("응원 채팅이 8명 이상 모이면 스트리머가 직접 피날레를 발동할 수 있습니다.", leftX, baseY - 34f, 720f);
            drawLine(
                    "현재 응원 수: " + stateMachine.getFinaleCheerCount() + " / " + stateMachine.getFinaleTriggerThreshold(),
                    leftX,
                    baseY - 118f,
                    1.0f
            );
            drawParagraph("F 키는 스트리머의 생일 소원 발동입니다. 응원이 충분히 모였을 때만 동작합니다.", rightX, baseY, 420f);
            return;
        }

        drawParagraph("SPACE로 첫 라운드를 시작하면 2회 선택 투표, 3회 사고 수습, 1회 피날레 응원 흐름을 확인할 수 있습니다.", leftX, baseY, 760f);
    }

    private void drawControls() {
        float x = 840f;
        float y = 300f;

        drawLine("키보드 컨트롤", x, y + 160f, 1.05f);
        drawLine("SPACE  시작 / 재시작", x, y + 126f, 0.95f);
        drawLine("1 2 3  선택지 테스트 투표", x, y + 98f, 0.95f);
        drawLine("P      스트리머 오늘의 픽", x, y + 70f, 0.95f);
        drawLine("V      문제 수습용 테스트 채팅", x, y + 42f, 0.95f);
        drawLine("E      스트리머 긴급 정리 콜", x, y + 14f, 0.95f);
        drawLine("C      피날레 응원 1회 추가", x, y - 14f, 0.95f);
        drawLine("F      스트리머 생일 소원 발동", x, y - 42f, 0.95f);
        drawLine("R      라운드 종료 후 다시 시작", x, y - 70f, 0.95f);
    }

    private void drawParagraph(String text, float x, float y, float width) {
        String[] words = text.split(" ");
        StringBuilder lineBuilder = new StringBuilder();
        float cursorY = y;

        for (String word : words) {
            String candidate = lineBuilder.length() == 0 ? word : lineBuilder + " " + word;
            if (estimateWidth(candidate) > width && lineBuilder.length() > 0) {
                drawLine(lineBuilder.toString(), x, cursorY, 0.95f);
                lineBuilder.setLength(0);
                lineBuilder.append(word);
                cursorY -= 24f;
                continue;
            }
            lineBuilder.setLength(0);
            lineBuilder.append(candidate);
        }

        if (!lineBuilder.isEmpty()) {
            drawLine(lineBuilder.toString(), x, cursorY, 0.95f);
        }
    }

    private float estimateWidth(String text) {
        return text.length() * 12f;
    }

    private void drawLine(String text, float x, float y, float scale) {
        font.getData().setScale(scale);
        font.draw(batch, text, x, y);
        font.getData().setScale(1.1f);
    }

    private String nextSyntheticViewerId() {
        String viewerId = "viewer-" + syntheticViewerSequence;
        syntheticViewerSequence += 1;
        return viewerId;
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
