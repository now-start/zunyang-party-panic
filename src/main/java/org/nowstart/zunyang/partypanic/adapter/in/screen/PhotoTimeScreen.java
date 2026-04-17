package org.nowstart.zunyang.partypanic.adapter.in.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.nowstart.zunyang.partypanic.adapter.in.input.MappedActionInputAdapter;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.MiniGameLayout;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.MiniGamePalette;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameAssets;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameViewportConfig;
import org.nowstart.zunyang.partypanic.adapter.in.ui.PanelTable;
import org.nowstart.zunyang.partypanic.application.port.out.GameNavigator;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;
import org.nowstart.zunyang.partypanic.domain.minigame.PhotoTimeStateMachine;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

import java.util.Map;

public final class PhotoTimeScreen extends AbstractMiniGameScreen {
    private final PhotoTimeStateMachine stateMachine = new PhotoTimeStateMachine();
    private final Texture cardTexture;
    private final Texture hostTexture;
    private final MappedActionInputAdapter<PhotoAction> input = new MappedActionInputAdapter<>(Map.ofEntries(
            Map.entry(Input.Keys.ESCAPE, PhotoAction.EXIT),
            Map.entry(Input.Keys.SPACE, PhotoAction.PRIMARY),
            Map.entry(Input.Keys.LEFT, PhotoAction.LEFT),
            Map.entry(Input.Keys.A, PhotoAction.LEFT),
            Map.entry(Input.Keys.RIGHT, PhotoAction.RIGHT),
            Map.entry(Input.Keys.D, PhotoAction.RIGHT),
            Map.entry(Input.Keys.UP, PhotoAction.UP),
            Map.entry(Input.Keys.W, PhotoAction.UP),
            Map.entry(Input.Keys.DOWN, PhotoAction.DOWN),
            Map.entry(Input.Keys.S, PhotoAction.DOWN),
            Map.entry(Input.Keys.R, PhotoAction.RESTART),
            Map.entry(Input.Keys.H, PhotoAction.SAVE)
    ));
    private PanelTable operationalPanel;
    private PanelTable commandPanel;
    private PanelTable liveChipPanel;
    private PanelTable liveGuidePanel;
    private PanelTable liveResultPanel;
    private Label operationalPhaseLabel;
    private Label operationalBestScoreLabel;
    private ProgressBar operationalTimeBar;
    private Label operationalTimeValueLabel;
    private ProgressBar operationalShotBar;
    private Label operationalShotValueLabel;
    private Label operationalJudgementLabel;
    private Label operationalScoreLabel;
    private Label operationalGuideBodyLabel;
    private Label operationalGuideHintLabel;
    private Label operationalResultValueLabel;
    private Label commandLabel;
    private Label liveChipTimeLabel;
    private Label liveChipShotLabel;
    private Label liveGuideBodyLabel;
    private Label liveGuideHintLabel;
    private Label liveResultValueLabel;

    public PhotoTimeScreen(GameNavigator navigator, GameProgress progress, GameAssets assets) {
        super(navigator, progress, assets);
        initializeUi("assets/images/backgrounds/mint-cats-stage.png");
        this.cardTexture = assets.photoCardTexture();
        this.hostTexture = assets.hostTexture();
    }

