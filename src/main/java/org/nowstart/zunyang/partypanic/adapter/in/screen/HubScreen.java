package org.nowstart.zunyang.partypanic.adapter.in.screen;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.adapter.in.input.HubInputAdapter;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.DialogueWindowRenderer;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.HubMapRenderer;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.PixelUiRenderer;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameAssets;
import org.nowstart.zunyang.partypanic.application.dto.AdvanceDialogueResult;
import org.nowstart.zunyang.partypanic.application.dto.HubContext;
import org.nowstart.zunyang.partypanic.application.dto.InteractResult;
import org.nowstart.zunyang.partypanic.application.dto.MovePlayerCommand;
import org.nowstart.zunyang.partypanic.application.dto.MovePlayerResult;
import org.nowstart.zunyang.partypanic.application.port.in.AdvanceDialogueUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.InteractUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MovePlayerUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.GameNavigator;
import org.nowstart.zunyang.partypanic.domain.model.Dialogue;
import org.nowstart.zunyang.partypanic.domain.model.Direction;
import org.nowstart.zunyang.partypanic.domain.model.GameState;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

import java.util.ArrayList;
import java.util.List;

public final class HubScreen extends AbstractGameScreen {
    private static final float MOVE_REPEAT_SECONDS = 0.12f;
    private static final float PLAYER_LERP_SPEED = 14f;
    private static final float DIALOGUE_LERP_SPEED = 10f;
    private static final Color DIALOGUE_TEXT_COLOR = new Color(0.96f, 0.92f, 0.87f, 1f);
    private static final Color DIALOGUE_META_COLOR = new Color(0.80f, 0.75f, 0.72f, 1f);

    private final GameProgress progress;
    private final MovePlayerUseCase movePlayerUseCase;
    private final InteractUseCase interactUseCase;
    private final AdvanceDialogueUseCase advanceDialogueUseCase;
    private final BitmapFont bodyFont;
    private final BitmapFont titleFont;
    private final Texture pixelTexture;
    private final Texture portraitTexture;
    private final PixelUiRenderer ui;
    private final HubMapRenderer mapRenderer;
    private final DialogueWindowRenderer dialogueWindow;
    private final String initialNotice;
    private final float mapX;
    private final HubInputAdapter input = new HubInputAdapter();

    private GameState currentState;
    private float moveCooldown;
    private float playerDrawX;
    private float playerDrawY;
    private float dialogueWindowProgress;
    private float sceneTime;
    private TransientDialogueState transientDialogueState;

    public HubScreen(GameNavigator navigator, GameProgress progress, String notice, HubContext hubContext, GameAssets assets) {
        super(navigator, assets);
        this.progress = progress;
        this.initialNotice = notice;
        this.currentState = hubContext.initialState();
        this.movePlayerUseCase = hubContext.movePlayerUseCase();
        this.interactUseCase = hubContext.interactUseCase();
        this.advanceDialogueUseCase = hubContext.advanceDialogueUseCase();
        this.mapX = (HubMapRenderer.WINDOW_WIDTH - mapWidth()) * 0.5f;
        this.bodyFont = assets.bodyFont();
        this.titleFont = assets.titleFont();
        this.pixelTexture = assets.pixelTexture();
        this.portraitTexture = assets.hostTexture();
        this.ui = new PixelUiRenderer(batch, bodyFont, titleFont, pixelTexture);
        this.mapRenderer = new HubMapRenderer(ui, progress, hubContext.eventResolver(), currentState.gameMap(), mapX);
        this.dialogueWindow = new DialogueWindowRenderer(ui);
        this.playerDrawX = tileToScreenX(currentState.player().position().x());
        this.playerDrawY = tileToScreenY(currentState.player().position().y());

        List<String> introPages = resolveInitialNoticePages();
        if (!introPages.isEmpty()) {
            transientDialogueState = new TransientDialogueState("치즈냥", introPages);
        }
    }

    @Override
    public void render(float delta) {
        sceneTime += delta;
        if (!handleInput(delta)) {
            return;
        }
        updateAnimations(delta);

        ScreenUtils.clear(0.10f, 0.08f, 0.10f, 1f);

        beginFrame();
        mapRenderer.drawBackdropAndFrame();
        endFrame();

        mapRenderer.renderMap(camera);

        beginFrame();
        mapRenderer.drawOverlay(currentState, playerDrawX, playerDrawY, showsOperationalUi());
        drawDialogueWindow();
        endFrame();
    }

    @Override
    protected InputProcessor createInputProcessor() {
        return input;
    }

    @Override
    public void dispose() {
        super.dispose();
        mapRenderer.dispose();
    }

