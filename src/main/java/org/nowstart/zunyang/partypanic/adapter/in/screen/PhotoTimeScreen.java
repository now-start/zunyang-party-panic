package org.nowstart.zunyang.partypanic.adapter.in.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.MiniGameLayout;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.MiniGamePalette;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameAssets;
import org.nowstart.zunyang.partypanic.application.port.out.GameNavigator;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.minigame.PhotoTimeStateMachine;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

public final class PhotoTimeScreen extends AbstractMiniGameScreen {
    private final PhotoTimeStateMachine stateMachine = new PhotoTimeStateMachine();
    private final Texture cardTexture;
    private final Texture hostTexture;

    public PhotoTimeScreen(GameNavigator navigator, GameProgress progress, GameAssets assets) {
        super(navigator, progress, assets);
        initializeUi("assets/images/backgrounds/mint-cats-stage.png");
        this.cardTexture = assets.photoCardTexture();
        this.hostTexture = assets.hostTexture();
    }

    @Override
    protected boolean handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigator.showHub("포토존에서 허브로 복귀했습니다.");
            return false;
        }

        if (stateMachine.getPhase() == PhotoTimeStateMachine.Phase.READY && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            stateMachine.start();
            return true;
        }

        if (stateMachine.isActive()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                stateMachine.moveLeft();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                stateMachine.moveRight();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                stateMachine.moveUp();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                stateMachine.moveDown();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                stateMachine.capture();
            }
            return true;
        }

        if (stateMachine.isResult()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                stateMachine.restart();
                return true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                navigator.completeScoredActivity(ActivityId.PHOTO_TIME, stateMachine.getFinalScore());
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
        float infoY = MiniGameLayout.STAGE_Y + MiniGameLayout.STAGE_HEIGHT - 146f;
        float infoWidth = 590f;
        float infoHeight = 104f;
        float viewX = MiniGameLayout.STAGE_X + 96f;
        float viewY = MiniGameLayout.STAGE_Y + 120f;
        float viewWidth = 700f;
        float viewHeight = 404f;
        float hostHeight = 370f;
        float hostWidth = hostHeight * (hostTexture.getWidth() / (float) hostTexture.getHeight());
        float hostX = viewX + 318f + (stateMachine.getTargetX() * 66f);
        float hostY = viewY + 20f + (stateMachine.getTargetY() * 40f);

        drawTitleLine("포토존 카메라 미니게임", titleX, titleY, 1.20f, MiniGamePalette.TEXT_ACCENT);
        drawPanel(infoX, infoY, infoWidth, infoHeight, MiniGamePalette.PANEL_COLOR);
        drawPanelOutline(infoX, infoY, infoWidth, infoHeight, MiniGamePalette.BORDER_COLOR);
        drawParagraph(resolvePhaseDescription(), infoX + 18f, infoY + 58f, infoWidth - 36f, 0.96f, MiniGamePalette.TEXT_PRIMARY);
        drawParagraph(resolvePhaseHint(), infoX + 18f, infoY + 24f, infoWidth - 36f, 0.84f, MiniGamePalette.TEXT_MUTED);

        drawPanel(viewX - 16f, viewY - 16f, viewWidth + 32f, viewHeight + 32f, new Color(0.12f, 0.08f, 0.11f, 0.76f));
        drawPanel(viewX, viewY, viewWidth, viewHeight, new Color(0.06f, 0.04f, 0.05f, 0.46f));
        drawPanelOutline(viewX, viewY, viewWidth, viewHeight, MiniGamePalette.BORDER_COLOR);

        drawTextureFit(hostTexture, hostX, hostY, hostWidth, hostHeight);
        drawTargetFrame(viewX, viewY, viewWidth, viewHeight);
        drawCameraFrame(viewX, viewY, viewWidth, viewHeight);

        float cardSize = 170f;
        float cardX = MiniGameLayout.STAGE_X + MiniGameLayout.STAGE_WIDTH - cardSize - 42f;
        float cardY = MiniGameLayout.STAGE_Y + 192f;
        drawPanel(cardX - 12f, cardY - 12f, cardSize + 24f, cardSize + 24f, new Color(0.14f, 0.08f, 0.12f, 0.72f));
        drawTextureFit(cardTexture, cardX, cardY, cardSize, cardSize);
        drawPanelOutline(cardX, cardY, cardSize, cardSize, MiniGamePalette.BORDER_COLOR);
        drawLine("이번 포토 무드", cardX, cardY - 10f, 0.82f, MiniGamePalette.TEXT_MUTED);
    }

    private void drawTargetFrame(float viewX, float viewY, float viewWidth, float viewHeight) {
        float centerX = viewX + (viewWidth * 0.5f) + (stateMachine.getTargetX() * 240f);
        float centerY = viewY + (viewHeight * 0.5f) + (stateMachine.getTargetY() * 138f);
        float width = 148f;
        float height = 184f;
        float x = centerX - (width * 0.5f);
        float y = centerY - (height * 0.5f);

        drawPanelOutline(x, y, width, height, MiniGamePalette.TEXT_MINT);
        drawPanel(centerX - 2f, y - 12f, 4f, height + 24f, new Color(MiniGamePalette.TEXT_MINT.r, MiniGamePalette.TEXT_MINT.g, MiniGamePalette.TEXT_MINT.b, 0.55f));
        drawPanel(x - 12f, centerY - 2f, width + 24f, 4f, new Color(MiniGamePalette.TEXT_MINT.r, MiniGamePalette.TEXT_MINT.g, MiniGamePalette.TEXT_MINT.b, 0.55f));
    }

    private void drawCameraFrame(float viewX, float viewY, float viewWidth, float viewHeight) {
        float centerX = viewX + (viewWidth * 0.5f) + (stateMachine.getFrameX() * 240f);
        float centerY = viewY + (viewHeight * 0.5f) + (stateMachine.getFrameY() * 138f);
        float width = 180f;
        float height = 220f;
        float x = centerX - (width * 0.5f);
        float y = centerY - (height * 0.5f);

        drawPanelOutline(x, y, width, height, MiniGamePalette.HIGHLIGHT_COLOR);
        drawPanel(centerX - 2f, y + 18f, 4f, height - 36f, MiniGamePalette.HIGHLIGHT_COLOR);
        drawPanel(x + 18f, centerY - 2f, width - 36f, 4f, MiniGamePalette.HIGHLIGHT_COLOR);
    }

    @Override
    protected void drawOperationalUi() {
        float left = MiniGameLayout.PANEL_X + 22f;
        float top = MiniGameLayout.PANEL_Y + MiniGameLayout.PANEL_HEIGHT - 24f;
        float timeRatio = stateMachine.getSecondsRemaining() / PhotoTimeStateMachine.ACTIVE_SECONDS;
        float shotRatio = stateMachine.getCapturedShots() / (float) PhotoTimeStateMachine.TOTAL_SHOTS;

        drawLine("포토존 상태", left, top, 1.16f, MiniGamePalette.TEXT_ACCENT);
        drawLine("단계 " + stateMachine.getPhase().name(), left, top - 34f, 0.92f, MiniGamePalette.TEXT_PRIMARY);
        drawLine("최고 점수 " + progress.getBestScore(ActivityId.PHOTO_TIME), left, top - 66f, 0.92f, MiniGamePalette.TEXT_MINT);

        drawLine("남은 시간", left, top - 126f, 0.96f, MiniGamePalette.TEXT_PRIMARY);
        drawPanel(left, top - 156f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        drawPanel(left, top - 156f, (MiniGameLayout.PANEL_WIDTH - 52f) * timeRatio, 18f, MiniGamePalette.TEXT_BLUE);
        drawPanelOutline(left, top - 156f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, MiniGamePalette.BORDER_COLOR);
        drawLine(String.format("%.1f초", stateMachine.getSecondsRemaining()), left, top - 168f, 0.78f, MiniGamePalette.TEXT_MUTED);

        drawLine("촬영 진행", left, top - 214f, 0.96f, MiniGamePalette.TEXT_PRIMARY);
        drawPanel(left, top - 244f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        drawPanel(left, top - 244f, (MiniGameLayout.PANEL_WIDTH - 52f) * shotRatio, 18f, MiniGamePalette.HIGHLIGHT_COLOR);
        drawPanelOutline(left, top - 244f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, MiniGamePalette.BORDER_COLOR);
        drawLine(stateMachine.getCapturedShots() + " / " + PhotoTimeStateMachine.TOTAL_SHOTS + " 컷", left, top - 256f, 0.78f, MiniGamePalette.TEXT_MUTED);

        drawLine("마지막 판정 " + stateMachine.getLastJudgement(), left, top - 312f, 0.92f, MiniGamePalette.TEXT_PRIMARY);
        drawLine("최근 점수 " + stateMachine.getLastShotScore(), left, top - 344f, 0.90f, MiniGamePalette.TEXT_MINT);

        float guideY = MiniGameLayout.PANEL_Y + 194f;
        drawPanel(left - 2f, guideY, MiniGameLayout.PANEL_WIDTH - 44f, 150f, MiniGamePalette.PANEL_COLOR);
        drawPanelOutline(left - 2f, guideY, MiniGameLayout.PANEL_WIDTH - 44f, 150f, MiniGamePalette.BORDER_COLOR);
        drawLine("조작", left + 14f, guideY + 120f, 0.98f, MiniGamePalette.TEXT_ACCENT);
        drawParagraph("방향키 또는 WASD로 카메라 프레임을 움직이고 SPACE로 셔터를 끊으세요. 민트 프레임과 분홍 프레임을 겹칠수록 점수가 높습니다.", left + 14f, guideY + 80f, MiniGameLayout.PANEL_WIDTH - 74f, 0.84f, MiniGamePalette.TEXT_PRIMARY);
        drawParagraph("결과 화면에서 H를 누르면 허브에 점수를 저장합니다.", left + 14f, guideY + 30f, MiniGameLayout.PANEL_WIDTH - 74f, 0.82f, MiniGamePalette.TEXT_MUTED);

        if (stateMachine.isResult()) {
            float resultY = MiniGameLayout.PANEL_Y + 82f;
            drawLine("결과 점수", left, resultY + 82f, 0.98f, MiniGamePalette.TEXT_ACCENT);
            drawLine(String.valueOf(stateMachine.getFinalScore()), left, resultY + 34f, 1.84f, MiniGamePalette.TEXT_MINT);
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
        float guideWidth = 580f;
        float guideHeight = 84f;

        drawPanel(chipX, chipY, chipWidth, chipHeight, MiniGamePalette.PANEL_STRONG);
        drawPanelOutline(chipX, chipY, chipWidth, chipHeight, MiniGamePalette.BORDER_COLOR);
        drawLine(String.format("%.1f초", stateMachine.getSecondsRemaining()), chipX + 16f, chipY + 58f, 0.94f, MiniGamePalette.TEXT_PRIMARY);
        drawLine(stateMachine.getCapturedShots() + " / " + PhotoTimeStateMachine.TOTAL_SHOTS + " 컷", chipX + 16f, chipY + 28f, 0.82f, MiniGamePalette.TEXT_MINT);

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

    @Override
    protected String commandHint() {
        return resolveCommandHint();
    }

    private String resolvePhaseDescription() {
        return switch (stateMachine.getPhase()) {
            case READY -> "치즈냥의 포토존 타이밍을 맞춰 3장의 생일 기념 컷을 남겨야 합니다. 민트 가이드와 카메라 프레임을 최대한 겹치세요.";
            case ACTIVE -> "구도 중심을 맞춘 뒤 SPACE로 셔터를 끊으세요. 빠르게 정확하게 3컷을 찍으면 높은 점수가 나옵니다.";
            case RESULT -> "촬영이 끝났습니다. 좋은 컷을 건졌다면 허브에 저장하고 다음 준비 단계로 넘어가면 됩니다.";
        };
    }

    private String resolvePhaseHint() {
        return switch (stateMachine.getPhase()) {
            case READY -> "SPACE로 촬영 시작";
            case ACTIVE -> "방향키/WASD로 이동, SPACE로 셔터";
            case RESULT -> "H 저장 후 허브 복귀, R 재시작, ESC 허브 복귀";
        };
    }

    private String resolveCommandHint() {
        return switch (stateMachine.getPhase()) {
            case READY -> "현재 입력: SPACE 로 시작, ESC 로 허브 복귀";
            case ACTIVE -> "현재 입력: 방향키/WASD 로 이동, SPACE 로 촬영, ESC 로 허브 복귀";
            case RESULT -> "현재 입력: H 로 저장 후 허브 복귀, R 또는 SPACE 로 다시 시작, ESC 로 허브 복귀";
        };
    }

    private String resolveLiveHint() {
        return switch (stateMachine.getPhase()) {
            case READY -> "SPACE 시작";
            case ACTIVE -> "방향키 이동  SPACE 촬영";
            case RESULT -> "H 저장  R 재시작  ESC 복귀";
        };
    }
}
