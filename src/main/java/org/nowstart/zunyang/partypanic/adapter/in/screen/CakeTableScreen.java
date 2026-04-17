package org.nowstart.zunyang.partypanic.adapter.in.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.minigame.CakeBalanceStateMachine;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.MiniGameLayout;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.MiniGamePalette;
import org.nowstart.zunyang.partypanic.adapter.in.support.ScreenSupport;
import org.nowstart.zunyang.partypanic.application.port.out.GameNavigator;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class CakeTableScreen extends AbstractMiniGameScreen {
    private final CakeBalanceStateMachine stateMachine = new CakeBalanceStateMachine();
    private final Texture cakeTexture;
    private final Texture hostTexture;

    public CakeTableScreen(GameNavigator navigator, GameProgress progress) {
        super(navigator, progress);
        initializeUi("assets/images/backgrounds/cake-rush-stage.png", buildFontCharacters());
        this.cakeTexture = ScreenSupport.loadTexture("assets/images/events/cake-balance-card.png");
        this.hostTexture = ScreenSupport.loadTexture("assets/images/characters/zunyang-birthday-host.png");
    }

    @Override
    protected boolean handleInput() {
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

    @Override
    protected void updateState(float delta) {
        stateMachine.update(delta);
    }

    @Override
    protected void drawMiniGameStage() {
        float titleX = MiniGameLayout.STAGE_X + 24f;
        float titleY = MiniGameLayout.STAGE_Y + MiniGameLayout.STAGE_HEIGHT - 18f;
        float infoX = MiniGameLayout.STAGE_X + 28f;
        float infoY = MiniGameLayout.STAGE_Y + MiniGameLayout.STAGE_HEIGHT - 148f;
        float infoWidth = 560f;
        float infoHeight = 104f;
        float cakeAreaX = MiniGameLayout.STAGE_X + 154f;
        float cakeAreaY = MiniGameLayout.STAGE_Y + 126f;
        float cakeAreaWidth = 430f;
        float cakeAreaHeight = 360f;
        float hostHeight = 404f;
        float hostWidth = hostHeight * (hostTexture.getWidth() / (float) hostTexture.getHeight());
        float hostX = MiniGameLayout.STAGE_X + MiniGameLayout.STAGE_WIDTH - hostWidth - 34f;
        float hostY = MiniGameLayout.STAGE_Y + 36f;

        drawTitleLine("케이크 테이블 미니게임", titleX, titleY, 1.22f, MiniGamePalette.TEXT_ACCENT);
        drawPanel(infoX, infoY, infoWidth, infoHeight, MiniGamePalette.PANEL_COLOR);
        drawPanelOutline(infoX, infoY, infoWidth, infoHeight, MiniGamePalette.BORDER_COLOR);
        drawParagraph(resolvePhaseDescription(), infoX + 18f, infoY + 58f, infoWidth - 36f, 0.96f, MiniGamePalette.TEXT_PRIMARY);
        drawParagraph(resolvePhaseHint(), infoX + 18f, infoY + 24f, infoWidth - 36f, 0.84f, MiniGamePalette.TEXT_MUTED);

        drawPanel(cakeAreaX - 16f, cakeAreaY - 16f, cakeAreaWidth + 32f, cakeAreaHeight + 32f, new Color(0.12f, 0.08f, 0.11f, 0.76f));
        drawPanel(cakeAreaX, cakeAreaY, cakeAreaWidth, cakeAreaHeight, new Color(0.08f, 0.05f, 0.07f, 0.54f));
        drawPanelOutline(cakeAreaX, cakeAreaY, cakeAreaWidth, cakeAreaHeight, MiniGamePalette.BORDER_COLOR);
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
        drawPanelOutline(cakeX, cakeY, cakeWidth, cakeHeight, Math.abs(tiltRatio) < 0.25f ? MiniGamePalette.TEXT_MINT : MiniGamePalette.HIGHLIGHT_COLOR);

        float gaugeX = x + 52f;
        float gaugeY = y + height - 66f;
        float gaugeWidth = width - 104f;
        float markerX = gaugeX + (gaugeWidth * ((tiltRatio + 1f) * 0.5f));

        drawLine("기울기", gaugeX, gaugeY + 42f, 0.92f, MiniGamePalette.TEXT_PRIMARY);
        drawPanel(gaugeX, gaugeY + 16f, gaugeWidth, 16f, new Color(0.24f, 0.16f, 0.18f, 0.90f));
        drawPanel(gaugeX + (gaugeWidth * 0.4f), gaugeY + 16f, gaugeWidth * 0.2f, 16f, MiniGamePalette.TEXT_MINT);
        drawPanel(markerX - 4f, gaugeY + 12f, 8f, 24f, MiniGamePalette.HIGHLIGHT_COLOR);
        drawPanelOutline(gaugeX, gaugeY + 16f, gaugeWidth, 16f, MiniGamePalette.BORDER_COLOR);

        drawLine(String.format("좌 %.0f  |  우 %.0f", Math.max(0f, -stateMachine.getBalance()), Math.max(0f, stateMachine.getBalance())), gaugeX, gaugeY - 2f, 0.82f, MiniGamePalette.TEXT_MUTED);
    }

    @Override
    protected void drawOperationalUi() {
        float left = MiniGameLayout.PANEL_X + 22f;
        float top = MiniGameLayout.PANEL_Y + MiniGameLayout.PANEL_HEIGHT - 24f;
        float stabilityRatio = stateMachine.getStability() / 100f;
        float timeRatio = stateMachine.getSecondsRemaining() / CakeBalanceStateMachine.ACTIVE_SECONDS;

        drawLine("케이크 상태", left, top, 1.16f, MiniGamePalette.TEXT_ACCENT);
        drawLine("단계 " + stateMachine.getPhase().name(), left, top - 34f, 0.92f, MiniGamePalette.TEXT_PRIMARY);
        drawLine("최고 점수 " + progress.getBestScore(ActivityId.CAKE_TABLE), left, top - 66f, 0.92f, MiniGamePalette.TEXT_MINT);

        drawLine("안정도", left, top - 126f, 0.96f, MiniGamePalette.TEXT_PRIMARY);
        drawPanel(left, top - 156f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        drawPanel(left, top - 156f, (MiniGameLayout.PANEL_WIDTH - 52f) * stabilityRatio, 18f, MiniGamePalette.TEXT_MINT);
        drawPanelOutline(left, top - 156f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, MiniGamePalette.BORDER_COLOR);
        drawLine(String.format("%.0f / 100", stateMachine.getStability()), left, top - 168f, 0.78f, MiniGamePalette.TEXT_MUTED);

        drawLine("남은 시간", left, top - 214f, 0.96f, MiniGamePalette.TEXT_PRIMARY);
        drawPanel(left, top - 244f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        drawPanel(left, top - 244f, (MiniGameLayout.PANEL_WIDTH - 52f) * timeRatio, 18f, MiniGamePalette.HIGHLIGHT_COLOR);
        drawPanelOutline(left, top - 244f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, MiniGamePalette.BORDER_COLOR);
        drawLine(String.format("%.1f초", stateMachine.getSecondsRemaining()), left, top - 256f, 0.78f, MiniGamePalette.TEXT_MUTED);

        drawLine("복구 횟수 " + stateMachine.getRecoveryCount(), left, top - 310f, 0.92f, MiniGamePalette.TEXT_PRIMARY);

        float cardY = MiniGameLayout.PANEL_Y + 184f;
        drawPanel(left - 2f, cardY, MiniGameLayout.PANEL_WIDTH - 44f, 154f, MiniGamePalette.PANEL_COLOR);
        drawPanelOutline(left - 2f, cardY, MiniGameLayout.PANEL_WIDTH - 44f, 154f, MiniGamePalette.BORDER_COLOR);
        drawLine("조작", left + 14f, cardY + 126f, 0.98f, MiniGamePalette.TEXT_ACCENT);
        drawParagraph("LEFT/A 또는 RIGHT/D 로 케이크를 바로잡고, S 또는 아래 방향키로 급한 흔들림을 눌러 주세요.", left + 14f, cardY + 86f, MiniGameLayout.PANEL_WIDTH - 74f, 0.86f, MiniGamePalette.TEXT_PRIMARY);
        drawParagraph("결과 화면에서 H를 누르면 점수를 저장하고 허브로 돌아갑니다.", left + 14f, cardY + 34f, MiniGameLayout.PANEL_WIDTH - 74f, 0.82f, MiniGamePalette.TEXT_MUTED);

        if (stateMachine.isResult()) {
            float resultY = MiniGameLayout.PANEL_Y + 74f;
            drawLine("결과 점수", left, resultY + 84f, 0.98f, MiniGamePalette.TEXT_ACCENT);
            drawLine(String.valueOf(stateMachine.getFinalScore()), left, resultY + 38f, 1.84f, MiniGamePalette.TEXT_MINT);
        }
    }

    @Override
    protected void drawLiveHud() {
        float chipX = MiniGameLayout.WINDOW_WIDTH - 250f;
        float chipY = MiniGameLayout.WINDOW_HEIGHT - 126f;
        float chipWidth = 194f;
        float chipHeight = 92f;
        float guideX = MiniGameLayout.STAGE_X + 28f;
        float guideY = MiniGameLayout.STAGE_Y + 24f;
        float guideWidth = 560f;
        float guideHeight = 84f;

        drawPanel(chipX, chipY, chipWidth, chipHeight, MiniGamePalette.PANEL_STRONG);
        drawPanelOutline(chipX, chipY, chipWidth, chipHeight, MiniGamePalette.BORDER_COLOR);
        drawLine(String.format("안정 %.0f", stateMachine.getStability()), chipX + 16f, chipY + 58f, 0.92f, MiniGamePalette.TEXT_PRIMARY);
        drawLine(String.format("시간 %.1f초", stateMachine.getSecondsRemaining()), chipX + 16f, chipY + 28f, 0.82f, MiniGamePalette.TEXT_MINT);

        drawPanel(guideX, guideY, guideWidth, guideHeight, MiniGamePalette.PANEL_COLOR);
        drawPanelOutline(guideX, guideY, guideWidth, guideHeight, MiniGamePalette.BORDER_COLOR);
        drawParagraph(resolvePhaseDescription(), guideX + 18f, guideY + 50f, guideWidth - 36f, 0.82f, MiniGamePalette.TEXT_PRIMARY);
        drawLine(resolveLiveHint(), guideX + 18f, guideY + 20f, 0.76f, MiniGamePalette.TEXT_MUTED);

        if (!stateMachine.isResult()) {
            return;
        }

        float resultWidth = 240f;
        float resultHeight = 118f;
        float resultX = MiniGameLayout.STAGE_X + ((MiniGameLayout.STAGE_WIDTH - resultWidth) * 0.5f);
        float resultY = MiniGameLayout.STAGE_Y + 36f;
        drawPanel(resultX, resultY, resultWidth, resultHeight, MiniGamePalette.PANEL_STRONG);
        drawPanelOutline(resultX, resultY, resultWidth, resultHeight, MiniGamePalette.HIGHLIGHT_COLOR);
        drawLine("결과 점수", resultX + 22f, resultY + 84f, 0.92f, MiniGamePalette.TEXT_ACCENT);
        drawLine(String.valueOf(stateMachine.getFinalScore()), resultX + 22f, resultY + 34f, 1.86f, MiniGamePalette.TEXT_MINT);
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

    @Override
    protected String commandHint() {
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

    @Override
    protected void disposeResources() {
        cakeTexture.dispose();
        hostTexture.dispose();
    }
}
