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
import org.nowstart.zunyang.partypanic.screen.minigame.MiniGameChrome;
import org.nowstart.zunyang.partypanic.screen.minigame.MiniGameLayout;
import org.nowstart.zunyang.partypanic.screen.minigame.MiniGamePalette;
import org.nowstart.zunyang.partypanic.screen.ui.PixelUiRenderer;
import org.nowstart.zunyang.partypanic.state.DeskSetupStateMachine;
import org.nowstart.zunyang.partypanic.world.GameProgress;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PartyPanicScreen extends ScreenAdapter {
    private final PartyPanicGame game;
    private final GameProgress progress;
    private final DeskSetupStateMachine stateMachine = new DeskSetupStateMachine();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font;
    private final Texture pixelTexture;
    private final Texture backgroundTexture;
    private final Texture hostTexture;
    private final PixelUiRenderer ui;

    public PartyPanicScreen(PartyPanicGame game, GameProgress progress) {
        this.game = game;
        this.progress = progress;
        this.font = ScreenSupport.createFont(buildFontCharacters());
        this.pixelTexture = ScreenSupport.createPixelTexture();
        this.backgroundTexture = ScreenSupport.loadTexture("assets/images/backgrounds/desk-party-stage.png");
        this.hostTexture = ScreenSupport.loadTexture("assets/images/characters/zunyang-birthday-host.png");
        this.ui = new PixelUiRenderer(batch, font, pixelTexture);
    }

    @Override
    public void render(float delta) {
        handleInput();
        if (game.getScreen() != this) {
            return;
        }

        stateMachine.update(delta);
        ScreenUtils.clear(0.06f, 0.04f, 0.05f, 1f);

        batch.begin();
        MiniGameChrome.drawBackdrop(ui, backgroundTexture);
        MiniGameChrome.drawFrames(ui, backgroundTexture, game.getConfig().showsOperationalUi());
        drawDeskSetupStage();
        if (game.getConfig().showsOperationalUi()) {
            drawStatusPanel();
            MiniGameChrome.drawCommandBar(ui, stateMachine.commandHint());
        } else {
            drawLiveHud();
        }
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.showHub("방송 책상에서 허브로 복귀했습니다.");
            return;
        }

        if (stateMachine.isReady()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                stateMachine.start();
            }
            return;
        }

        if (stateMachine.isActive()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                stateMachine.moveSelectionUp();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                stateMachine.moveSelectionDown();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                stateMachine.adjustSelected(-6f);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                stateMachine.adjustSelected(6f);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                stateMachine.confirmSelected();
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            stateMachine.restart();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.H) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.finishBroadcastDeskMinigame(stateMachine.finalScore());
        }
    }

    private void drawDeskSetupStage() {
        float infoX = MiniGameLayout.STAGE_X + 28f;
        float infoY = MiniGameLayout.STAGE_Y + MiniGameLayout.STAGE_HEIGHT - 144f;
        float infoWidth = 596f;
        float infoHeight = 100f;
        float hostHeight = 404f;
        float hostWidth = hostHeight * (hostTexture.getWidth() / (float) hostTexture.getHeight());
        float hostX = MiniGameLayout.STAGE_X + MiniGameLayout.STAGE_WIDTH - hostWidth - 28f;
        float hostY = MiniGameLayout.STAGE_Y + 46f;

        ui.line("방송 책상 정리", MiniGameLayout.STAGE_X + 24f, MiniGameLayout.STAGE_Y + MiniGameLayout.STAGE_HEIGHT - 18f, 1.22f, MiniGamePalette.TEXT_ACCENT);
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

    private void drawStatusPanel() {
        float left = MiniGameLayout.PANEL_X + 22f;
        float top = MiniGameLayout.PANEL_Y + MiniGameLayout.PANEL_HEIGHT - 24f;
        float timeRatio = stateMachine.secondsRemaining() / DeskSetupStateMachine.ACTIVE_SECONDS;
        float completedRatio = stateMachine.confirmedCount() / (float) stateMachine.taskCount();

        ui.line("책상 상태", left, top, 1.16f, MiniGamePalette.TEXT_ACCENT);
        ui.line("단계 " + stateMachine.phase().name(), left, top - 34f, 0.92f, MiniGamePalette.TEXT_PRIMARY);
        ui.line("최고 점수 " + progress.getBestScore(GameProgress.BROADCAST_DESK), left, top - 66f, 0.92f, MiniGamePalette.TEXT_MINT);

        ui.line("남은 시간", left, top - 126f, 0.96f, MiniGamePalette.TEXT_PRIMARY);
        ui.panel(left, top - 156f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        ui.panel(left, top - 156f, (MiniGameLayout.PANEL_WIDTH - 52f) * timeRatio, 18f, MiniGamePalette.HIGHLIGHT_COLOR);
        ui.panelOutline(left, top - 156f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, MiniGamePalette.BORDER_COLOR);
        ui.line(String.format("%.1f초", stateMachine.secondsRemaining()), left, top - 168f, 0.78f, MiniGamePalette.TEXT_MUTED);

        ui.line("완료 진행", left, top - 214f, 0.96f, MiniGamePalette.TEXT_PRIMARY);
        ui.panel(left, top - 244f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, new Color(0.22f, 0.15f, 0.18f, 0.92f));
        ui.panel(left, top - 244f, (MiniGameLayout.PANEL_WIDTH - 52f) * completedRatio, 18f, MiniGamePalette.TEXT_MINT);
        ui.panelOutline(left, top - 244f, MiniGameLayout.PANEL_WIDTH - 52f, 18f, MiniGamePalette.BORDER_COLOR);
        ui.line(stateMachine.confirmedCount() + " / " + stateMachine.taskCount() + " 항목", left, top - 256f, 0.78f, MiniGamePalette.TEXT_MUTED);

        ui.line("선택 중", left, top - 314f, 0.96f, MiniGamePalette.TEXT_PRIMARY);
        ui.line(stateMachine.taskTitle(stateMachine.selectedIndex()), left, top - 348f, 1.14f, MiniGamePalette.TEXT_PRIMARY);
        ui.paragraph(stateMachine.taskHint(stateMachine.selectedIndex()), left, top - 382f, MiniGameLayout.PANEL_WIDTH - 44f, 0.82f, MiniGamePalette.TEXT_MUTED);

        float guideY = MiniGameLayout.PANEL_Y + 166f;
        ui.panel(left - 2f, guideY, MiniGameLayout.PANEL_WIDTH - 44f, 168f, MiniGamePalette.PANEL_COLOR);
        ui.panelOutline(left - 2f, guideY, MiniGameLayout.PANEL_WIDTH - 44f, 168f, MiniGamePalette.BORDER_COLOR);
        ui.line("안내", left + 14f, guideY + 138f, 0.98f, MiniGamePalette.TEXT_ACCENT);
        ui.paragraph(stateMachine.feedback(), left + 14f, guideY + 98f, MiniGameLayout.PANEL_WIDTH - 74f, 0.84f, MiniGamePalette.TEXT_PRIMARY);
        ui.paragraph("UP / DOWN 으로 작업 선택, LEFT / RIGHT 로 수치 조정, SPACE 로 확정합니다.", left + 14f, guideY + 52f, MiniGameLayout.PANEL_WIDTH - 74f, 0.80f, MiniGamePalette.TEXT_MUTED);
        ui.paragraph("결과 화면에서는 H 또는 ENTER로 저장, R로 재시작합니다.", left + 14f, guideY + 20f, MiniGameLayout.PANEL_WIDTH - 74f, 0.80f, MiniGamePalette.TEXT_MUTED);

        if (stateMachine.isResult()) {
            float resultY = MiniGameLayout.PANEL_Y + 74f;
            ui.line("결과 점수", left, resultY + 80f, 0.98f, MiniGamePalette.TEXT_ACCENT);
            ui.line(String.valueOf(stateMachine.finalScore()), left, resultY + 34f, 1.86f, MiniGamePalette.TEXT_MINT);
        }
    }

    private void drawLiveHud() {
        float chipX = MiniGameLayout.WINDOW_WIDTH - 246f;
        float chipY = MiniGameLayout.WINDOW_HEIGHT - 116f;
        float feedbackX = MiniGameLayout.STAGE_X + 28f;
        float feedbackY = MiniGameLayout.STAGE_Y + 24f;

        ui.panel(chipX, chipY, 190f, 84f, MiniGamePalette.PANEL_STRONG);
        ui.panelOutline(chipX, chipY, 190f, 84f, MiniGamePalette.BORDER_COLOR);
        ui.line(String.format("%.1f초", stateMachine.secondsRemaining()), chipX + 16f, chipY + 54f, 0.96f, MiniGamePalette.TEXT_PRIMARY);
        ui.line(stateMachine.confirmedCount() + " / " + stateMachine.taskCount() + " 정리", chipX + 16f, chipY + 24f, 0.82f, MiniGamePalette.TEXT_MINT);

        ui.panel(feedbackX, feedbackY, 560f, 84f, MiniGamePalette.PANEL_COLOR);
        ui.panelOutline(feedbackX, feedbackY, 560f, 84f, MiniGamePalette.BORDER_COLOR);
        ui.paragraph(stateMachine.feedback(), feedbackX + 18f, feedbackY + 50f, 524f, 0.84f, MiniGamePalette.TEXT_PRIMARY);
        ui.line(stateMachine.liveHint(), feedbackX + 18f, feedbackY + 20f, 0.76f, MiniGamePalette.TEXT_MUTED);

        if (!stateMachine.isResult()) {
            return;
        }

        float resultX = MiniGameLayout.STAGE_X + ((MiniGameLayout.STAGE_WIDTH - 256f) * 0.5f);
        float resultY = MiniGameLayout.STAGE_Y + 40f;
        ui.panel(resultX, resultY, 256f, 118f, MiniGamePalette.PANEL_STRONG);
        ui.panelOutline(resultX, resultY, 256f, 118f, MiniGamePalette.HIGHLIGHT_COLOR);
        ui.line("결과 점수", resultX + 22f, resultY + 86f, 0.92f, MiniGamePalette.TEXT_ACCENT);
        ui.line(String.valueOf(stateMachine.finalScore()), resultX + 22f, resultY + 36f, 1.90f, MiniGamePalette.TEXT_MINT);
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
                "현재 수치",
                "항목",
                "방송 책상에서 허브로 복귀했습니다.",
                "UP / DOWN 으로 작업 선택, LEFT / RIGHT 로 수치 조정, SPACE 로 확정합니다.",
                "결과 화면에서는 H 또는 ENTER로 저장, R로 재시작합니다."
        )) {
            appendCharacters(characters, text);
        }

        for (int index = 0; index < stateMachine.taskCount(); index += 1) {
            appendCharacters(characters, stateMachine.taskTitle(index));
            appendCharacters(characters, stateMachine.taskHint(index));
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
    public void dispose() {
        batch.dispose();
        font.dispose();
        pixelTexture.dispose();
        backgroundTexture.dispose();
        hostTexture.dispose();
    }
}
