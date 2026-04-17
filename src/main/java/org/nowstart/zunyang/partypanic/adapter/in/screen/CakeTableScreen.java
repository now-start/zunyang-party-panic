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
import org.nowstart.zunyang.partypanic.domain.minigame.CakeBalanceStateMachine;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

import java.util.Map;

public final class CakeTableScreen extends AbstractMiniGameScreen {
    private final CakeBalanceStateMachine stateMachine = new CakeBalanceStateMachine();
    private final Texture cakeTexture;
    private final Texture hostTexture;
    private final MappedActionInputAdapter<CakeAction> input = new MappedActionInputAdapter<>(Map.of(
            Input.Keys.ESCAPE, CakeAction.EXIT,
            Input.Keys.SPACE, CakeAction.PRIMARY,
            Input.Keys.LEFT, CakeAction.LEFT,
            Input.Keys.A, CakeAction.LEFT,
            Input.Keys.RIGHT, CakeAction.RIGHT,
            Input.Keys.D, CakeAction.RIGHT,
            Input.Keys.S, CakeAction.STABILIZE,
            Input.Keys.DOWN, CakeAction.STABILIZE,
            Input.Keys.R, CakeAction.RESTART,
            Input.Keys.H, CakeAction.SAVE
    ));

    private PanelTable operationalPanel;
    private PanelTable commandPanel;
    private PanelTable liveChipPanel;
    private PanelTable liveGuidePanel;
    private PanelTable liveResultPanel;
    private Label operationalPhaseLabel;
    private Label operationalBestScoreLabel;
    private ProgressBar operationalStabilityBar;
    private Label operationalStabilityValueLabel;
    private ProgressBar operationalTimeBar;
    private Label operationalTimeValueLabel;
    private Label operationalRecoveryValueLabel;
    private Label operationalGuideBodyLabel;
    private Label operationalGuideHintLabel;
    private Label operationalResultValueLabel;
    private Label commandLabel;
    private Label liveChipStabilityLabel;
    private Label liveChipTimeLabel;
    private Label liveGuideBodyLabel;
    private Label liveGuideHintLabel;
    private Label liveResultValueLabel;

