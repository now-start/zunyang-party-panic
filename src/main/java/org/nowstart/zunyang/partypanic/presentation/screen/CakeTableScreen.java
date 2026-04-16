package org.nowstart.zunyang.partypanic.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.application.port.GameNavigator;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.minigame.CakeBalanceStateMachine;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;
import org.nowstart.zunyang.partypanic.presentation.support.ScreenSupport;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class CakeTableScreen extends ScreenAdapter {
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

    private static final Color TEXT_PRIMARY = new Color(0.97f, 0.93f, 0.85f, 1f);
    private static final Color TEXT_MUTED = new Color(0.90f, 0.84f, 0.80f, 1f);
    private static final Color TEXT_ACCENT = new Color(1.00f, 0.88f, 0.65f, 1f);
    private static final Color TEXT_MINT = new Color(0.73f, 0.93f, 0.87f, 1f);
    private static final Color HIGHLIGHT_COLOR = new Color(0.96f, 0.61f, 0.71f, 0.92f);
    private static final Color BORDER_COLOR = new Color(0.97f, 0.86f, 0.78f, 0.90f);
    private static final Color PANEL_COLOR = new Color(0.10f, 0.07f, 0.10f, 0.74f);
    private static final Color PANEL_STRONG = new Color(0.14f, 0.09f, 0.13f, 0.84f);
    private static final Color OVERLAY_COLOR = new Color(0.05f, 0.03f, 0.04f, 0.45f);
    private static final Color STAGE_FRAME = new Color(0.18f, 0.10f, 0.14f, 0.45f);

    private final GameNavigator navigator;
    private final GameProgress progress;
    private final CakeBalanceStateMachine stateMachine = new CakeBalanceStateMachine();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont bodyFont;
    private final BitmapFont titleFont;
    private final Texture pixelTexture;
    private final Texture backgroundTexture;
    private final Texture cakeTexture;
    private final Texture hostTexture;

    public CakeTableScreen(GameNavigator navigator, GameProgress progress) {
        this.navigator = navigator;
        this.progress = progress;
        this.bodyFont = ScreenSupport.createBodyFont(buildFontCharacters());
        this.titleFont = ScreenSupport.createTitleFont(buildFontCharacters());
        this.pixelTexture = ScreenSupport.createPixelTexture();
        this.backgroundTexture = ScreenSupport.loadTexture("assets/images/backgrounds/cake-rush-stage.png");
        this.cakeTexture = ScreenSupport.loadTexture("assets/images/events/cake-balance-card.png");
        this.hostTexture = ScreenSupport.loadTexture("assets/images/characters/zunyang-birthday-host.png");
    }

    @Override
    public void render(float delta) {
        if (!handleInput()) {
            return;
        }

        stateMachine.update(delta);

        ScreenUtils.clear(0.07f, 0.04f, 0.05f, 1f);

        batch.begin();
        drawBackdrop();
        drawFrames();
        drawCakeStage();
        if (showsOperationalUi()) {
            drawOperatorPanel();
            drawCommandBar();
        } else {
            drawLiveHud();
        }
        batch.end();
    }

    private boolean handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigator.showHub("케이크 테이블에서 허브로 복귀했습니다.");
            return false;
        }

        if (stateMachine.getPhase() == CakeBalanceStateMachine.Phase.READY && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            stateMachine.start();
            return true;
        }

        if (stateMachine.isActive()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                stateMachine.nudgeLeft();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                stateMachine.nudgeRight();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                stateMachine.stabilize();
            }
            return true;
        }

        if (stateMachine.isResult()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                stateMachine.restart();
                return true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                navigator.completeScoredActivity(ActivityId.CAKE_TABLE, stateMachine.getFinalScore());
                return false;
            }
        }
        return true;
    }

    private void drawBackdrop() {
        drawTextureCover(backgroundTexture, 0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT);
        drawPanel(0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT, OVERLAY_COLOR);
    }

    private void drawFrames() {
        drawPanel(STAGE_X - 8f, STAGE_Y - 8f, STAGE_WIDTH + 16f, STAGE_HEIGHT + 16f, STAGE_FRAME);
        drawTextureCover(backgroundTexture, STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT);
        drawPanel(STAGE_X, STAGE_Y, STAGE_WIDTH, STAGE_HEIGHT, new Color(0.03f, 0.02f, 0.03f, 0.18f));
        drawPanelOutline(STAGE_X - 1f, STAGE_Y - 1f, STAGE_WIDTH + 2f, STAGE_HEIGHT + 2f, BORDER_COLOR);

        if (showsOperationalUi()) {
            drawPanel(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, PANEL_STRONG);
            drawPanelOutline(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT, BORDER_COLOR);

            drawPanel(COMMAND_X, COMMAND_Y, COMMAND_WIDTH, COMMAND_HEIGHT, PANEL_STRONG);
            drawPanelOutline(COMMAND_X, COMMAND_Y, COMMAND_WIDTH, COMMAND_HEIGHT, BORDER_COLOR);
        }
    }

    private void drawCakeStage() {
        float titleX = STAGE_X + 24f;
        float titleY = STAGE_Y + STAGE_HEIGHT - 18f;
        float infoX = STAGE_X + 28f;
        float infoY = STAGE_Y + STAGE_HEIGHT - 148f;
        float infoWidth = 560f;
        float infoHeight = 104f;
        float cakeAreaX = STAGE_X + 154f;
        float cakeAreaY = STAGE_Y + 126f;
        float cakeAreaWidth = 430f;
        float cakeAreaHeight = 360f;
        float hostHeight = 404f;
        float hostWidth = hostHeight * (hostTexture.getWidth() / (float) hostTexture.getHeight());
        float hostX = STAGE_X + STAGE_WIDTH - hostWidth - 34f;
        float hostY = STAGE_Y + 36f;

        drawTitleLine("케이크 테이블 미니게임", titleX, titleY, 1.22f, TEXT_ACCENT);
        drawPanel(infoX, infoY, infoWidth, infoHeight, PANEL_COLOR);
        drawPanelOutline(infoX, infoY, infoWidth, infoHeight, BORDER_COLOR);
        drawParagraph(resolvePhaseDescription(), infoX + 18f, infoY + 58f, infoWidth - 36f, 0.96f, TEXT_PRIMARY);
        drawParagraph(resolvePhaseHint(), infoX + 18f, infoY + 24f, infoWidth - 36f, 0.84f, TEXT_MUTED);

        drawPanel(cakeAreaX - 16f, cakeAreaY - 16f, cakeAreaWidth + 32f, cakeAreaHeight + 32f, new Color(0.12f, 0.08f, 0.11f, 0.76f));
        drawPanel(cakeAreaX, cakeAreaY, cakeAreaWidth, cakeAreaHeight, new Color(0.08f, 0.05f, 0.07f, 0.54f));
        drawPanelOutline(cakeAreaX, cakeAreaY, cakeAreaWidth, cakeAreaHeight, BORDER_COLOR);
        drawBalanceBoard(cakeAreaX, cakeAreaY, cakeAreaWidth, cakeAreaHeight);

        drawPanel(hostX - 16f, hostY - 14f, hostWidth + 32f, hostHeight + 28f, new Color(0.14f, 0.08f, 0.12f, 0.36f));
        drawTextureFit(hostTexture, hostX, hostY, hostWidth, hostHeight);
    }

    private void drawBalanceBoard(float x, float y, float width, float height) {
        float boardCenterX = x + (width * 0.5f);
        float boardCenterY = y + (height * 0.45f);
        float tiltRatio = stateMachine.getBalance() / 100f;
        float boardOffset = tiltRatio * 108f;
        float cakeWidth = 220f;
        float cakeHeight = 220f;
        float cakeX = boardCenterX - (cakeWidth * 0.5f) + boardOffset;
        float cakeY = boardCenterY - 34f + Math.abs(tiltRatio) * 18f;
        float pivotWidth = 220f;

        drawPanel(boardCenterX - (pivotWidth * 0.5f), boardCenterY - 4f, pivotWidth, 8f, new Color(0.91f, 0.76f, 0.62f, 0.90f));
        drawPanel(boardCenterX - 12f, boardCenterY - 64f, 24f, 64f, new Color(0.78f, 0.58f, 0.46f, 0.90f));
        drawPanel(cakeX - 8f, cakeY - 8f, cakeWidth + 16f, cakeHeight + 16f, new Color(0.16f, 0.10f, 0.12f, 0.60f));
        drawTextureFit(cakeTexture, cakeX, cakeY, cakeWidth, cakeHeight);
        drawPanelOutline(cakeX, cakeY, cakeWidth, cakeHeight, Math.abs(tiltRatio) < 0.25f ? TEXT_MINT : HIGHLIGHT_COLOR);

        float gaugeX = x + 52f;
        float gaugeY = y + height - 66f;
        float gaugeWidth = width - 104f;
        float markerX = gaugeX + (gaugeWidth * ((tiltRatio + 1f) * 0.5f));

        drawLine("기울기", gaugeX, gaugeY + 42f, 0.92f, TEXT_PRIMARY);
        drawPanel(gaugeX, gaugeY + 16f, gaugeWidth, 16f, new Color(0.24f, 0.16f, 0.18f, 0.90f));
        drawPanel(gaugeX + (gaugeWidth * 0.4f), gaugeY + 16f, gaugeWidth * 0.2f, 16f, TEXT_MINT);
        drawPanel(markerX - 4f, gaugeY + 12f, 8f, 24f, HIGHLIGHT_COLOR);
        drawPanelOutline(gaugeX, gaugeY + 16f, gaugeWidth, 16f, BORDER_COLOR);

        drawLine(String.format("좌 %.0f  |  우 %.0f", Math.max(0f, -stateMachine.getBalance()), Math.max(0f, stateMachine.getBalance())), gaugeX, gaugeY - 2f, 0.82f, TEXT_MUTED);
    }

    private void drawOperatorPanel() {
        float left = PANEL_X + 22f;
        float top = PANEL_Y + PANEL_HEIGHT - 24f;
        float stabilityRatio = stateMachine.getStability() / 100f;
        float timeRatio = stateMachine.getSecondsRemaining() / CakeBalanceStateMachine.ACTIVE_SECONDS;

        drawLine("케이크 상태", left, top, 1.16f, TEXT_ACCENT);
        drawLine("단계 " + stateMachine.getPhase().name(), left, top - 34f, 0.92f, TEXT_PRIMARY);
        drawLine("최고 점수 " + progress.getBestScore(ActivityId.CAKE_TABLE), left, top - 66f, 0.92f, TEXT_MINT);

        drawLine("안정도", left, top - 126f, 0.96f, TEXT_PRIMARY);
        drawPanel(left, top - 156f, PANEL_WIDTH - 52f, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        drawPanel(left, top - 156f, (PANEL_WIDTH - 52f) * stabilityRatio, 18f, TEXT_MINT);
        drawPanelOutline(left, top - 156f, PANEL_WIDTH - 52f, 18f, BORDER_COLOR);
        drawLine(String.format("%.0f / 100", stateMachine.getStability()), left, top - 168f, 0.78f, TEXT_MUTED);

        drawLine("남은 시간", left, top - 214f, 0.96f, TEXT_PRIMARY);
        drawPanel(left, top - 244f, PANEL_WIDTH - 52f, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        drawPanel(left, top - 244f, (PANEL_WIDTH - 52f) * timeRatio, 18f, HIGHLIGHT_COLOR);
        drawPanelOutline(left, top - 244f, PANEL_WIDTH - 52f, 18f, BORDER_COLOR);
        drawLine(String.format("%.1f초", stateMachine.getSecondsRemaining()), left, top - 256f, 0.78f, TEXT_MUTED);

        drawLine("복구 횟수 " + stateMachine.getRecoveryCount(), left, top - 310f, 0.92f, TEXT_PRIMARY);

        float cardY = PANEL_Y + 184f;
        drawPanel(left - 2f, cardY, PANEL_WIDTH - 44f, 154f, PANEL_COLOR);
        drawPanelOutline(left - 2f, cardY, PANEL_WIDTH - 44f, 154f, BORDER_COLOR);
        drawLine("조작", left + 14f, cardY + 126f, 0.98f, TEXT_ACCENT);
        drawParagraph("LEFT/A 또는 RIGHT/D 로 케이크를 바로잡고, S 또는 아래 방향키로 급한 흔들림을 눌러 주세요.", left + 14f, cardY + 86f, PANEL_WIDTH - 74f, 0.86f, TEXT_PRIMARY);
        drawParagraph("결과 화면에서 H를 누르면 점수를 저장하고 허브로 돌아갑니다.", left + 14f, cardY + 34f, PANEL_WIDTH - 74f, 0.82f, TEXT_MUTED);

        if (stateMachine.isResult()) {
            float resultY = PANEL_Y + 74f;
            drawLine("결과 점수", left, resultY + 84f, 0.98f, TEXT_ACCENT);
            drawLine(String.valueOf(stateMachine.getFinalScore()), left, resultY + 38f, 1.84f, TEXT_MINT);
        }
    }

    private void drawLiveHud() {
        float chipX = WINDOW_WIDTH - 250f;
        float chipY = WINDOW_HEIGHT - 126f;
        float chipWidth = 194f;
        float chipHeight = 92f;
        float guideX = STAGE_X + 28f;
        float guideY = STAGE_Y + 24f;
        float guideWidth = 560f;
        float guideHeight = 84f;

        drawPanel(chipX, chipY, chipWidth, chipHeight, PANEL_STRONG);
        drawPanelOutline(chipX, chipY, chipWidth, chipHeight, BORDER_COLOR);
        drawLine(String.format("안정 %.0f", stateMachine.getStability()), chipX + 16f, chipY + 58f, 0.92f, TEXT_PRIMARY);
        drawLine(String.format("시간 %.1f초", stateMachine.getSecondsRemaining()), chipX + 16f, chipY + 28f, 0.82f, TEXT_MINT);

        drawPanel(guideX, guideY, guideWidth, guideHeight, PANEL_COLOR);
        drawPanelOutline(guideX, guideY, guideWidth, guideHeight, BORDER_COLOR);
        drawParagraph(resolvePhaseDescription(), guideX + 18f, guideY + 50f, guideWidth - 36f, 0.82f, TEXT_PRIMARY);
        drawLine(resolveLiveHint(), guideX + 18f, guideY + 20f, 0.76f, TEXT_MUTED);

        if (!stateMachine.isResult()) {
            return;
        }

        float resultWidth = 240f;
        float resultHeight = 118f;
        float resultX = STAGE_X + ((STAGE_WIDTH - resultWidth) * 0.5f);
        float resultY = STAGE_Y + 36f;
        drawPanel(resultX, resultY, resultWidth, resultHeight, PANEL_STRONG);
        drawPanelOutline(resultX, resultY, resultWidth, resultHeight, HIGHLIGHT_COLOR);
        drawLine("결과 점수", resultX + 22f, resultY + 84f, 0.92f, TEXT_ACCENT);
        drawLine(String.valueOf(stateMachine.getFinalScore()), resultX + 22f, resultY + 34f, 1.86f, TEXT_MINT);
    }

    private void drawCommandBar() {
        drawLine(resolveCommandHint(), COMMAND_X + 22f, COMMAND_Y + 38f, 0.92f, TEXT_PRIMARY);
    }

    private String resolvePhaseDescription() {
        return switch (stateMachine.getPhase()) {
            case READY -> "치즈냥이 케이크 테이블 위 마지막 장식을 올리려 합니다. 시작하면 기울기와 안정도가 동시에 움직입니다.";
            case ACTIVE -> "케이크가 한쪽으로 기울면 안정도가 빠르게 깎입니다. 중앙 구간을 오래 유지해 복구 횟수를 챙기세요.";
            case RESULT -> "테이블 정리가 끝났습니다. 점수를 저장하고 허브로 돌아가면 준비 진행도에 바로 반영됩니다.";
        };
    }

    private String resolvePhaseHint() {
        return switch (stateMachine.getPhase()) {
            case READY -> "SPACE로 시작";
            case ACTIVE -> "LEFT/A, RIGHT/D, S/아래 방향키 사용";
            case RESULT -> "H 저장 후 허브 복귀, R 재시작, ESC 허브 복귀";
        };
    }

    private String resolveCommandHint() {
        return switch (stateMachine.getPhase()) {
            case READY -> "현재 입력: SPACE 로 시작, ESC 로 허브 복귀";
            case ACTIVE -> "현재 입력: LEFT/A, RIGHT/D 로 조정, S/아래 방향키로 급정리, ESC 로 허브 복귀";
            case RESULT -> "현재 입력: H 로 저장 후 허브 복귀, R 또는 SPACE 로 다시 시작, ESC 로 허브 복귀";
        };
    }

    private String resolveLiveHint() {
        return switch (stateMachine.getPhase()) {
            case READY -> "SPACE 시작";
            case ACTIVE -> "LEFT RIGHT 균형  S 급정리";
            case RESULT -> "H 저장  R 재시작  ESC 복귀";
        };
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
        return text.length() * 11.2f * scale;
    }

    private void drawLine(String text, float x, float y, float scale, Color color) {
        drawText(bodyFont, text, x, y, scale, color);
    }

    private void drawTitleLine(String text, float x, float y, float scale, Color color) {
        drawText(titleFont, text, x, y, scale, color);
    }

    private void drawText(BitmapFont font, String text, float x, float y, float scale, Color color) {
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
                "케이크 테이블 미니게임",
                "치즈냥이 케이크 테이블 위 마지막 장식을 올리려 합니다. 시작하면 기울기와 안정도가 동시에 움직입니다.",
                "케이크가 한쪽으로 기울면 안정도가 빠르게 깎입니다. 중앙 구간을 오래 유지해 복구 횟수를 챙기세요.",
                "테이블 정리가 끝났습니다. 점수를 저장하고 허브로 돌아가면 준비 진행도에 바로 반영됩니다.",
                "SPACE로 시작",
                "LEFT/A, RIGHT/D, S/아래 방향키 사용",
                "H 저장 후 허브 복귀, R 재시작, ESC 허브 복귀",
                "케이크 상태",
                "단계 ",
                "최고 점수 ",
                "안정도",
                "남은 시간",
                "복구 횟수 ",
                "조작",
                "LEFT/A 또는 RIGHT/D 로 케이크를 바로잡고, S 또는 아래 방향키로 급한 흔들림을 눌러 주세요.",
                "결과 화면에서 H를 누르면 점수를 저장하고 허브로 돌아갑니다.",
                "결과 점수",
                "현재 입력: SPACE 로 시작, ESC 로 허브 복귀",
                "현재 입력: LEFT/A, RIGHT/D 로 조정, S/아래 방향키로 급정리, ESC 로 허브 복귀",
                "현재 입력: H 로 저장 후 허브 복귀, R 또는 SPACE 로 다시 시작, ESC 로 허브 복귀",
                "SPACE 시작",
                "LEFT RIGHT 균형  S 급정리",
                "H 저장  R 재시작  ESC 복귀",
                "안정 ",
                "시간 ",
                "기울기",
                "좌 %.0f  |  우 %.0f",
                "케이크 테이블에서 허브로 복귀했습니다.",
                "점",
                "초",
                "READY",
                "ACTIVE",
                "RESULT"
        )) {
            appendCharacters(characters, text);
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
        return navigator.showsOperationalUi();
    }

    @Override
    public void dispose() {
        batch.dispose();
        bodyFont.dispose();
        titleFont.dispose();
        pixelTexture.dispose();
        backgroundTexture.dispose();
        cakeTexture.dispose();
        hostTexture.dispose();
    }
}
