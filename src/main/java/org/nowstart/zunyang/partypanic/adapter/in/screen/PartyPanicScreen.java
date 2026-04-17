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
import org.nowstart.zunyang.partypanic.domain.minigame.DeskSetupStateMachine;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

import java.util.Map;

public final class PartyPanicScreen extends AbstractMiniGameScreen {
    private final DeskSetupStateMachine stateMachine = new DeskSetupStateMachine();
    private final Texture hostTexture;
    private final MappedActionInputAdapter<PartyAction> input = new MappedActionInputAdapter<>(Map.ofEntries(
            Map.entry(Input.Keys.ESCAPE, PartyAction.EXIT),
            Map.entry(Input.Keys.SPACE, PartyAction.SPACE),
            Map.entry(Input.Keys.ENTER, PartyAction.ENTER),
            Map.entry(Input.Keys.UP, PartyAction.SELECT_PREVIOUS),
            Map.entry(Input.Keys.DOWN, PartyAction.SELECT_NEXT),
            Map.entry(Input.Keys.TAB, PartyAction.SELECT_NEXT),
            Map.entry(Input.Keys.LEFT, PartyAction.ADJUST_LEFT),
            Map.entry(Input.Keys.A, PartyAction.ADJUST_LEFT),
            Map.entry(Input.Keys.RIGHT, PartyAction.ADJUST_RIGHT),
            Map.entry(Input.Keys.D, PartyAction.ADJUST_RIGHT),
            Map.entry(Input.Keys.R, PartyAction.RESTART),
            Map.entry(Input.Keys.H, PartyAction.SAVE)
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
    private ProgressBar operationalCompletedBar;
    private Label operationalCompletedValueLabel;
    private Label operationalSelectedValueLabel;
    private Label operationalGuideBodyLabel;
    private Label operationalGuideHintLabel;
    private Label operationalResultValueLabel;
    private Label commandLabel;
    private Label liveChipTimeLabel;
    private Label liveChipProgressLabel;
    private Label liveGuideBodyLabel;
    private Label liveGuideHintLabel;
    private Label liveResultValueLabel;

    public PartyPanicScreen(GameNavigator navigator, GameProgress progress, GameAssets assets) {
        super(navigator, progress, assets);
        initializeUi("assets/images/backgrounds/desk-party-stage.png");
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
        operationalPanel.add(scene2dUi.titleLabel("책상 상태", 1.12f, MiniGamePalette.TEXT_ACCENT)).row();
        operationalPhaseLabel = scene2dUi.bodyLabel("", 0.92f, MiniGamePalette.TEXT_PRIMARY);
        operationalPanel.add(operationalPhaseLabel).row();
        operationalBestScoreLabel = scene2dUi.bodyLabel("", 0.92f, MiniGamePalette.TEXT_MINT);
        operationalPanel.add(operationalBestScoreLabel).row();
        operationalPanel.add(scene2dUi.bodyLabel("남은 시간", 0.96f, MiniGamePalette.TEXT_PRIMARY)).padTop(10f).row();
        operationalTimeBar = scene2dUi.progressBar(new Color(0.22f, 0.15f, 0.18f, 0.92f), MiniGamePalette.HIGHLIGHT_COLOR);
        operationalPanel.add(operationalTimeBar).height(18f).row();
        operationalTimeValueLabel = scene2dUi.bodyLabel("", 0.78f, MiniGamePalette.TEXT_MUTED);
        operationalPanel.add(operationalTimeValueLabel).row();
        operationalPanel.add(scene2dUi.bodyLabel("완료 진행", 0.96f, MiniGamePalette.TEXT_PRIMARY)).padTop(10f).row();
        operationalCompletedBar = scene2dUi.progressBar(new Color(0.22f, 0.15f, 0.18f, 0.92f), MiniGamePalette.TEXT_MINT);
        operationalPanel.add(operationalCompletedBar).height(18f).row();
        operationalCompletedValueLabel = scene2dUi.bodyLabel("", 0.78f, MiniGamePalette.TEXT_MUTED);
        operationalPanel.add(operationalCompletedValueLabel).row();
        operationalSelectedValueLabel = scene2dUi.bodyLabel("", 1.14f, MiniGamePalette.TEXT_PRIMARY);
        operationalPanel.add(operationalSelectedValueLabel).padTop(10f).row();
        operationalGuideBodyLabel = scene2dUi.bodyLabel("", 0.84f, MiniGamePalette.TEXT_PRIMARY);
        operationalPanel.add(operationalGuideBodyLabel).width(MiniGameLayout.PANEL_WIDTH - 44f).row();
        operationalGuideHintLabel = scene2dUi.bodyLabel("", 0.80f, MiniGamePalette.TEXT_MUTED);
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
        liveChipPanel.setBounds(MiniGameLayout.WINDOW_WIDTH - 246f, MiniGameLayout.WINDOW_HEIGHT - 116f, 190f, 84f);
        liveChipPanel.defaults().left().growX().padBottom(6f);
        liveChipTimeLabel = scene2dUi.bodyLabel("", 0.96f, MiniGamePalette.TEXT_PRIMARY);
        liveChipPanel.add(liveChipTimeLabel).row();
        liveChipProgressLabel = scene2dUi.bodyLabel("", 0.82f, MiniGamePalette.TEXT_MINT);
        liveChipPanel.add(liveChipProgressLabel);
        stage.addActor(liveChipPanel);

        liveGuidePanel = scene2dUi.panel(MiniGamePalette.PANEL_COLOR, MiniGamePalette.BORDER_COLOR);
        liveGuidePanel.setBounds(MiniGameLayout.STAGE_X + 28f, MiniGameLayout.STAGE_Y + 24f, 560f, 84f);
        liveGuidePanel.defaults().left().growX().padBottom(6f);
        liveGuideBodyLabel = scene2dUi.bodyLabel("", 0.84f, MiniGamePalette.TEXT_PRIMARY);
        liveGuidePanel.add(liveGuideBodyLabel).width(524f).row();
        liveGuideHintLabel = scene2dUi.bodyLabel("", 0.76f, MiniGamePalette.TEXT_MUTED);
        liveGuidePanel.add(liveGuideHintLabel).width(524f);
        stage.addActor(liveGuidePanel);

        liveResultPanel = scene2dUi.panel(MiniGamePalette.PANEL_STRONG, MiniGamePalette.HIGHLIGHT_COLOR);
        liveResultPanel.setBounds(MiniGameLayout.STAGE_X + ((MiniGameLayout.STAGE_WIDTH - 256f) * 0.5f), MiniGameLayout.STAGE_Y + 40f, 256f, 118f);
        liveResultPanel.defaults().left().growX().padBottom(6f);
        liveResultPanel.add(scene2dUi.bodyLabel("결과 점수", 0.92f, MiniGamePalette.TEXT_ACCENT)).row();
        liveResultValueLabel = scene2dUi.titleLabel("", 1.90f, MiniGamePalette.TEXT_MINT);
        liveResultPanel.add(liveResultValueLabel);
        stage.addActor(liveResultPanel);

        return stage;
    }

    @Override
    protected boolean handleInput() {
        PartyAction action;
        while ((action = input.pollAction()) != null) {
            if (action == PartyAction.EXIT) {
                navigator.showHub("방송 책상에서 허브로 복귀했습니다.");
                return false;
            }

            if (stateMachine.isReady()) {
                if (action == PartyAction.SPACE || action == PartyAction.ENTER) {
                    stateMachine.start();
                }
                return true;
            }

            if (stateMachine.isActive()) {
                switch (action) {
                    case SELECT_PREVIOUS -> stateMachine.moveSelectionUp();
                    case SELECT_NEXT -> stateMachine.moveSelectionDown();
                    case ADJUST_LEFT -> stateMachine.adjustSelected(-6f);
                    case ADJUST_RIGHT -> stateMachine.adjustSelected(6f);
                    case SPACE, ENTER -> stateMachine.confirmSelected();
                    default -> {
                    }
                }
                return true;
            }

            if (action == PartyAction.RESTART || action == PartyAction.SPACE) {
                stateMachine.restart();
                return true;
            }
            if (action == PartyAction.SAVE || action == PartyAction.ENTER) {
                navigator.completeScoredActivity(ActivityId.BROADCAST_DESK, stateMachine.finalScore());
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
        float infoX = MiniGameLayout.STAGE_X + 28f;
        float infoY = MiniGameLayout.STAGE_Y + MiniGameLayout.STAGE_HEIGHT - 144f;
        float infoWidth = 596f;
        float infoHeight = 100f;
        float hostHeight = 404f;
        float hostWidth = hostHeight * (hostTexture.getWidth() / (float) hostTexture.getHeight());
        float hostX = MiniGameLayout.STAGE_X + MiniGameLayout.STAGE_WIDTH - hostWidth - 28f;
        float hostY = MiniGameLayout.STAGE_Y + 46f;

        ui.titleLine("방송 책상 정리", MiniGameLayout.STAGE_X + 24f, MiniGameLayout.STAGE_Y + MiniGameLayout.STAGE_HEIGHT - 18f, 1.22f, MiniGamePalette.TEXT_ACCENT);
        ui.panel(infoX, infoY, infoWidth, infoHeight, MiniGamePalette.PANEL_COLOR);
        ui.panelOutline(infoX, infoY, infoWidth, infoHeight, MiniGamePalette.BORDER_COLOR);
        ui.paragraph(stateMachine.phaseDescription(), infoX + 18f, infoY + 58f, infoWidth - 36f, 0.94f, MiniGamePalette.TEXT_PRIMARY);
        ui.line(stateMachine.phaseHint(), infoX + 18f, infoY + 24f, 0.82f, MiniGamePalette.TEXT_MUTED);

        float startX = MiniGameLayout.STAGE_X + 56f;
        float startY = MiniGameLayout.STAGE_Y + 302f;
        for (int index = 0; index < stateMachine.taskCount(); index += 1) {
            float x = startX + ((index % 2) * 318f);
            float y = startY - ((index / 2) * 190f);
            drawTaskCard(index, x, y, 286f, 150f);
        }

        ui.panel(hostX - 16f, hostY - 14f, hostWidth + 32f, hostHeight + 28f, new Color(0.14f, 0.08f, 0.12f, 0.36f));
        ui.textureFit(hostTexture, hostX, hostY, hostWidth, hostHeight);
    }

    private void drawTaskCard(int index, float x, float y, float width, float height) {
        boolean selected = stateMachine.selectedIndex() == index;
        boolean completed = stateMachine.confirmed(index);
        Color border = completed ? MiniGamePalette.TEXT_MINT : selected ? MiniGamePalette.HIGHLIGHT_COLOR : MiniGamePalette.BORDER_COLOR;
        float gaugeWidth = width - 34f;
        float markerX = x + 18f + (gaugeWidth * (stateMachine.value(index) / 100f));
        float targetX = x + 18f + (gaugeWidth * (stateMachine.targetMin(index) / 100f));
        float targetWidth = gaugeWidth * ((stateMachine.targetMax(index) - stateMachine.targetMin(index)) / 100f);

        ui.panel(x, y, width, height, MiniGamePalette.PANEL_COLOR);
        ui.panelOutline(x, y, width, height, border);
        ui.line(stateMachine.taskTitle(index), x + 18f, y + height - 18f, 0.94f, completed ? MiniGamePalette.TEXT_MINT : MiniGamePalette.TEXT_PRIMARY);
        ui.paragraph(stateMachine.taskHint(index), x + 18f, y + height - 48f, width - 36f, 0.76f, MiniGamePalette.TEXT_MUTED);

        ui.panel(x + 18f, y + 46f, gaugeWidth, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        ui.panel(targetX, y + 46f, targetWidth, 18f, MiniGamePalette.TEXT_MINT);
        ui.panelOutline(x + 18f, y + 46f, gaugeWidth, 18f, MiniGamePalette.BORDER_COLOR);
        ui.panel(markerX - 4f, y + 40f, 8f, 30f, MiniGamePalette.HIGHLIGHT_COLOR);

        ui.line("현재 수치 " + Math.round(stateMachine.value(index)), x + 18f, y + 24f, 0.78f, MiniGamePalette.TEXT_PRIMARY);
        ui.line(
                completed ? "고정 완료" : stateMachine.isWithinTarget(index) ? "확정 가능" : "조정 필요",
                x + 142f,
                y + 24f,
                0.78f,
                completed ? MiniGamePalette.TEXT_MINT : stateMachine.isWithinTarget(index) ? MiniGamePalette.TEXT_ACCENT : MiniGamePalette.TEXT_MUTED
        );
    }

    @Override
    protected void syncHud() {
        boolean operational = showsOperationalUi();

        operationalPanel.setVisible(operational);
        commandPanel.setVisible(operational);
        liveChipPanel.setVisible(!operational);
        liveGuidePanel.setVisible(!operational);
        liveResultPanel.setVisible(!operational && stateMachine.isResult());

        operationalPhaseLabel.setText("단계 " + stateMachine.phase().name());
        operationalBestScoreLabel.setText("최고 점수 " + progress.getBestScore(ActivityId.BROADCAST_DESK));
        operationalTimeBar.setValue(stateMachine.secondsRemaining() / DeskSetupStateMachine.ACTIVE_SECONDS);
        operationalTimeValueLabel.setText(String.format("%.1f초", stateMachine.secondsRemaining()));
        operationalCompletedBar.setValue(stateMachine.confirmedCount() / (float) stateMachine.taskCount());
        operationalCompletedValueLabel.setText(stateMachine.confirmedCount() + " / " + stateMachine.taskCount() + " 항목");
        operationalSelectedValueLabel.setText("선택 중  " + stateMachine.taskTitle(stateMachine.selectedIndex()));
        operationalGuideBodyLabel.setText(stateMachine.feedback());
        operationalGuideHintLabel.setText("UP / DOWN 선택, LEFT / RIGHT 조정, SPACE 확정, H 저장, R 재시작");
        operationalResultValueLabel.setText(stateMachine.isResult() ? String.valueOf(stateMachine.finalScore()) : "");
        commandLabel.setText(stateMachine.commandHint());

        liveChipTimeLabel.setText(String.format("%.1f초", stateMachine.secondsRemaining()));
        liveChipProgressLabel.setText(stateMachine.confirmedCount() + " / " + stateMachine.taskCount() + " 정리");
        liveGuideBodyLabel.setText(stateMachine.feedback());
        liveGuideHintLabel.setText(stateMachine.liveHint());
        liveResultValueLabel.setText(String.valueOf(stateMachine.finalScore()));
    }

    private enum PartyAction {
        EXIT,
        SPACE,
        ENTER,
        SELECT_PREVIOUS,
        SELECT_NEXT,
        ADJUST_LEFT,
        ADJUST_RIGHT,
        RESTART,
        SAVE
    }
}
