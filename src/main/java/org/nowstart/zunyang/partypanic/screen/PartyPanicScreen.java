package org.nowstart.zunyang.partypanic.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.content.GameContent;
import org.nowstart.zunyang.partypanic.model.ChoiceSet;
import org.nowstart.zunyang.partypanic.model.GameState;
import org.nowstart.zunyang.partypanic.model.PartyAction;
import org.nowstart.zunyang.partypanic.model.TroubleEvent;
import org.nowstart.zunyang.partypanic.state.GameStateMachine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class PartyPanicScreen extends ScreenAdapter {
    private static final float WINDOW_WIDTH = 1600f;
    private static final float WINDOW_HEIGHT = 900f;
    private static final float STAGE_X = 36f;
    private static final float STAGE_Y = 116f;
    private static final float STAGE_WIDTH = 1096f;
    private static final float STAGE_HEIGHT = 644f;
    private static final float HUD_X = STAGE_X;
    private static final float HUD_Y = 786f;
    private static final float HUD_WIDTH = STAGE_WIDTH;
    private static final float HUD_HEIGHT = 86f;
    private static final float PANEL_X = 1160f;
    private static final float PANEL_Y = 116f;
    private static final float PANEL_WIDTH = 404f;
    private static final float PANEL_HEIGHT = 756f;
    private static final float COMMAND_X = STAGE_X;
    private static final float COMMAND_Y = 34f;
    private static final float COMMAND_WIDTH = STAGE_WIDTH;
    private static final float COMMAND_HEIGHT = 62f;

    private static final Color TEXT_PRIMARY = new Color(0.97f, 0.93f, 0.85f, 1f);
    private static final Color TEXT_MUTED = new Color(0.90f, 0.84f, 0.80f, 1f);
    private static final Color TEXT_ACCENT = new Color(1.00f, 0.88f, 0.65f, 1f);
    private static final Color TEXT_MINT = new Color(0.73f, 0.93f, 0.87f, 1f);
    private static final Color PANEL_COLOR = new Color(0.10f, 0.07f, 0.10f, 0.74f);
    private static final Color PANEL_STRONG = new Color(0.14f, 0.09f, 0.13f, 0.84f);
    private static final Color STAGE_FRAME = new Color(0.18f, 0.10f, 0.14f, 0.45f);
    private static final Color OVERLAY_COLOR = new Color(0.05f, 0.03f, 0.04f, 0.45f);
    private static final Color HIGHLIGHT_COLOR = new Color(0.96f, 0.61f, 0.71f, 0.92f);
    private static final Color HIGHLIGHT_MINT = new Color(0.68f, 0.90f, 0.83f, 0.92f);
    private static final Color BORDER_COLOR = new Color(0.97f, 0.86f, 0.78f, 0.90f);

    private final SpriteBatch batch = new SpriteBatch();
    private final GameStateMachine stateMachine = new GameStateMachine(GameContent.defaultContent());
    private final BitmapFont font;
    private final Texture pixelTexture;
    private final Texture hostTexture;
    private final Texture resultBackgroundTexture;
    private final Map<String, Texture> stageTextures = new LinkedHashMap<>();
    private final Map<String, Texture> choiceCardTextures = new LinkedHashMap<>();
    private final Map<String, Texture> troubleCardTextures = new LinkedHashMap<>();

    private int syntheticViewerSequence = 1;

    public PartyPanicScreen() {
        font = createFont();
        pixelTexture = createPixelTexture();
        hostTexture = loadTexture("images/characters/zunyang-birthday-host.png");
        resultBackgroundTexture = loadTexture("images/ui/result-card-background.png");

        stageTextures.put("desk-party", loadTexture("images/backgrounds/desk-party-stage.png"));
        stageTextures.put("mint-cats", loadTexture("images/backgrounds/mint-cats-stage.png"));
        stageTextures.put("cake-rush", loadTexture("images/backgrounds/cake-rush-stage.png"));
        stageTextures.put("finale", loadTexture("images/backgrounds/finale-stage.png"));

        choiceCardTextures.put("fan-letter", loadTexture("images/choices/fan-letter-card.png"));
        choiceCardTextures.put("mini-game", loadTexture("images/choices/mini-game-card.png"));
        choiceCardTextures.put("photo-time", loadTexture("images/choices/photo-time-card.png", "images/events/photo-time-card.png"));

        troubleCardTextures.put("audio-pop", loadTexture("images/events/audio-pop-card.png"));
        troubleCardTextures.put("cake-balance", loadTexture("images/events/cake-balance-card.png"));
        troubleCardTextures.put("camera-chaos", loadTexture("images/events/camera-chaos-card.png"));
    }

    @Override
    public void render(float delta) {
        handleInput();
        stateMachine.update(delta);

        ScreenUtils.clear(0.06f, 0.04f, 0.05f, 1f);

        batch.begin();
        drawBackdrop();
        drawBroadcastFrame();
        drawBroadcastShowcase();
        drawHud();
        drawControlsPanel();
        drawCommandBar();
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

    private void drawBackdrop() {
        Texture backdrop = resolveBackdropTexture();
        drawTextureCover(backdrop, 0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT);
        drawPanel(0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT, OVERLAY_COLOR);
    }

    private void drawBroadcastFrame() {
        drawPanel(STAGE_X - 8f, STAGE_Y - 8f, STAGE_WIDTH + 16f, STAGE_HEIGHT + 16f, STAGE_FRAME);
        drawTextureCover(resolveMainStageTexture(), STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT);
        drawPanel(STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT, new Color(0.03f, 0.02f, 0.03f, 0.12f));
        drawPanelOutline(STAGE_X - 1f, STAGE_Y - 1f, STAGE_WIDTH + 2f, STAGE_HEIGHT + 2f, BORDER_COLOR);

        drawPanel(HUD_X, HUD_Y, HUD_WIDTH, HUD_HEIGHT, PANEL_STRONG);
        drawPanelOutline(HUD_X, HUD_Y, HUD_WIDTH, HUD_HEIGHT, BORDER_COLOR);

        drawPanel(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, PANEL_STRONG);
        drawPanelOutline(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, BORDER_COLOR);

        drawPanel(COMMAND_X, COMMAND_Y, COMMAND_WIDTH, COMMAND_HEIGHT, PANEL_STRONG);
        drawPanelOutline(COMMAND_X, COMMAND_Y, COMMAND_WIDTH, COMMAND_HEIGHT, BORDER_COLOR);
    }

    private void drawBroadcastShowcase() {
        if (stateMachine.getState() == GameState.ROUND_COMPLETE) {
            drawResultScene();
            drawHostCharacter();
            return;
        }

        if (stateMachine.isChoicePhase()) {
            drawHostCharacter();
            drawChoiceScene();
            return;
        }

        if (stateMachine.isTroublePhase()) {
            drawHostCharacter();
            drawTroubleScene();
            return;
        }

        if (stateMachine.isFinaleInputOpen() || stateMachine.getState() == GameState.FINALE_RESOLVE) {
            drawHostCharacter();
            drawFinaleScene();
            return;
        }

        drawHostCharacter();
        drawIdleScene();
    }

    private void drawIdleScene() {
        float cardX = STAGE_X + 46f;
        float cardY = STAGE_Y + 128f;
        float cardWidth = 520f;
        float cardHeight = 210f;

        drawPanel(cardX, cardY, cardWidth, cardHeight, PANEL_COLOR);
        drawPanelOutline(cardX, cardY, cardWidth, cardHeight, BORDER_COLOR);
        drawLine("치즈냥 생일 방송 프리뷰", cardX + 24f, cardY + cardHeight - 24f, 1.18f, TEXT_ACCENT);
        drawParagraph("SPACE로 라운드를 시작하면 배경 투표 1회, 이벤트 카드 투표 1회, 사고 대응 3회, 피날레 응원 1회가 이어집니다.", cardX + 24f, cardY + 138f, cardWidth - 48f, 0.98f, TEXT_PRIMARY);
        drawParagraph("방송 화면은 좌측 무대만 잘라 써도 되고, 우측 패널은 운영용으로 남겨 둔 상태입니다.", cardX + 24f, cardY + 82f, cardWidth - 48f, 0.95f, TEXT_MUTED);
    }

    private void drawChoiceScene() {
        ChoiceSet choiceSet = stateMachine.getCurrentChoiceSet();
        if (choiceSet == null) {
            return;
        }

        float infoX = STAGE_X + 36f;
        float infoY = STAGE_Y + STAGE_HEIGHT - 156f;
        float infoWidth = 580f;
        float infoHeight = 118f;
        int emphasisIndex = stateMachine.isChoiceInputOpen()
                ? Math.max(0, stateMachine.getLeadingOptionIndex())
                : Math.max(0, stateMachine.getWinningOptionIndex());

        drawPanel(infoX, infoY, infoWidth, infoHeight, PANEL_COLOR);
        drawPanelOutline(infoX, infoY, infoWidth, infoHeight, BORDER_COLOR);
        drawLine(choiceSet.roundLabel(), infoX + 22f, infoY + infoHeight - 20f, 1.10f, TEXT_ACCENT);
        drawParagraph(choiceSet.prompt(), infoX + 22f, infoY + 60f, infoWidth - 44f, 1.02f, TEXT_PRIMARY);
        drawParagraph("선두 선택은 분홍 테두리로 강조됩니다. 숫자 1 2 3으로 테스트 득표, P로 오늘의 픽을 넣을 수 있습니다.", infoX + 22f, infoY + 26f, infoWidth - 44f, 0.86f, TEXT_MUTED);

        drawChoiceGallery(choiceSet, emphasisIndex);
    }

    private void drawChoiceGallery(ChoiceSet choiceSet, int emphasisIndex) {
        boolean firstChoice = stateMachine.getCurrentChoiceIndex() == 0;
        float cardWidth = firstChoice ? 248f : 228f;
        float cardHeight = firstChoice ? 176f : 228f;
        float gap = 28f;
        float totalWidth = (cardWidth * 3f) + (gap * 2f);
        float startX = STAGE_X + 52f;
        float startY = STAGE_Y + 42f;
        List<PartyAction> actions = choiceSet.actions();
        List<Integer> voteCounts = stateMachine.getVoteCounts();
        boolean resolvedState = !stateMachine.isChoiceInputOpen();

        if (totalWidth > STAGE_WIDTH - 360f) {
            startX = STAGE_X + 36f;
        }

        for (int index = 0; index < actions.size(); index += 1) {
            PartyAction action = actions.get(index);
            Texture texture = resolveChoiceTexture(action.id(), firstChoice);
            float x = startX + (index * (cardWidth + gap));
            float y = startY;
            Color border = index == emphasisIndex ? HIGHLIGHT_COLOR : BORDER_COLOR;

            drawPanel(x - 10f, y - 10f, cardWidth + 20f, cardHeight + 72f, new Color(0.13f, 0.08f, 0.12f, 0.75f));
            drawTextureCover(texture, x, y + 34f, cardWidth, cardHeight);
            drawPanel(x, y + 34f, cardWidth, cardHeight, new Color(0.05f, 0.03f, 0.04f, 0.18f));
            drawPanelOutline(x, y + 34f, cardWidth, cardHeight, border);

            drawLine((index + 1) + ". " + action.title(), x, y + 22f, 0.96f, index == emphasisIndex ? TEXT_ACCENT : TEXT_PRIMARY);
            drawLine(resolveVoteLabel(voteCounts, index, resolvedState, emphasisIndex), x, y + 2f, 0.84f, TEXT_MUTED);
        }
    }

    private void drawTroubleScene() {
        TroubleEvent troubleEvent = stateMachine.getCurrentTroubleEvent();
        if (troubleEvent == null) {
            return;
        }

        float infoX = STAGE_X + 36f;
        float infoY = STAGE_Y + STAGE_HEIGHT - 160f;
        float infoWidth = 590f;
        float infoHeight = 122f;
        float cardX = STAGE_X + 58f;
        float cardY = STAGE_Y + 148f;
        float cardSize = 288f;
        int requiredResponses = stateMachine.getCurrentTroubleRequiredResponses();
        int progress = stateMachine.getTroubleProgress();
        float progressRatio = requiredResponses == 0 ? 0f : Math.min(1f, progress / (float) requiredResponses);

        drawPanel(infoX, infoY, infoWidth, infoHeight, PANEL_COLOR);
        drawPanelOutline(infoX, infoY, infoWidth, infoHeight, BORDER_COLOR);
        drawLine(troubleEvent.title(), infoX + 22f, infoY + infoHeight - 22f, 1.06f, TEXT_ACCENT);
        drawParagraph(troubleEvent.instruction(), infoX + 22f, infoY + 64f, infoWidth - 44f, 0.98f, TEXT_PRIMARY);
        drawParagraph("V로 대응 1, E로 긴급 정리 콜 2를 추가합니다.", infoX + 22f, infoY + 28f, infoWidth - 44f, 0.86f, TEXT_MUTED);

        Texture troubleTexture = troubleCardTextures.get(troubleEvent.id());
        drawPanel(cardX - 10f, cardY - 10f, cardSize + 20f, cardSize + 20f, new Color(0.12f, 0.08f, 0.11f, 0.75f));
        drawTextureFit(troubleTexture, cardX, cardY, cardSize, cardSize);
        drawPanelOutline(cardX, cardY, cardSize, cardSize, HIGHLIGHT_COLOR);

        drawMiniSelectionBadge(resolveResolvedChoiceAction(1), STAGE_X + 388f, STAGE_Y + 184f, 172f, "선택된 이벤트");

        float meterX = STAGE_X + 388f;
        float meterY = STAGE_Y + 136f;
        float meterWidth = 246f;
        drawLine("현재 대응 " + progress + " / " + requiredResponses, meterX, meterY + 56f, 1.00f, TEXT_PRIMARY);
        drawPanel(meterX, meterY + 18f, meterWidth, 16f, new Color(0.22f, 0.16f, 0.18f, 0.90f));
        drawPanel(meterX, meterY + 18f, meterWidth * progressRatio, 16f, HIGHLIGHT_MINT);
        drawPanelOutline(meterX, meterY + 18f, meterWidth, 16f, BORDER_COLOR);
    }

    private void drawFinaleScene() {
        float infoX = STAGE_X + 36f;
        float infoY = STAGE_Y + STAGE_HEIGHT - 152f;
        float infoWidth = 612f;
        float infoHeight = 114f;
        float cheerRatio = Math.min(1f, stateMachine.getFinaleCheerCount() / (float) stateMachine.getFinaleTriggerThreshold());

        drawPanel(infoX, infoY, infoWidth, infoHeight, PANEL_COLOR);
        drawPanelOutline(infoX, infoY, infoWidth, infoHeight, BORDER_COLOR);
        drawLine(stateMachine.getPhaseTitle(), infoX + 22f, infoY + infoHeight - 22f, 1.12f, TEXT_ACCENT);
        drawParagraph(stateMachine.getPhaseMessage(), infoX + 22f, infoY + 60f, infoWidth - 44f, 0.98f, TEXT_PRIMARY);
        drawParagraph(stateMachine.getSummaryMessage(), infoX + 22f, infoY + 26f, infoWidth - 44f, 0.86f, TEXT_MUTED);

        float meterX = STAGE_X + 54f;
        float meterY = STAGE_Y + 172f;
        float meterWidth = 364f;
        drawLine("피날레 응원 " + stateMachine.getFinaleCheerCount() + " / " + stateMachine.getFinaleTriggerThreshold(), meterX, meterY + 58f, 1.02f, TEXT_PRIMARY);
        drawPanel(meterX, meterY + 22f, meterWidth, 20f, new Color(0.22f, 0.14f, 0.18f, 0.88f));
        drawPanel(meterX, meterY + 22f, meterWidth * cheerRatio, 20f, HIGHLIGHT_COLOR);
        drawPanelOutline(meterX, meterY + 22f, meterWidth, 20f, BORDER_COLOR);

        drawMiniSelectionBadge(resolveResolvedChoiceAction(1), STAGE_X + 470f, STAGE_Y + 178f, 184f, "선택된 이벤트");
    }

    private void drawResultScene() {
        drawTextureCover(resultBackgroundTexture, STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT);
        drawPanel(STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT, new Color(0.05f, 0.03f, 0.04f, 0.18f));

        float resultX = STAGE_X + 56f;
        float resultY = STAGE_Y + 150f;
        float resultWidth = 520f;
        float resultHeight = 278f;
        String grade = resolveGrade(stateMachine.getRoundScore());

        drawPanel(resultX, resultY, resultWidth, resultHeight, new Color(0.13f, 0.08f, 0.11f, 0.80f));
        drawPanelOutline(resultX, resultY, resultWidth, resultHeight, BORDER_COLOR);
        drawLine("생일 파티 결과", resultX + 28f, resultY + resultHeight - 26f, 1.14f, TEXT_ACCENT);
        drawLine(grade, resultX + 28f, resultY + resultHeight - 88f, 2.10f, HIGHLIGHT_MINT);
        drawLine("최종 점수 " + stateMachine.getRoundScore(), resultX + 158f, resultY + resultHeight - 94f, 1.04f, TEXT_PRIMARY);
        drawParagraph(resolveGradeLine(grade), resultX + 28f, resultY + 118f, resultWidth - 56f, 1.00f, TEXT_PRIMARY);
        drawParagraph(stateMachine.getSummaryMessage(), resultX + 28f, resultY + 72f, resultWidth - 56f, 0.90f, TEXT_MUTED);

        drawMiniSelectionBadge(resolveResolvedChoiceAction(0), STAGE_X + 620f, STAGE_Y + 194f, 176f, "확정된 배경");
        drawMiniSelectionBadge(resolveResolvedChoiceAction(1), STAGE_X + 816f, STAGE_Y + 194f, 176f, "확정된 이벤트");
    }

    private void drawHud() {
        String timer = String.format(Locale.ROOT, "남은 시간 %.1f초", stateMachine.getSecondsRemaining());
        String metrics = String.format(
                Locale.ROOT,
                "참여자 %d명  |  점수 %d  |  피날레 %d/%d",
                stateMachine.getUniqueParticipantCount(),
                stateMachine.getRoundScore(),
                stateMachine.getFinaleCheerCount(),
                stateMachine.getFinaleTriggerThreshold()
        );

        drawLine("치즈냥 생일 파티 프로토타입", HUD_X + 22f, HUD_Y + HUD_HEIGHT - 18f, 1.20f, TEXT_ACCENT);
        drawLine(stateMachine.getPhaseTitle(), HUD_X + 22f, HUD_Y + 34f, 1.08f, TEXT_PRIMARY);
        drawLine(timer, HUD_X + 440f, HUD_Y + HUD_HEIGHT - 18f, 0.98f, TEXT_PRIMARY);
        drawLine(metrics, HUD_X + 440f, HUD_Y + 34f, 0.96f, TEXT_MUTED);
    }

    private void drawControlsPanel() {
        float left = PANEL_X + 22f;
        float top = PANEL_Y + PANEL_HEIGHT - 26f;
        PartyAction stageAction = resolveResolvedChoiceAction(0);
        PartyAction routeAction = resolveResolvedChoiceAction(1);

        drawLine("운영 패널", left, top, 1.16f, TEXT_ACCENT);
        drawLine("상태 " + stateMachine.getState().name(), left, top - 34f, 0.90f, TEXT_MUTED);
        drawLine("방송 무드 " + readableActionTitle(stageAction, "기본 무대"), left, top - 66f, 0.92f, TEXT_PRIMARY);
        drawLine("중반 이벤트 " + readableActionTitle(routeAction, "미정"), left, top - 94f, 0.92f, TEXT_PRIMARY);

        float rowY = top - 154f;
        drawControlRow("SPACE", "라운드 시작 / 재시작", rowY, stateMachine.getState() == GameState.IDLE || stateMachine.getState() == GameState.ROUND_COMPLETE);
        drawControlRow("1 2 3", "선택지 테스트 득표", rowY - 48f, stateMachine.isChoiceInputOpen());
        drawControlRow("P", "오늘의 픽", rowY - 96f, stateMachine.canUseTodayPick());
        drawControlRow("V", "사고 대응 1회", rowY - 144f, stateMachine.isTroubleInputOpen());
        drawControlRow("E", "긴급 정리 콜", rowY - 192f, stateMachine.canTriggerEmergencyCall());
        drawControlRow("C", "피날레 응원", rowY - 240f, stateMachine.isFinaleInputOpen());
        drawControlRow("F", "생일 소원 발동", rowY - 288f, stateMachine.canTriggerFinale());
        drawControlRow("R", "결과 후 다음 판", rowY - 336f, stateMachine.getState() == GameState.ROUND_COMPLETE);

        float noteY = PANEL_Y + 92f;
        drawLine("요약", left, noteY + 108f, 1.00f, TEXT_ACCENT);
        drawParagraph("상황: " + stateMachine.getPhaseMessage(), left, noteY + 72f, PANEL_WIDTH - 44f, 0.92f, TEXT_PRIMARY);
        drawParagraph("요약: " + stateMachine.getSummaryMessage(), left, noteY + 26f, PANEL_WIDTH - 44f, 0.86f, TEXT_MUTED);
    }

    private void drawControlRow(String key, String label, float baselineY, boolean active) {
        float keyX = PANEL_X + 22f;
        float keyWidth = 92f;
        float rowWidth = PANEL_WIDTH - 44f;
        float rowHeight = 36f;
        Color tagColor = active ? HIGHLIGHT_COLOR : new Color(0.26f, 0.18f, 0.21f, 0.95f);
        Color rowColor = active ? new Color(0.20f, 0.12f, 0.16f, 0.72f) : new Color(0.12f, 0.08f, 0.11f, 0.72f);

        drawPanel(keyX, baselineY - 24f, rowWidth, rowHeight, rowColor);
        drawPanel(keyX, baselineY - 24f, keyWidth, rowHeight, tagColor);
        drawPanelOutline(keyX, baselineY - 24f, rowWidth, rowHeight, BORDER_COLOR);
        drawLine(key, keyX + 14f, baselineY - 2f, 0.92f, active ? Color.BLACK : TEXT_PRIMARY);
        drawLine(label, keyX + keyWidth + 16f, baselineY - 2f, 0.90f, active ? TEXT_PRIMARY : TEXT_MUTED);
    }

    private void drawCommandBar() {
        drawLine(resolveCommandHint(), COMMAND_X + 22f, COMMAND_Y + 38f, 0.94f, TEXT_PRIMARY);
    }

    private void drawHostCharacter() {
        float drawHeight = 468f;
        float drawWidth = drawHeight * (hostTexture.getWidth() / (float) hostTexture.getHeight());
        float x = STAGE_X + STAGE_WIDTH - drawWidth - 42f;
        float y = STAGE_Y + 22f;

        drawPanel(x - 18f, y - 16f, drawWidth + 36f, drawHeight + 32f, new Color(0.14f, 0.08f, 0.12f, 0.40f));
        drawTextureFit(hostTexture, x, y, drawWidth, drawHeight);
    }

    private void drawMiniSelectionBadge(PartyAction action, float x, float y, float size, String label) {
        if (action == null) {
            return;
        }

        Texture texture = resolveChoiceTexture(action.id(), false);
        if (texture == null) {
            texture = resolveChoiceTexture(action.id(), true);
        }

        drawPanel(x, y, size, size + 52f, new Color(0.12f, 0.08f, 0.11f, 0.78f));
        drawTextureFit(texture, x + 12f, y + 34f, size - 24f, size - 24f);
        drawPanelOutline(x, y, size, size + 52f, BORDER_COLOR);
        drawLine(label, x + 12f, y + 22f, 0.78f, TEXT_MUTED);
        drawLine(action.title(), x + 12f, y + size + 34f, 0.82f, TEXT_PRIMARY);
    }

    private Texture resolveBackdropTexture() {
        if (stateMachine.getState() == GameState.ROUND_COMPLETE) {
            return resultBackgroundTexture;
        }
        if (stateMachine.isFinaleInputOpen() || stateMachine.getState() == GameState.FINALE_RESOLVE) {
            return stageTextures.get("finale");
        }
        return resolveStageThemeTexture();
    }

    private Texture resolveMainStageTexture() {
        if (stateMachine.getState() == GameState.ROUND_COMPLETE) {
            return resultBackgroundTexture;
        }
        if (stateMachine.isFinaleInputOpen() || stateMachine.getState() == GameState.FINALE_RESOLVE) {
            return stageTextures.get("finale");
        }
        return resolveStageThemeTexture();
    }

    private Texture resolveStageThemeTexture() {
        PartyAction resolvedStageAction = resolveResolvedChoiceAction(0);
        if (resolvedStageAction != null) {
            return stageTextures.getOrDefault(resolvedStageAction.id(), stageTextures.get("desk-party"));
        }

        ChoiceSet currentChoiceSet = stateMachine.getCurrentChoiceSet();
        if (currentChoiceSet != null && stateMachine.getCurrentChoiceIndex() == 0) {
            int previewIndex = stateMachine.isChoiceInputOpen()
                    ? Math.max(0, stateMachine.getLeadingOptionIndex())
                    : Math.max(0, stateMachine.getWinningOptionIndex());

            if (previewIndex < currentChoiceSet.actions().size()) {
                String actionId = currentChoiceSet.actions().get(previewIndex).id();
                return stageTextures.getOrDefault(actionId, stageTextures.get("desk-party"));
            }
        }

        return stageTextures.get("desk-party");
    }

    private Texture resolveChoiceTexture(String actionId, boolean firstChoice) {
        if (firstChoice) {
            return stageTextures.getOrDefault(actionId, stageTextures.get("desk-party"));
        }
        return choiceCardTextures.getOrDefault(actionId, troubleCardTextures.get(actionId));
    }

    private String resolveVoteLabel(List<Integer> voteCounts, int index, boolean resolvedState, int emphasisIndex) {
        int votes = index < voteCounts.size() ? voteCounts.get(index) : 0;
        if (resolvedState && index == emphasisIndex) {
            return "확정됨  |  테스트 득표 " + votes;
        }
        if (index == emphasisIndex) {
            return "현재 선두  |  테스트 득표 " + votes;
        }
        return "테스트 득표 " + votes;
    }

    private PartyAction resolveResolvedChoiceAction(int choiceIndex) {
        return stateMachine.getResolvedChoiceAction(choiceIndex);
    }

    private String resolveCommandHint() {
        if (stateMachine.isChoiceInputOpen()) {
            return "현재 입력: 1 2 3 으로 선택지 득표, P 로 오늘의 픽";
        }
        if (stateMachine.isTroubleInputOpen()) {
            return "현재 입력: V 로 대응 1회, E 로 긴급 정리 콜";
        }
        if (stateMachine.isFinaleInputOpen()) {
            return "현재 입력: C 로 응원 추가, F 로 피날레 발동";
        }
        if (stateMachine.getState() == GameState.ROUND_COMPLETE) {
            return "현재 입력: R 또는 SPACE 로 다음 판 시작";
        }
        return "현재 입력: SPACE 로 라운드 시작";
    }

    private String resolveGrade(int score) {
        if (score >= 28) {
            return "S";
        }
        if (score >= 22) {
            return "A";
        }
        if (score >= 16) {
            return "B";
        }
        if (score >= 10) {
            return "C";
        }
        return "D";
    }

    private String resolveGradeLine(String grade) {
        return switch (grade) {
            case "S" -> "모두가 호흡을 맞춰 최고의 생일 무대를 완성했습니다.";
            case "A" -> "조금 정신없었지만 아주 좋은 생일 방송으로 마무리됐습니다.";
            case "B" -> "중간 위기는 있었지만 끝은 충분히 해피엔딩이었습니다.";
            case "C" -> "꽤 부산스러웠지만 그래서 더 기억에 남는 파티였습니다.";
            default -> "사고는 많았지만 방송은 남았습니다. 완전히 밈이 된 생일 파티입니다.";
        };
    }

    private String readableActionTitle(PartyAction action, String fallback) {
        return action == null ? fallback : action.title();
    }

    private void drawPanel(float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(pixelTexture, x, y, width, height);
        batch.setColor(Color.WHITE);
    }

    private void drawPanelOutline(float x, float y, float width, float height, Color color) {
        drawPanel(x, y, width, 2f, color);
        drawPanel(x, y + height - 2f, width, 2f, color);
        drawPanel(x, y, 2f, height, color);
        drawPanel(x + width - 2f, y, 2f, height, color);
    }

    private void drawTextureCover(Texture texture, float x, float y, float width, float height) {
        float targetAspect = width / height;
        float textureAspect = texture.getWidth() / (float) texture.getHeight();
        int srcX = 0;
        int srcY = 0;
        int srcWidth = texture.getWidth();
        int srcHeight = texture.getHeight();

        if (textureAspect > targetAspect) {
            srcWidth = Math.round(texture.getHeight() * targetAspect);
            srcX = (texture.getWidth() - srcWidth) / 2;
        } else if (textureAspect < targetAspect) {
            srcHeight = Math.round(texture.getWidth() / targetAspect);
            srcY = (texture.getHeight() - srcHeight) / 2;
        }

        batch.draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, false, false);
    }

    private void drawTextureFit(Texture texture, float x, float y, float width, float height) {
        float scale = Math.min(width / texture.getWidth(), height / texture.getHeight());
        float drawWidth = texture.getWidth() * scale;
        float drawHeight = texture.getHeight() * scale;
        float drawX = x + ((width - drawWidth) * 0.5f);
        float drawY = y + ((height - drawHeight) * 0.5f);
        batch.draw(texture, drawX, drawY, drawWidth, drawHeight);
    }

    private void drawParagraph(String text, float x, float y, float width, float scale, Color color) {
        String[] words = text.split(" ");
        StringBuilder lineBuilder = new StringBuilder();
        float cursorY = y;

        for (String word : words) {
            String candidate = lineBuilder.length() == 0 ? word : lineBuilder + " " + word;
            if (estimateWidth(candidate, scale) > width && lineBuilder.length() > 0) {
                drawLine(lineBuilder.toString(), x, cursorY, scale, color);
                lineBuilder.setLength(0);
                lineBuilder.append(word);
                cursorY -= 24f * scale;
                continue;
            }
            lineBuilder.setLength(0);
            lineBuilder.append(candidate);
        }

        if (!lineBuilder.isEmpty()) {
            drawLine(lineBuilder.toString(), x, cursorY, scale, color);
        }
    }

    private float estimateWidth(String text, float scale) {
        return text.length() * 11.6f * scale;
    }

    private BitmapFont createFont() {
        Path fontPath = resolveFontPath();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.absolute(fontPath.toString()));
        try {
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 24;
            parameter.incremental = false;
            parameter.minFilter = Texture.TextureFilter.Linear;
            parameter.magFilter = Texture.TextureFilter.Linear;
            parameter.characters = buildFontCharacters();
            return generator.generateFont(parameter);
        } finally {
            generator.dispose();
        }
    }

    private Texture createPixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture loadTexture(String... candidates) {
        for (String candidate : candidates) {
            FileHandle handle = Gdx.files.internal(candidate);
            if (handle.exists()) {
                return new Texture(handle);
            }
        }

        throw new IllegalStateException("Missing texture. Tried: " + String.join(", ", candidates));
    }

    private Path resolveFontPath() {
        List<Path> candidates = new ArrayList<>();
        candidates.add(Path.of("asset", "fonts", "NotoSansKR-Regular.ttf").toAbsolutePath());
        candidates.add(Path.of("assets", "fonts", "NotoSansKR-Regular.ttf").toAbsolutePath());
        candidates.add(Path.of("asset", "fonts", "malgun.ttf").toAbsolutePath());
        candidates.add(Path.of("assets", "fonts", "malgun.ttf").toAbsolutePath());
        candidates.add(Path.of("/mnt/c/Windows/Fonts/malgun.ttf"));
        candidates.add(Path.of("/mnt/c/Windows/Fonts/malgunbd.ttf"));
        candidates.add(Path.of("/mnt/c/Windows/Fonts/malgunsl.ttf"));

        Path windowsMalgunFont = resolveWindowsFont("malgun.ttf");
        if (windowsMalgunFont != null) {
            candidates.add(windowsMalgunFont);
        }

        Path windowsMalgunBoldFont = resolveWindowsFont("malgunbd.ttf");
        if (windowsMalgunBoldFont != null) {
            candidates.add(windowsMalgunBoldFont);
        }

        Path windowsMalgunLightFont = resolveWindowsFont("malgunsl.ttf");
        if (windowsMalgunLightFont != null) {
            candidates.add(windowsMalgunLightFont);
        }

        candidates.add(Path.of("asset", "fonts", "NotoSansKR-VF.ttf").toAbsolutePath());
        candidates.add(Path.of("assets", "fonts", "NotoSansKR-VF.ttf").toAbsolutePath());

        Path windowsNotoFont = resolveWindowsFont("NotoSansKR-VF.ttf");
        if (windowsNotoFont != null) {
            candidates.add(windowsNotoFont);
        }

        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate) && !isUnsupportedVariableFont(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "Korean font not found. Add a TTF under asset/fonts or install NotoSansKR/Malgun Gothic."
        );
    }

    private boolean isUnsupportedVariableFont(Path candidate) {
        String fileName = candidate.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.contains("-vf.");
    }

    private Path resolveWindowsFont(String fileName) {
        String windowsDirectory = System.getenv("WINDIR");
        if (windowsDirectory == null || windowsDirectory.isBlank()) {
            return null;
        }
        return Path.of(windowsDirectory, "Fonts", fileName);
    }

    private String buildFontCharacters() {
        Set<Character> characters = new LinkedHashSet<>();
        appendCharacters(characters, FreeTypeFontGenerator.DEFAULT_CHARS);

        for (String text : List.of(
                "치즈냥 생일 파티 프로토타입",
                "남은 시간 %.1f초",
                "참여자 %d명  |  점수 %d  |  피날레 %d/%d",
                "운영 패널",
                "상태 ",
                "방송 무드 ",
                "중반 이벤트 ",
                "라운드 시작 / 재시작",
                "선택지 테스트 득표",
                "오늘의 픽",
                "사고 대응 1회",
                "긴급 정리 콜",
                "피날레 응원",
                "생일 소원 발동",
                "결과 후 다음 판",
                "요약",
                "상황: ",
                "현재 입력: 1 2 3 으로 선택지 득표, P 로 오늘의 픽",
                "현재 입력: V 로 대응 1회, E 로 긴급 정리 콜",
                "현재 입력: C 로 응원 추가, F 로 피날레 발동",
                "현재 입력: R 또는 SPACE 로 다음 판 시작",
                "현재 입력: SPACE 로 라운드 시작",
                "치즈냥 생일 방송 프리뷰",
                "SPACE로 라운드를 시작하면 배경 투표 1회, 이벤트 카드 투표 1회, 사고 대응 3회, 피날레 응원 1회가 이어집니다.",
                "방송 화면은 좌측 무대만 잘라 써도 되고, 우측 패널은 운영용으로 남겨 둔 상태입니다.",
                "선두 선택은 분홍 테두리로 강조됩니다. 숫자 1 2 3으로 테스트 득표, P로 오늘의 픽을 넣을 수 있습니다.",
                "현재 선두  |  테스트 득표 ",
                "확정됨  |  테스트 득표 ",
                "V로 대응 1, E로 긴급 정리 콜 2를 추가합니다.",
                "선택된 이벤트",
                "현재 대응 ",
                " / ",
                "피날레 응원 ",
                "생일 파티 결과",
                "최종 점수 ",
                "확정된 배경",
                "같은 VTuber 세계관과 방송 무드",
                "기본 무대",
                "미정",
                "모두가 호흡을 맞춰 최고의 생일 무대를 완성했습니다.",
                "조금 정신없었지만 아주 좋은 생일 방송으로 마무리됐습니다.",
                "중간 위기는 있었지만 끝은 충분히 해피엔딩이었습니다.",
                "꽤 부산스러웠지만 그래서 더 기억에 남는 파티였습니다.",
                "사고는 많았지만 방송은 남았습니다. 완전히 밈이 된 생일 파티입니다.",
                "S",
                "A",
                "B",
                "C",
                "D"
        )) {
            appendCharacters(characters, text);
        }

        GameContent content = GameContent.defaultContent();
        for (ChoiceSet choiceSet : content.choiceSets()) {
            appendCharacters(characters, choiceSet.id());
            appendCharacters(characters, choiceSet.prompt());
            appendCharacters(characters, choiceSet.roundLabel());
            appendCharacters(characters, choiceSet.resolutionText());

            for (PartyAction action : choiceSet.actions()) {
                appendCharacters(characters, action.id());
                appendCharacters(characters, action.title());
                appendCharacters(characters, action.description());
                appendCharacters(characters, action.chatCommand());
                appendCharacters(characters, action.streamerNote());
            }
        }

        for (TroubleEvent troubleEvent : content.troubleEvents()) {
            appendCharacters(characters, troubleEvent.id());
            appendCharacters(characters, troubleEvent.title());
            appendCharacters(characters, troubleEvent.instruction());
            appendCharacters(characters, troubleEvent.successText());
            appendCharacters(characters, troubleEvent.failureText());
        }

        StringBuilder builder = new StringBuilder(characters.size());
        for (Character character : characters) {
            builder.append(character);
        }
        return builder.toString();
    }

    private void appendCharacters(Set<Character> characters, String text) {
        for (int index = 0; index < text.length(); index += 1) {
            characters.add(text.charAt(index));
        }
    }

    private void drawLine(String text, float x, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        font.draw(batch, text, x, y);
        font.setColor(TEXT_PRIMARY);
        font.getData().setScale(1f);
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
        pixelTexture.dispose();
        hostTexture.dispose();
        resultBackgroundTexture.dispose();
        for (Texture texture : stageTextures.values()) {
            texture.dispose();
        }
        for (Texture texture : choiceCardTextures.values()) {
            texture.dispose();
        }
        for (Texture texture : troubleCardTextures.values()) {
            texture.dispose();
        }
    }
}