    private boolean handleInput(float delta) {
        if (transientDialogueState != null) {
            if (input.consumeConfirmRequested()) {
                transientDialogueState = transientDialogueState.advance();
            }
            return true;
        }

        if (currentState.activeDialogue() != null) {
            if (input.consumeConfirmRequested()) {
                AdvanceDialogueResult result = advanceDialogueUseCase.advance();
                currentState = result.state();
                if (result.completedActivityId() != null) {
                    navigator.openActivity(result.completedActivityId());
                    return false;
                }
            }
            return true;
        }

        moveCooldown = Math.max(0f, moveCooldown - delta);

        if (input.consumeBackRequested()) {
            navigator.showTitle();
            return false;
        }

        if (input.consumeConfirmRequested()) {
            InteractResult result = interactUseCase.interact();
            currentState = result.state();
            return true;
        }

        Direction direction = input.pressedDirection();
        if (direction == null) {
            return true;
        }

        if (moveCooldown > 0f) {
            MovePlayerResult result = movePlayerUseCase.move(new MovePlayerCommand(direction, false));
            currentState = result.state();
            return true;
        }

        MovePlayerResult result = movePlayerUseCase.move(new MovePlayerCommand(direction));
        currentState = result.state();
        moveCooldown = MOVE_REPEAT_SECONDS;
        return true;
    }

    private void updateAnimations(float delta) {
        float moveAlpha = Math.min(1f, delta * PLAYER_LERP_SPEED);
        playerDrawX = MathUtils.lerp(playerDrawX, tileToScreenX(currentState.player().position().x()), moveAlpha);
        playerDrawY = MathUtils.lerp(playerDrawY, tileToScreenY(currentState.player().position().y()), moveAlpha);

        float dialogueTarget = hasVisibleDialogue() ? 1f : 0f;
        float dialogueAlpha = Math.min(1f, delta * DIALOGUE_LERP_SPEED);
        dialogueWindowProgress = MathUtils.lerp(dialogueWindowProgress, dialogueTarget, dialogueAlpha);
    }

    private void drawDialogueWindow() {
        if (!hasVisibleDialogue()) {
            return;
        }

        Dialogue visibleDialogue = currentDialogue();
        dialogueWindow.draw(
                portraitTexture,
                sceneTime,
                dialogueWindowProgress,
                visibleDialogue.currentLine().speaker(),
                visibleDialogue.currentLine().text(),
                DIALOGUE_TEXT_COLOR,
                showsOperationalUi()
                        ? (visibleDialogue.currentIndex() + 1) + " / " + visibleDialogue.lineCount()
                        : null,
                DIALOGUE_META_COLOR
        );
    }

    private boolean hasVisibleDialogue() {
        return transientDialogueState != null || currentState.activeDialogue() != null;
    }

    private Dialogue currentDialogue() {
        if (transientDialogueState != null) {
            return transientDialogueState.dialogue();
        }
        return currentState.activeDialogue();
    }

    private float mapWidth() {
        return currentState.gameMap().columnCount() * HubMapRenderer.TILE_SIZE;
    }

    private float mapHeight() {
        return currentState.gameMap().rowCount() * HubMapRenderer.TILE_SIZE;
    }

    private float tileToScreenX(int tileX) {
        return mapX + (tileX * HubMapRenderer.TILE_SIZE);
    }

    private float tileToScreenY(int tileY) {
        return HubMapRenderer.MAP_Y + ((currentState.gameMap().rowCount() - tileY - 1) * HubMapRenderer.TILE_SIZE);
    }

    private List<String> resolveInitialNoticePages() {
        if (initialNotice == null || initialNotice.isBlank()) {
            return List.of();
        }

        List<String> pages = new ArrayList<>();
        pages.add(initialNotice);
        if (progress.getCompletedCount() == 0) {
            pages.add("방향키로 한 칸씩 움직이고, 조사하고 싶은 오브젝트 정면에서 ENTER나 SPACE를 누르자.");
        }
        return pages;
    }

    private static final class TransientDialogueState {
        private final Dialogue dialogue;

        private TransientDialogueState(String speaker, List<String> pages) {
            this.dialogue = Dialogue.singleSpeaker(speaker, pages);
        }

        private TransientDialogueState(Dialogue dialogue) {
            this.dialogue = dialogue;
        }

        private Dialogue dialogue() {
            return dialogue;
        }

        private TransientDialogueState advance() {
            if (!dialogue.hasNext()) {
                return null;
            }
            return new TransientDialogueState(dialogue.advance());
        }
    }
}
