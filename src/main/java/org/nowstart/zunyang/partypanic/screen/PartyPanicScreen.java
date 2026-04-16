package org.nowstart.zunyang.partypanic.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.PartyPanicGame;
import org.nowstart.zunyang.partypanic.world.GameProgress;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PartyPanicScreen extends ScreenAdapter {
    private static final float WINDOW_WIDTH = 1600f;
    private static final float WINDOW_HEIGHT = 900f;
    private static final float STAGE_X = 36f;
    private static final float STAGE_Y = 116f;
    private static final float STAGE_WIDTH = 1096f;
    private static final float STAGE_HEIGHT = 644f;
    private static final float PANEL_X = 1160f;
    private static final float PANEL_Y = 116f;
    private static final float PANEL_WIDTH = 404f;
    private static final float PANEL_HEIGHT = 756f;
    private static final float COMMAND_X = STAGE_X;
    private static final float COMMAND_Y = 34f;
    private static final float COMMAND_WIDTH = STAGE_WIDTH;
    private static final float COMMAND_HEIGHT = 62f;
    private static final float ACTIVE_SECONDS = 45f;

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

    private static final Color TEXT_PRIMARY = new Color(0.97f, 0.93f, 0.85f, 1f);
    private static final Color TEXT_MUTED = new Color(0.90f, 0.84f, 0.80f, 1f);
    private static final Color TEXT_ACCENT = new Color(1.00f, 0.88f, 0.65f, 1f);
    private static final Color TEXT_MINT = new Color(0.73f, 0.93f, 0.87f, 1f);
    private static final Color PANEL_COLOR = new Color(0.10f, 0.07f, 0.10f, 0.74f);
    private static final Color PANEL_STRONG = new Color(0.14f, 0.09f, 0.13f, 0.84f);
    private static final Color STAGE_FRAME = new Color(0.18f, 0.10f, 0.14f, 0.45f);
    private static final Color OVERLAY_COLOR = new Color(0.05f, 0.03f, 0.04f, 0.45f);
    private static final Color HIGHLIGHT_COLOR = new Color(0.96f, 0.61f, 0.71f, 0.92f);
    private static final Color BORDER_COLOR = new Color(0.97f, 0.86f, 0.78f, 0.90f);

    private final PartyPanicGame game;
    private final GameProgress progress;
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font;
    private final Texture pixelTexture;
    private final Texture backgroundTexture;
    private final Texture hostTexture;

    private final float[] values = DEFAULT_VALUES.clone();
    private final boolean[] confirmed = new boolean[TASK_TITLES.length];

    private Phase phase = Phase.READY;
    private int selectedIndex;
    private float secondsRemaining = ACTIVE_SECONDS;
    private int adjustmentCount;
    private int finalScore;
    private String feedback = "SPACE를 눌러 책상 정리를 시작하세요.";

    public PartyPanicScreen(PartyPanicGame game, GameProgress progress) {
        this.game = game;
        this.progress = progress;
        this.font = ScreenSupport.createFont(buildFontCharacters());
        this.pixelTexture = ScreenSupport.createPixelTexture();
        this.backgroundTexture = ScreenSupport.loadTexture("images/backgrounds/desk-party-stage.png");
        this.hostTexture = ScreenSupport.loadTexture("images/characters/zunyang-birthday-host.png");
    }

    @Override
    public void render(float delta) {
        handleInput();
        if (game.getScreen() != this) {
            return;
        }

        update(delta);

        ScreenUtils.clear(0.06f, 0.04f, 0.05f, 1f);

        batch.begin();
        drawBackdrop();
        drawFrames();
        drawDeskSetupStage();
        if (showsOperationalUi()) {
            drawStatusPanel();
            drawCommandBar();
        } else {
            drawLiveHud();
        }
        batch.end();
    }

    private void update(float delta) {
        if (phase != Phase.ACTIVE) {
            return;
        }

        secondsRemaining = Math.max(0f, secondsRemaining - delta);
        if (allConfirmed() || secondsRemaining <= 0f) {
            finishRound();
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.showHub("방송 책상에서 허브로 복귀했습니다.");
            return;
        }

        if (phase == Phase.READY) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                phase = Phase.ACTIVE;
                feedback = "첫 작업부터 맞춰 보자.";
            }
            return;
        }

        if (phase == Phase.ACTIVE) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                selectedIndex = (selectedIndex + TASK_TITLES.length - 1) % TASK_TITLES.length;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                selectedIndex = (selectedIndex + 1) % TASK_TITLES.length;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                adjustSelected(-6f);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                adjustSelected(6f);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                confirmSelected();
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            resetRound();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.H) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.finishBroadcastDeskMinigame(finalScore);
        }
    }

    private void adjustSelected(float delta) {
        if (confirmed[selectedIndex]) {
            feedback = TASK_TITLES[selectedIndex] + "은 이미 고정했습니다.";
            return;
        }

        values[selectedIndex] = Math.max(0f, Math.min(100f, values[selectedIndex] + delta));
        adjustmentCount += 1;
        feedback = TASK_TITLES[selectedIndex] + " 수치를 조정했습니다.";
    }

    private void confirmSelected() {
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

    private boolean isWithinTarget(int index) {
        return values[index] >= TARGET_MIN[index] && values[index] <= TARGET_MAX[index];
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

    private void resetRound() {
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

    private void drawBackdrop() {
        drawTextureCover(backgroundTexture, 0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT);
        drawPanel(0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT, OVERLAY_COLOR);
    }

    private void drawFrames() {
        drawPanel(STAGE_X - 8f, STAGE_Y - 8f, STAGE_WIDTH + 16f, STAGE_HEIGHT + 16f, STAGE_FRAME);
        drawTextureCover(backgroundTexture, STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT);
        drawPanel(STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT, new Color(0.03f, 0.02f, 0.03f, 0.14f));
        drawPanelOutline(STAGE_X - 1f, STAGE_Y - 1f, STAGE_WIDTH + 2f, STAGE_HEIGHT + 2f, BORDER_COLOR);

        if (showsOperationalUi()) {
            drawPanel(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, PANEL_STRONG);
            drawPanelOutline(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, BORDER_COLOR);

            drawPanel(COMMAND_X, COMMAND_Y, COMMAND_WIDTH, COMMAND_HEIGHT, PANEL_STRONG);
            drawPanelOutline(COMMAND_X, COMMAND_Y, COMMAND_WIDTH, COMMAND_HEIGHT, BORDER_COLOR);
        }
    }

    private void drawDeskSetupStage() {
        float titleX = STAGE_X + 24f;
        float titleY = STAGE_Y + STAGE_HEIGHT - 18f;
        float infoX = STAGE_X + 28f;
        float infoY = STAGE_Y + STAGE_HEIGHT - 144f;
        float infoWidth = 596f;
        float infoHeight = 100f;
        float hostHeight = 404f;
        float hostWidth = hostHeight * (hostTexture.getWidth() / (float) hostTexture.getHeight());
        float hostX = STAGE_X + STAGE_WIDTH - hostWidth - 28f;
        float hostY = STAGE_Y + 46f;

        drawLine("방송 책상 정리", titleX, titleY, 1.22f, TEXT_ACCENT);
        drawPanel(infoX, infoY, infoWidth, infoHeight, PANEL_COLOR);
        drawPanelOutline(infoX, infoY, infoWidth, infoHeight, BORDER_COLOR);
        drawParagraph(resolvePhaseDescription(), infoX + 18f, infoY + 58f, infoWidth - 36f, 0.94f, TEXT_PRIMARY);
        drawLine(resolvePhaseHint(), infoX + 18f, infoY + 24f, 0.82f, TEXT_MUTED);

        float startX = STAGE_X + 56f;
        float startY = STAGE_Y + 302f;
        for (int index = 0; index < TASK_TITLES.length; index += 1) {
            float x = startX + ((index % 2) * 318f);
            float y = startY - ((index / 2) * 190f);
            drawTaskCard(index, x, y, 286f, 150f);
        }

        drawPanel(hostX - 16f, hostY - 14f, hostWidth + 32f, hostHeight + 28f, new Color(0.14f, 0.08f, 0.12f, 0.36f));
        drawTextureFit(hostTexture, hostX, hostY, hostWidth, hostHeight);
    }

    private void drawTaskCard(int index, float x, float y, float width, float height) {
        boolean selected = selectedIndex == index;
        boolean completed = confirmed[index];
        Color border = completed ? TEXT_MINT : selected ? HIGHLIGHT_COLOR : BORDER_COLOR;
        float gaugeWidth = width - 34f;
        float markerX = x + 18f + (gaugeWidth * (values[index] / 100f));
        float targetX = x + 18f + (gaugeWidth * (TARGET_MIN[index] / 100f));
        float targetWidth = gaugeWidth * ((TARGET_MAX[index] - TARGET_MIN[index]) / 100f);

        drawPanel(x, y, width, height, PANEL_COLOR);
        drawPanelOutline(x, y, width, height, border);
        drawLine(TASK_TITLES[index], x + 18f, y + height - 18f, 0.94f, completed ? TEXT_MINT : TEXT_PRIMARY);
        drawParagraph(TASK_HINTS[index], x + 18f, y + height - 48f, width - 36f, 0.76f, TEXT_MUTED);

        drawPanel(x + 18f, y + 46f, gaugeWidth, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        drawPanel(targetX, y + 46f, targetWidth, 18f, TEXT_MINT);
        drawPanelOutline(x + 18f, y + 46f, gaugeWidth, 18f, BORDER_COLOR);
        drawPanel(markerX - 4f, y + 40f, 8f, 30f, HIGHLIGHT_COLOR);

        drawLine("현재 수치 " + Math.round(values[index]), x + 18f, y + 24f, 0.78f, TEXT_PRIMARY);
        drawLine(completed ? "고정 완료" : isWithinTarget(index) ? "확정 가능" : "조정 필요", x + 142f, y + 24f, 0.78f, completed ? TEXT_MINT : isWithinTarget(index) ? TEXT_ACCENT : TEXT_MUTED);
    }

    private void drawStatusPanel() {
        float left = PANEL_X + 22f;
        float top = PANEL_Y + PANEL_HEIGHT - 24f;
        float timeRatio = secondsRemaining / ACTIVE_SECONDS;
        float completedRatio = getConfirmedCount() / (float) TASK_TITLES.length;

        drawLine("책상 상태", left, top, 1.16f, TEXT_ACCENT);
        drawLine("단계 " + phase.name(), left, top - 34f, 0.92f, TEXT_PRIMARY);
        drawLine("최고 점수 " + progress.getBestScore(GameProgress.BROADCAST_DESK), left, top - 66f, 0.92f, TEXT_MINT);

        drawLine("남은 시간", left, top - 126f, 0.96f, TEXT_PRIMARY);
        drawPanel(left, top - 156f, PANEL_WIDTH - 52f, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        drawPanel(left, top - 156f, (PANEL_WIDTH - 52f) * timeRatio, 18f, HIGHLIGHT_COLOR);
        drawPanelOutline(left, top - 156f, PANEL_WIDTH - 52f, 18f, BORDER_COLOR);
        drawLine(String.format("%.1f초", secondsRemaining), left, top - 168f, 0.78f, TEXT_MUTED);

        drawLine("완료 진행", left, top - 214f, 0.96f, TEXT_PRIMARY);
        drawPanel(left, top - 244f, PANEL_WIDTH - 52f, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        drawPanel(left, top - 244f, (PANEL_WIDTH - 52f) * completedRatio, 18f, TEXT_MINT);
        drawPanelOutline(left, top - 244f, PANEL_WIDTH - 52f, 18f, BORDER_COLOR);
        drawLine(getConfirmedCount() + " / " + TASK_TITLES.length + " 항목", left, top - 256f, 0.78f, TEXT_MUTED);

        drawLine("선택 중", left, top - 314f, 0.96f, TEXT_PRIMARY);
        drawLine(TASK_TITLES[selectedIndex], left, top - 348f, 1.14f, TEXT_PRIMARY);
        drawParagraph(TASK_HINTS[selectedIndex], left, top - 382f, PANEL_WIDTH - 44f, 0.82f, TEXT_MUTED);

        float guideY = PANEL_Y + 166f;
        drawPanel(left - 2f, guideY, PANEL_WIDTH - 44f, 168f, PANEL_COLOR);
        drawPanelOutline(left - 2f, guideY, PANEL_WIDTH - 44f, 168f, BORDER_COLOR);
        drawLine("안내", left + 14f, guideY + 138f, 0.98f, TEXT_ACCENT);
        drawParagraph(feedback, left + 14f, guideY + 98f, PANEL_WIDTH - 74f, 0.84f, TEXT_PRIMARY);
        drawParagraph("UP / DOWN 으로 작업 선택, LEFT / RIGHT 로 수치 조정, SPACE 로 확정합니다.", left + 14f, guideY + 52f, PANEL_WIDTH - 74f, 0.80f, TEXT_MUTED);
        drawParagraph("결과 화면에서는 H 또는 ENTER로 저장, R로 재시작합니다.", left + 14f, guideY + 20f, PANEL_WIDTH - 74f, 0.80f, TEXT_MUTED);

        if (phase == Phase.RESULT) {
            float resultY = PANEL_Y + 74f;
            drawLine("결과 점수", left, resultY + 80f, 0.98f, TEXT_ACCENT);
            drawLine(String.valueOf(finalScore), left, resultY + 34f, 1.86f, TEXT_MINT);
        }
    }

    private void drawLiveHud() {
        float chipX = WINDOW_WIDTH - 246f;
        float chipY = WINDOW_HEIGHT - 116f;
        float chipWidth = 190f;
        float chipHeight = 84f;
        float feedbackX = STAGE_X + 28f;
        float feedbackY = STAGE_Y + 24f;
        float feedbackWidth = 560f;
        float feedbackHeight = 84f;

        drawPanel(chipX, chipY, chipWidth, chipHeight, PANEL_STRONG);
        drawPanelOutline(chipX, chipY, chipWidth, chipHeight, BORDER_COLOR);
        drawLine(String.format("%.1f초", secondsRemaining), chipX + 16f, chipY + 54f, 0.96f, TEXT_PRIMARY);
        drawLine(getConfirmedCount() + " / " + TASK_TITLES.length + " 정리", chipX + 16f, chipY + 24f, 0.82f, TEXT_MINT);

        drawPanel(feedbackX, feedbackY, feedbackWidth, feedbackHeight, PANEL_COLOR);
        drawPanelOutline(feedbackX, feedbackY, feedbackWidth, feedbackHeight, BORDER_COLOR);
        drawParagraph(feedback, feedbackX + 18f, feedbackY + 50f, feedbackWidth - 36f, 0.84f, TEXT_PRIMARY);
        drawLine(resolveLiveHint(), feedbackX + 18f, feedbackY + 20f, 0.76f, TEXT_MUTED);

        if (phase != Phase.RESULT) {
            return;
        }

        float resultWidth = 256f;
        float resultHeight = 118f;
        float resultX = STAGE_X + ((STAGE_WIDTH - resultWidth) * 0.5f);
        float resultY = STAGE_Y + 40f;

        drawPanel(resultX, resultY, resultWidth, resultHeight, PANEL_STRONG);
        drawPanelOutline(resultX, resultY, resultWidth, resultHeight, HIGHLIGHT_COLOR);
        drawLine("결과 점수", resultX + 22f, resultY + 86f, 0.92f, TEXT_ACCENT);
        drawLine(String.valueOf(finalScore), resultX + 22f, resultY + 36f, 1.90f, TEXT_MINT);
    }

    private void drawCommandBar() {
        drawLine(resolveCommandHint(), COMMAND_X + 22f, COMMAND_Y + 38f, 0.90f, TEXT_PRIMARY);
    }

    private String resolvePhaseDescription() {
        return switch (phase) {
            case READY -> "방송 시작 직전 책상이 아직 덜 맞춰져 있습니다. 마이크, 조명, 메모, 무드를 순서대로 정리하세요.";
            case ACTIVE -> "민트 구간에 맞춘 뒤 SPACE로 고정하면 됩니다. 네 항목을 빠르게 정리할수록 점수가 올라갑니다.";
            case RESULT -> "책상 정리가 끝났습니다. 저장하고 허브로 돌아가면 다음 챕터가 열립니다.";
        };
    }

    private String resolvePhaseHint() {
        return switch (phase) {
            case READY -> "SPACE 또는 ENTER로 시작";
            case ACTIVE -> "UP / DOWN 선택, LEFT / RIGHT 조정, SPACE 확정";
            case RESULT -> "H 또는 ENTER 저장, R 재시작, ESC 허브 복귀";
        };
    }

    private String resolveCommandHint() {
        return switch (phase) {
            case READY -> "SPACE / ENTER 시작 | ESC 허브 복귀";
            case ACTIVE -> "UP / DOWN 작업 선택 | LEFT / RIGHT 값 조정 | SPACE 확정 | ESC 허브 복귀";
            case RESULT -> "H / ENTER 저장 | R 재시작 | ESC 허브 복귀";
        };
    }

    private String resolveLiveHint() {
        return switch (phase) {
            case READY -> "SPACE 시작";
            case ACTIVE -> "UP DOWN 선택  LEFT RIGHT 조정  SPACE 확정";
            case RESULT -> "H 저장  R 재시작  ESC 복귀";
        };
    }

    private int getConfirmedCount() {
        int count = 0;
        for (boolean value : confirmed) {
            if (value) {
                count += 1;
            }
        }
        return count;
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
        return text.length() * 11.4f * scale;
    }

    private void drawLine(String text, float x, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        font.draw(batch, text, x, y);
        font.setColor(TEXT_PRIMARY);
        font.getData().setScale(1f);
    }

    private String buildFontCharacters() {
        Set<Character> characters = new LinkedHashSet<>();
        appendCharacters(characters, FreeTypeFontGenerator.DEFAULT_CHARS);

        for (String text : List.of(
                "방송 책상 정리",
                "책상 상태",
                "단계",
                "최고 점수",
                "남은 시간",
                "완료 진행",
                "선택 중",
                "안내",
                "결과 점수",
                "SPACE를 눌러 책상 정리를 시작하세요.",
                "SPACE를 눌러 책상 정리를 다시 시작하세요.",
                "첫 작업부터 맞춰 보자.",
                "좋아. 적어도 방송은 시작할 수 있겠다.",
                "시간이 다 됐다. 그래도 시작할 정도는 정리됐다.",
                "민트 구간에 맞춘 뒤 SPACE로 고정하면 됩니다. 네 항목을 빠르게 정리할수록 점수가 올라갑니다.",
                "방송 시작 직전 책상이 아직 덜 맞춰져 있습니다. 마이크, 조명, 메모, 무드를 순서대로 정리하세요.",
                "책상 정리가 끝났습니다. 저장하고 허브로 돌아가면 다음 챕터가 열립니다.",
                "SPACE 또는 ENTER로 시작",
                "UP / DOWN 선택, LEFT / RIGHT 조정, SPACE 확정",
                "H 또는 ENTER 저장, R 재시작, ESC 허브 복귀",
                "SPACE / ENTER 시작 | ESC 허브 복귀",
                "UP / DOWN 작업 선택 | LEFT / RIGHT 값 조정 | SPACE 확정 | ESC 허브 복귀",
                "H / ENTER 저장 | R 재시작 | ESC 허브 복귀",
                "SPACE 시작",
                "UP DOWN 선택  LEFT RIGHT 조정  SPACE 확정",
                "H 저장  R 재시작  ESC 복귀",
                "수치를 조정했습니다.",
                "은 이미 고정했습니다.",
                "은 이미 완료했습니다.",
                "이 아직 맞지 않습니다. 민트 구간에 맞춰 주세요.",
                "고정 완료",
                "확정 가능",
                "조정 필요",
                "항목",
                "방송 책상에서 허브로 복귀했습니다.",
                "UP / DOWN 으로 작업 선택, LEFT / RIGHT 로 수치 조정, SPACE 로 확정합니다.",
                "결과 화면에서는 H 또는 ENTER로 저장, R로 재시작합니다."
        )) {
            appendCharacters(characters, text);
        }

        for (String title : TASK_TITLES) {
            appendCharacters(characters, title);
        }
        for (String hint : TASK_HINTS) {
            appendCharacters(characters, hint);
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

    private boolean showsOperationalUi() {
        return game.getConfig().showsOperationalUi();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        pixelTexture.dispose();
        backgroundTexture.dispose();
        hostTexture.dispose();
    }

    private enum Phase {
        READY,
        ACTIVE,
        RESULT
    }
}