    public CakeTableScreen(GameNavigator navigator, GameProgress progress, GameAssets assets) {
        super(navigator, progress, assets);
        initializeUi("assets/images/backgrounds/cake-rush-stage.png");
        this.cakeTexture = assets.cakeCardTexture();
        this.hostTexture = assets.hostTexture();
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
        operationalPanel.add(scene2dUi.titleLabel("케이크 상태", 1.12f, MiniGamePalette.TEXT_ACCENT)).row();
        operationalPhaseLabel = scene2dUi.bodyLabel("", 0.92f, MiniGamePalette.TEXT_PRIMARY);
        operationalPanel.add(operationalPhaseLabel).row();
        operationalBestScoreLabel = scene2dUi.bodyLabel("", 0.92f, MiniGamePalette.TEXT_MINT);
        operationalPanel.add(operationalBestScoreLabel).row();
        operationalPanel.add(scene2dUi.bodyLabel("안정도", 0.96f, MiniGamePalette.TEXT_PRIMARY)).padTop(10f).row();
        operationalStabilityBar = scene2dUi.progressBar(new Color(0.22f, 0.15f, 0.18f, 0.92f), MiniGamePalette.TEXT_MINT);
        operationalPanel.add(operationalStabilityBar).height(18f).row();
        operationalStabilityValueLabel = scene2dUi.bodyLabel("", 0.78f, MiniGamePalette.TEXT_MUTED);
        operationalPanel.add(operationalStabilityValueLabel).row();
        operationalPanel.add(scene2dUi.bodyLabel("남은 시간", 0.96f, MiniGamePalette.TEXT_PRIMARY)).padTop(10f).row();
        operationalTimeBar = scene2dUi.progressBar(new Color(0.22f, 0.15f, 0.18f, 0.92f), MiniGamePalette.HIGHLIGHT_COLOR);
        operationalPanel.add(operationalTimeBar).height(18f).row();
        operationalTimeValueLabel = scene2dUi.bodyLabel("", 0.78f, MiniGamePalette.TEXT_MUTED);
        operationalPanel.add(operationalTimeValueLabel).row();
        operationalRecoveryValueLabel = scene2dUi.bodyLabel("", 0.92f, MiniGamePalette.TEXT_PRIMARY);
        operationalPanel.add(operationalRecoveryValueLabel).padTop(10f).row();
        operationalGuideBodyLabel = scene2dUi.bodyLabel("", 0.84f, MiniGamePalette.TEXT_PRIMARY);
        operationalPanel.add(operationalGuideBodyLabel).width(MiniGameLayout.PANEL_WIDTH - 44f).row();
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
        liveChipStabilityLabel = scene2dUi.bodyLabel("", 0.92f, MiniGamePalette.TEXT_PRIMARY);
        liveChipPanel.add(liveChipStabilityLabel).row();
        liveChipTimeLabel = scene2dUi.bodyLabel("", 0.82f, MiniGamePalette.TEXT_MINT);
        liveChipPanel.add(liveChipTimeLabel);
        stage.addActor(liveChipPanel);

        liveGuidePanel = scene2dUi.panel(MiniGamePalette.PANEL_COLOR, MiniGamePalette.BORDER_COLOR);
        liveGuidePanel.setBounds(MiniGameLayout.STAGE_X + 28f, MiniGameLayout.STAGE_Y + 24f, 560f, 84f);
        liveGuidePanel.defaults().left().growX().padBottom(6f);
        liveGuideBodyLabel = scene2dUi.bodyLabel("", 0.82f, MiniGamePalette.TEXT_PRIMARY);
        liveGuidePanel.add(liveGuideBodyLabel).width(524f).row();
        liveGuideHintLabel = scene2dUi.bodyLabel("", 0.76f, MiniGamePalette.TEXT_MUTED);
        liveGuidePanel.add(liveGuideHintLabel).width(524f);
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
    protected boolean handleInput() {
        CakeAction action;
        while ((action = input.pollAction()) != null) {
            if (action == CakeAction.EXIT) {
                navigator.showHub("케이크 테이블에서 허브로 복귀했습니다.");
                return false;
            }

            if (stateMachine.getPhase() == CakeBalanceStateMachine.Phase.READY) {
                if (action == CakeAction.PRIMARY) {
                    stateMachine.start();
                }
                return true;
            }

            if (stateMachine.isActive()) {
                switch (action) {
                    case LEFT -> stateMachine.nudgeLeft();
                    case RIGHT -> stateMachine.nudgeRight();
                    case STABILIZE -> stateMachine.stabilize();
                    default -> {
                    }
                }
                return true;
            }

            if (stateMachine.isResult()) {
                if (action == CakeAction.RESTART || action == CakeAction.PRIMARY) {
                    stateMachine.restart();
                    return true;
                }
                if (action == CakeAction.SAVE) {
                    navigator.completeScoredActivity(ActivityId.CAKE_TABLE, stateMachine.getFinalScore());
                    return false;
                }
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
    protected void syncHud() {
        boolean operational = showsOperationalUi();

        operationalPanel.setVisible(operational);
        commandPanel.setVisible(operational);
        liveChipPanel.setVisible(!operational);
        liveGuidePanel.setVisible(!operational);
        liveResultPanel.setVisible(!operational && stateMachine.isResult());

        operationalPhaseLabel.setText("단계 " + stateMachine.getPhase().name());
        operationalBestScoreLabel.setText("최고 점수 " + progress.getBestScore(ActivityId.CAKE_TABLE));
        operationalStabilityBar.setValue(stateMachine.getStability() / 100f);
        operationalStabilityValueLabel.setText(String.format("%.0f / 100", stateMachine.getStability()));
        operationalTimeBar.setValue(stateMachine.getSecondsRemaining() / CakeBalanceStateMachine.ACTIVE_SECONDS);
        operationalTimeValueLabel.setText(String.format("%.1f초", stateMachine.getSecondsRemaining()));
        operationalRecoveryValueLabel.setText("복구 횟수 " + stateMachine.getRecoveryCount());
        operationalGuideBodyLabel.setText("LEFT/A 또는 RIGHT/D 로 케이크를 바로잡고, S 또는 아래 방향키로 급한 흔들림을 눌러 주세요.");
        operationalGuideHintLabel.setText("H 저장, R 재시작, ESC 복귀");
        operationalResultValueLabel.setText(stateMachine.isResult() ? String.valueOf(stateMachine.getFinalScore()) : "");
        commandLabel.setText(commandHint());

        liveChipStabilityLabel.setText(String.format("안정 %.0f", stateMachine.getStability()));
        liveChipTimeLabel.setText(String.format("시간 %.1f초", stateMachine.getSecondsRemaining()));
        liveGuideBodyLabel.setText(resolvePhaseDescription());
        liveGuideHintLabel.setText(resolveLiveHint());
        liveResultValueLabel.setText(String.valueOf(stateMachine.getFinalScore()));
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

    private String commandHint() {
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

    private enum CakeAction {
        EXIT,
        PRIMARY,
        LEFT,
        RIGHT,
        STABILIZE,
        RESTART,
        SAVE
    }
}