    @Override
    protected boolean handleInput() {
        PhotoAction action;
        while ((action = input.pollAction()) != null) {
            if (action == PhotoAction.EXIT) {
                navigator.showHub("포토존에서 허브로 복귀했습니다.");
                return false;
            }

            if (stateMachine.getPhase() == PhotoTimeStateMachine.Phase.READY) {
                if (action == PhotoAction.PRIMARY) {
                    stateMachine.start();
                }
                return true;
            }

            if (stateMachine.isActive()) {
                switch (action) {
                    case LEFT -> stateMachine.moveLeft();
                    case RIGHT -> stateMachine.moveRight();
                    case UP -> stateMachine.moveUp();
                    case DOWN -> stateMachine.moveDown();
                    case PRIMARY -> stateMachine.capture();
                    default -> {
                    }
                }
                return true;
            }

            if (stateMachine.isResult()) {
                if (action == PhotoAction.RESTART || action == PhotoAction.PRIMARY) {
                    stateMachine.restart();
                    return true;
                }
                if (action == PhotoAction.SAVE) {
                    navigator.completeScoredActivity(ActivityId.PHOTO_TIME, stateMachine.getFinalScore());
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected InputProcessor createInputProcessor() {
        return input;
    }

    @Override
    protected Stage buildUiStage() {
        Stage stage = new Stage(new FitViewport(GameViewportConfig.WORLD_WIDTH, GameViewportConfig.WORLD_HEIGHT));

        operationalPanel = scene2dUi.panel(MiniGamePalette.PANEL_STRONG, MiniGamePalette.BORDER_COLOR);
        operationalPanel.setBounds(MiniGameLayout.PANEL_X, MiniGameLayout.PANEL_Y, MiniGameLayout.PANEL_WIDTH, MiniGameLayout.PANEL_HEIGHT);
        operationalPanel.defaults().left().growX().padBottom(8f);
        operationalPanel.add(scene2dUi.titleLabel("포토존 상태", 1.12f, MiniGamePalette.TEXT_ACCENT)).row();
        operationalPhaseLabel = scene2dUi.bodyLabel("", 0.92f, MiniGamePalette.TEXT_PRIMARY);
        operationalPanel.add(operationalPhaseLabel).row();
        operationalBestScoreLabel = scene2dUi.bodyLabel("", 0.92f, MiniGamePalette.TEXT_MINT);
        operationalPanel.add(operationalBestScoreLabel).row();
        operationalPanel.add(scene2dUi.bodyLabel("남은 시간", 0.96f, MiniGamePalette.TEXT_PRIMARY)).padTop(10f).row();
        operationalTimeBar = scene2dUi.progressBar(new Color(0.22f, 0.15f, 0.18f, 0.92f), MiniGamePalette.TEXT_BLUE);
        operationalPanel.add(operationalTimeBar).height(18f).row();
        operationalTimeValueLabel = scene2dUi.bodyLabel("", 0.78f, MiniGamePalette.TEXT_MUTED);
        operationalPanel.add(operationalTimeValueLabel).row();
        operationalPanel.add(scene2dUi.bodyLabel("촬영 진행", 0.96f, MiniGamePalette.TEXT_PRIMARY)).padTop(10f).row();
        operationalShotBar = scene2dUi.progressBar(new Color(0.22f, 0.15f, 0.18f, 0.92f), MiniGamePalette.HIGHLIGHT_COLOR);
        operationalPanel.add(operationalShotBar).height(18f).row();
        operationalShotValueLabel = scene2dUi.bodyLabel("", 0.78f, MiniGamePalette.TEXT_MUTED);
        operationalPanel.add(operationalShotValueLabel).row();
        operationalJudgementLabel = scene2dUi.bodyLabel("", 0.92f, MiniGamePalette.TEXT_PRIMARY);
        operationalPanel.add(operationalJudgementLabel).padTop(10f).row();
        operationalScoreLabel = scene2dUi.bodyLabel("", 0.90f, MiniGamePalette.TEXT_MINT);
        operationalPanel.add(operationalScoreLabel).row();
        operationalGuideBodyLabel = scene2dUi.bodyLabel("", 0.84f, MiniGamePalette.TEXT_PRIMARY);
        operationalPanel.add(operationalGuideBodyLabel).width(MiniGameLayout.PANEL_WIDTH - 44f).padTop(10f).row();
        operationalGuideHintLabel = scene2dUi.bodyLabel("", 0.82f, MiniGamePalette.TEXT_MUTED);
        operationalPanel.add(operationalGuideHintLabel).width(MiniGameLayout.PANEL_WIDTH - 44f).row();
        operationalResultValueLabel = scene2dUi.titleLabel("", 1.84f, MiniGamePalette.TEXT_MINT);
        operationalPanel.add(operationalResultValueLabel).padTop(14f).row();
        stage.addActor(operationalPanel);

        commandPanel = scene2dUi.panel(MiniGamePalette.PANEL_STRONG, MiniGamePalette.BORDER_COLOR);
        commandPanel.setBounds(MiniGameLayout.COMMAND_X, MiniGameLayout.COMMAND_Y, MiniGameLayout.COMMAND_WIDTH, MiniGameLayout.COMMAND_HEIGHT);
        commandPanel.defaults().left().growX();
        commandLabel = scene2dUi.bodyLabel("", 0.90f, MiniGamePalette.TEXT_PRIMARY);
        commandPanel.add(commandLabel).width(MiniGameLayout.COMMAND_WIDTH - 44f);
        stage.addActor(commandPanel);

        liveChipPanel = scene2dUi.panel(MiniGamePalette.PANEL_STRONG, MiniGamePalette.BORDER_COLOR);
        liveChipPanel.setBounds(MiniGameLayout.WINDOW_WIDTH - 250f, MiniGameLayout.WINDOW_HEIGHT - 126f, 194f, 92f);
        liveChipPanel.defaults().left().growX().padBottom(6f);
        liveChipTimeLabel = scene2dUi.bodyLabel("", 0.94f, MiniGamePalette.TEXT_PRIMARY);
        liveChipPanel.add(liveChipTimeLabel).row();
        liveChipShotLabel = scene2dUi.bodyLabel("", 0.82f, MiniGamePalette.TEXT_MINT);
        liveChipPanel.add(liveChipShotLabel);
        stage.addActor(liveChipPanel);

        liveGuidePanel = scene2dUi.panel(MiniGamePalette.PANEL_COLOR, MiniGamePalette.BORDER_COLOR);
        liveGuidePanel.setBounds(MiniGameLayout.STAGE_X + 28f, MiniGameLayout.STAGE_Y + 24f, 580f, 84f);
        liveGuidePanel.defaults().left().growX().padBottom(6f);
        liveGuideBodyLabel = scene2dUi.bodyLabel("", 0.82f, MiniGamePalette.TEXT_PRIMARY);
        liveGuidePanel.add(liveGuideBodyLabel).width(544f).row();
        liveGuideHintLabel = scene2dUi.bodyLabel("", 0.76f, MiniGamePalette.TEXT_MUTED);
        liveGuidePanel.add(liveGuideHintLabel).width(544f);
        stage.addActor(liveGuidePanel);

        liveResultPanel = scene2dUi.panel(MiniGamePalette.PANEL_STRONG, MiniGamePalette.HIGHLIGHT_COLOR);
        liveResultPanel.setBounds(MiniGameLayout.STAGE_X + ((MiniGameLayout.STAGE_WIDTH - 240f) * 0.5f), MiniGameLayout.STAGE_Y + 36f, 240f, 118f);
        liveResultPanel.defaults().left().growX().padBottom(6f);
        liveResultPanel.add(scene2dUi.bodyLabel("결과 점수", 0.92f, MiniGamePalette.TEXT_ACCENT)).row();
        liveResultValueLabel = scene2dUi.titleLabel("", 1.86f, MiniGamePalette.TEXT_MINT);
        liveResultPanel.add(liveResultValueLabel);
        stage.addActor(liveResultPanel);

        return stage;
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
    protected void syncHud() {
        boolean operational = showsOperationalUi();

        operationalPanel.setVisible(operational);
        commandPanel.setVisible(operational);
        liveChipPanel.setVisible(!operational);
        liveGuidePanel.setVisible(!operational);
        liveResultPanel.setVisible(!operational && stateMachine.isResult());

        operationalPhaseLabel.setText("단계 " + stateMachine.getPhase().name());
        operationalBestScoreLabel.setText("최고 점수 " + progress.getBestScore(ActivityId.PHOTO_TIME));
        operationalTimeBar.setValue(stateMachine.getSecondsRemaining() / PhotoTimeStateMachine.ACTIVE_SECONDS);
        operationalTimeValueLabel.setText(String.format("%.1f초", stateMachine.getSecondsRemaining()));
        operationalShotBar.setValue(stateMachine.getCapturedShots() / (float) PhotoTimeStateMachine.TOTAL_SHOTS);
        operationalShotValueLabel.setText(stateMachine.getCapturedShots() + " / " + PhotoTimeStateMachine.TOTAL_SHOTS + " 컷");
        operationalJudgementLabel.setText("마지막 판정 " + stateMachine.getLastJudgement());
        operationalScoreLabel.setText("최근 점수 " + stateMachine.getLastShotScore());
        operationalGuideBodyLabel.setText("방향키 또는 WASD로 카메라 프레임을 움직이고 SPACE로 셔터를 끊으세요. 민트 프레임과 분홍 프레임을 겹칠수록 점수가 높습니다.");
        operationalGuideHintLabel.setText("결과 화면에서 H를 누르면 허브에 점수를 저장합니다.");
        operationalResultValueLabel.setText(stateMachine.isResult() ? String.valueOf(stateMachine.getFinalScore()) : "");
        commandLabel.setText(resolveCommandHint());

        liveChipTimeLabel.setText(String.format("시간 %.1f초", stateMachine.getSecondsRemaining()));
        liveChipShotLabel.setText(stateMachine.getCapturedShots() + " / " + PhotoTimeStateMachine.TOTAL_SHOTS + " 컷");
        liveGuideBodyLabel.setText(resolvePhaseDescription());
        liveGuideHintLabel.setText(resolveLiveHint());
        liveResultValueLabel.setText(String.valueOf(stateMachine.getFinalScore()));
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

    private enum PhotoAction {
        EXIT,
        PRIMARY,
        LEFT,
        RIGHT,
        UP,
        DOWN,
        RESTART,
        SAVE
    }
}
