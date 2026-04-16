package org.nowstart.zunyang.partypanic.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.application.port.GameNavigator;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;
import org.nowstart.zunyang.partypanic.presentation.hub.HubDirection;
import org.nowstart.zunyang.partypanic.presentation.hub.HubEventVisual;
import org.nowstart.zunyang.partypanic.presentation.hub.HubMapEvent;
import org.nowstart.zunyang.partypanic.presentation.hub.HubMapModel;
import org.nowstart.zunyang.partypanic.presentation.support.ScreenSupport;
import org.nowstart.zunyang.partypanic.presentation.ui.DialogueWindowRenderer;
import org.nowstart.zunyang.partypanic.presentation.ui.PixelUiRenderer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class HubScreen extends ScreenAdapter {
    private static final float WINDOW_WIDTH = 1600f;
    private static final float WINDOW_HEIGHT = 900f;
    private static final float MAP_Y = 164f;
    private static final float MOVE_REPEAT_SECONDS = 0.12f;
    private static final float PLAYER_LERP_SPEED = 14f;
    private static final float DIALOGUE_LERP_SPEED = 10f;

    private static final Color TEXT_LIGHT = new Color(0.96f, 0.92f, 0.87f, 1f);
    private static final Color TEXT_MUTED = new Color(0.80f, 0.75f, 0.72f, 1f);
    private static final Color TEXT_ACCENT = new Color(0.96f, 0.43f, 0.61f, 1f);
    private static final Color TEXT_MINT = new Color(0.57f, 0.84f, 0.76f, 1f);
    private static final Color OUTER_BACKGROUND = new Color(0.10f, 0.08f, 0.10f, 1f);
    private static final Color FRAME_COLOR = new Color(0.25f, 0.15f, 0.18f, 1f);
    private static final Color FLOOR_LIGHT = new Color(0.93f, 0.86f, 0.78f, 1f);
    private static final Color FLOOR_DARK = new Color(0.90f, 0.82f, 0.74f, 1f);
    private static final Color WALL_COLOR = new Color(0.49f, 0.35f, 0.34f, 1f);
    private static final Color WALL_TRIM = new Color(0.64f, 0.47f, 0.45f, 1f);
    private static final Color RUG_LIGHT = new Color(0.95f, 0.73f, 0.82f, 1f);
    private static final Color RUG_DARK = new Color(0.88f, 0.59f, 0.72f, 1f);
    private static final Color TILE_OUTLINE = new Color(0.79f, 0.69f, 0.62f, 0.42f);
    private static final Color WINDOW_BACKGROUND = new Color(0.15f, 0.11f, 0.13f, 0.96f);
    private static final Color WINDOW_EDGE = new Color(0.97f, 0.88f, 0.77f, 0.92f);

    private final GameNavigator navigator;
    private final GameProgress progress;
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font;
    private final Texture pixelTexture;
    private final Texture portraitTexture;
    private final PixelUiRenderer ui;
    private final DialogueWindowRenderer dialogueWindow;
    private final HubMapModel mapModel;
    private final String initialNotice;
    private final float mapX;

    private float moveCooldown;
    private float playerDrawX;
    private float playerDrawY;
    private float dialogueWindowProgress;
    private float sceneTime;
    private DialogueState dialogueState;

    public HubScreen(GameNavigator navigator, GameProgress progress, String notice) {
        this.navigator = navigator;
        this.progress = progress;
        this.initialNotice = notice;
        this.mapModel = new HubMapModel();
        this.mapX = (WINDOW_WIDTH - mapModel.mapWidth()) * 0.5f;
        this.font = ScreenSupport.createFont(buildFontCharacters());
        this.pixelTexture = ScreenSupport.createPixelTexture();
        this.portraitTexture = ScreenSupport.loadTexture("assets/images/characters/zunyang-birthday-host.png");
        this.ui = new PixelUiRenderer(batch, font, pixelTexture);
        this.dialogueWindow = new DialogueWindowRenderer(ui);
        this.playerDrawX = tileToScreenX(mapModel.playerTileX());
        this.playerDrawY = tileToScreenY(mapModel.playerTileY());

        List<String> introPages = resolveInitialNoticePages();
        if (!introPages.isEmpty()) {
            openDialogue("치즈냥", introPages, null);
        }
    }

    @Override
    public void render(float delta) {
        sceneTime += delta;
        if (!handleInput(delta)) {
            return;
        }
        updateAnimations(delta);

        ScreenUtils.clear(OUTER_BACKGROUND);

        batch.begin();
        drawBackdrop();
        drawMapFrame();
        drawMapTiles();
        drawMapEvents();
        drawPlayer();
        drawLocationPlate();
        if (navigator.showsOperationalUi()) {
            drawDebugPlate();
        } else {
            drawFocusPlate();
        }
        if (dialogueState != null) {
            dialogueWindow.draw(
                    portraitTexture,
                    sceneTime,
                    dialogueWindowProgress,
                    dialogueState.speaker,
                    dialogueState.pages.get(dialogueState.pageIndex),
                    TEXT_LIGHT,
                    navigator.showsOperationalUi() ? (dialogueState.pageIndex + 1) + " / " + dialogueState.pages.size() : null,
                    TEXT_MUTED
            );
        }
        batch.end();
    }

    private boolean handleInput(float delta) {
        if (dialogueState != null) {
            if (isConfirmPressed()) {
                return advanceDialogue();
            }
            return true;
        }

        moveCooldown = Math.max(0f, moveCooldown - delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            navigator.showTitle();
            return false;
        }

        if (isConfirmPressed()) {
            interactFacingTile();
            return true;
        }

        HubDirection direction = readPressedDirection();
        if (direction == null) {
            return true;
        }

        mapModel.setFacing(direction);
        if (moveCooldown > 0f) {
            return true;
        }

        mapModel.attemptStep(direction);
        moveCooldown = MOVE_REPEAT_SECONDS;
        return true;
    }

    private void updateAnimations(float delta) {
        float moveAlpha = Math.min(1f, delta * PLAYER_LERP_SPEED);
        playerDrawX = MathUtils.lerp(playerDrawX, tileToScreenX(mapModel.playerTileX()), moveAlpha);
        playerDrawY = MathUtils.lerp(playerDrawY, tileToScreenY(mapModel.playerTileY()), moveAlpha);

        float dialogueTarget = dialogueState == null ? 0f : 1f;
        float dialogueAlpha = Math.min(1f, delta * DIALOGUE_LERP_SPEED);
        dialogueWindowProgress = MathUtils.lerp(dialogueWindowProgress, dialogueTarget, dialogueAlpha);
    }

    private boolean isConfirmPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Input.Keys.E);
    }

    private HubDirection readPressedDirection() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            return HubDirection.LEFT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            return HubDirection.RIGHT;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            return HubDirection.UP;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            return HubDirection.DOWN;
        }
        return null;
    }

    private void interactFacingTile() {
        HubMapEvent facingEvent = mapModel.findFacingEvent();
        if (facingEvent == null) {
            return;
        }

        if (!progress.isUnlocked(facingEvent.id())) {
            openDialogue("치즈냥", List.of(facingEvent.lockedNotice()), null);
            return;
        }

        openDialogue("치즈냥", facingEvent.interactionLines(), () -> navigator.openActivity(facingEvent.id()));
    }

    private void openDialogue(String speaker, List<String> pages, Runnable onFinish) {
        if (pages == null || pages.isEmpty()) {
            if (onFinish != null) {
                onFinish.run();
            }
            return;
        }
        dialogueWindowProgress = 0f;
        dialogueState = new DialogueState(speaker, List.copyOf(pages), onFinish);
    }

    private boolean advanceDialogue() {
        if (dialogueState.pageIndex < dialogueState.pages.size() - 1) {
            dialogueState.pageIndex += 1;
            return true;
        }

        Runnable onFinish = dialogueState.onFinish;
        dialogueState = null;
        if (onFinish != null) {
            onFinish.run();
            return false;
        }
        return true;
    }

    private void drawBackdrop() {
        ui.panel(0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT, OUTER_BACKGROUND);
        ui.panel(0f, MAP_Y - 64f, WINDOW_WIDTH, mapModel.mapHeight() + 128f, new Color(0.17f, 0.12f, 0.13f, 1f));
        ui.panel(0f, 0f, WINDOW_WIDTH, 124f, new Color(0.09f, 0.07f, 0.08f, 1f));
        ui.panel(0f, WINDOW_HEIGHT - 120f, WINDOW_WIDTH, 120f, new Color(0.09f, 0.07f, 0.08f, 1f));
    }

    private void drawMapFrame() {
        ui.panel(mapX - 16f, MAP_Y - 16f, mapModel.mapWidth() + 32f, mapModel.mapHeight() + 32f, FRAME_COLOR);
        ui.panelOutline(mapX - 16f, MAP_Y - 16f, mapModel.mapWidth() + 32f, mapModel.mapHeight() + 32f, WINDOW_EDGE);
        ui.panel(mapX - 6f, MAP_Y - 6f, mapModel.mapWidth() + 12f, mapModel.mapHeight() + 12f, new Color(0.24f, 0.18f, 0.17f, 0.52f));
    }

    private void drawMapTiles() {
        for (int row = 0; row < mapModel.rowCount(); row += 1) {
            for (int column = 0; column < mapModel.columnCount(); column += 1) {
                drawTile(column, row, mapModel.tileAt(row, column));
            }
        }
    }

    private void drawTile(int column, int row, char tile) {
        float x = tileToScreenX(column);
        float y = tileToScreenY(row);
        boolean even = (column + row) % 2 == 0;

        switch (tile) {
            case '#' -> {
                ui.panel(x, y, HubMapModel.TILE_SIZE, HubMapModel.TILE_SIZE, WALL_COLOR);
                ui.panel(x, y + HubMapModel.TILE_SIZE - 12f, HubMapModel.TILE_SIZE, 12f, WALL_TRIM);
                ui.panel(x, y, HubMapModel.TILE_SIZE, 8f, new Color(0.34f, 0.24f, 0.24f, 1f));
            }
            case '=' -> ui.panel(x, y, HubMapModel.TILE_SIZE, HubMapModel.TILE_SIZE, even ? RUG_LIGHT : RUG_DARK);
            default -> ui.panel(x, y, HubMapModel.TILE_SIZE, HubMapModel.TILE_SIZE, even ? FLOOR_LIGHT : FLOOR_DARK);
        }

        ui.panelOutline(x, y, HubMapModel.TILE_SIZE, HubMapModel.TILE_SIZE, TILE_OUTLINE);
    }

    private void drawMapEvents() {
        HubMapEvent suggestedEvent = mapModel.findSuggestedEvent(progress);
        HubMapEvent facingEvent = mapModel.findFacingEvent();

        for (HubMapEvent event : mapModel.events()) {
            float x = tileToScreenX(event.tileX());
            float y = tileToScreenY(event.tileY());
            boolean unlocked = progress.isUnlocked(event.id());
            boolean completed = progress.isCompleted(event.id());
            boolean suggested = suggestedEvent != null && suggestedEvent.id() == event.id();
            boolean facing = facingEvent != null && facingEvent.id() == event.id();

            ui.panel(x + 10f, y + 4f, HubMapModel.TILE_SIZE - 20f, 8f, new Color(0f, 0f, 0f, 0.18f));
            drawEventObject(event.visual(), x, y, unlocked, completed);

            Color outline = completed ? TEXT_MINT : suggested ? TEXT_ACCENT : unlocked ? WINDOW_EDGE : TEXT_MUTED;
            if (facing) {
                outline = TEXT_ACCENT;
            }
            ui.panelOutline(x + 4f, y + 4f, HubMapModel.TILE_SIZE - 8f, HubMapModel.TILE_SIZE - 8f, outline);

            if (navigator.showsOperationalUi()) {
                ui.panel(x - 2f, y + HubMapModel.TILE_SIZE + 4f, 62f, 18f, WINDOW_BACKGROUND);
                ui.line(event.title(), x + 4f, y + HubMapModel.TILE_SIZE + 17f, 0.46f, unlocked ? TEXT_LIGHT : TEXT_MUTED);
            }
        }
    }

    private void drawEventObject(HubEventVisual visual, float x, float y, boolean unlocked, boolean completed) {
        Color base = resolveEventBaseColor(visual, unlocked);
        Color accent = resolveEventAccentColor(visual, unlocked, completed);

        switch (visual) {
            case DESK -> {
                ui.panel(x + 8f, y + 8f, 32f, 14f, base);
                ui.panel(x + 12f, y + 22f, 24f, 12f, accent);
                ui.panel(x + 20f, y + 34f, 8f, 8f, new Color(0.22f, 0.17f, 0.18f, 1f));
            }
            case DOOR -> {
                ui.panel(x + 12f, y + 6f, 24f, 34f, base);
                ui.panel(x + 16f, y + 10f, 16f, 24f, accent);
                ui.panel(x + 30f, y + 22f, 3f, 3f, TEXT_LIGHT);
            }
            case CAKE -> {
                ui.panel(x + 10f, y + 8f, 28f, 10f, new Color(0.78f, 0.64f, 0.48f, 1f));
                ui.panel(x + 12f, y + 18f, 24f, 16f, base);
                ui.panel(x + 20f, y + 34f, 2f, 8f, accent);
                ui.panel(x + 26f, y + 34f, 2f, 8f, accent);
            }
            case PHOTO -> {
                ui.panel(x + 21f, y + 8f, 6f, 30f, base);
                ui.panel(x + 13f, y + 18f, 22f, 14f, accent);
                ui.panel(x + 12f, y + 6f, 8f, 4f, base);
                ui.panel(x + 28f, y + 6f, 8f, 4f, base);
            }
            case MAILBOX -> {
                ui.panel(x + 14f, y + 10f, 20f, 22f, base);
                ui.panel(x + 12f, y + 30f, 24f, 8f, accent);
                ui.panel(x + 18f, y + 20f, 12f, 4f, TEXT_LIGHT);
            }
            case STAGE -> {
                ui.panel(x + 8f, y + 8f, 32f, 8f, accent);
                ui.panel(x + 12f, y + 16f, 24f, 18f, base);
                ui.panel(x + 8f, y + 34f, 32f, 6f, accent);
            }
        }
    }

    private Color resolveEventBaseColor(HubEventVisual visual, boolean unlocked) {
        if (!unlocked) {
            return new Color(0.42f, 0.38f, 0.38f, 1f);
        }
        return switch (visual) {
            case DESK -> new Color(0.69f, 0.53f, 0.41f, 1f);
            case DOOR -> new Color(0.57f, 0.39f, 0.33f, 1f);
            case CAKE -> new Color(0.96f, 0.76f, 0.84f, 1f);
            case PHOTO -> new Color(0.52f, 0.62f, 0.68f, 1f);
            case MAILBOX -> new Color(0.78f, 0.47f, 0.59f, 1f);
            case STAGE -> new Color(0.53f, 0.35f, 0.52f, 1f);
        };
    }

    private Color resolveEventAccentColor(HubEventVisual visual, boolean unlocked, boolean completed) {
        if (completed) {
            return TEXT_MINT;
        }
        if (!unlocked) {
            return new Color(0.55f, 0.50f, 0.50f, 1f);
        }
        return switch (visual) {
            case DESK -> new Color(0.42f, 0.70f, 0.74f, 1f);
            case DOOR -> new Color(0.84f, 0.68f, 0.45f, 1f);
            case CAKE -> new Color(0.98f, 0.88f, 0.55f, 1f);
            case PHOTO -> new Color(0.81f, 0.90f, 0.95f, 1f);
            case MAILBOX -> new Color(0.96f, 0.88f, 0.68f, 1f);
            case STAGE -> new Color(0.96f, 0.70f, 0.77f, 1f);
        };
    }

    private void drawPlayer() {
        float x = playerDrawX;
        float y = playerDrawY;

        ui.panel(x + 10f, y + 4f, HubMapModel.TILE_SIZE - 20f, 8f, new Color(0f, 0f, 0f, 0.20f));
        ui.panel(x + 18f, y + 10f, 12f, 12f, new Color(0.96f, 0.82f, 0.70f, 1f));
        ui.panel(x + 14f, y + 20f, 20f, 14f, new Color(0.97f, 0.90f, 0.55f, 1f));
        ui.panel(x + 12f, y + 30f, 24f, 10f, new Color(0.88f, 0.41f, 0.57f, 1f));
        ui.panel(x + 10f, y + 36f, 8f, 6f, new Color(0.97f, 0.90f, 0.55f, 1f));
        ui.panel(x + 30f, y + 36f, 8f, 6f, new Color(0.97f, 0.90f, 0.55f, 1f));
        drawFacingAccent(x, y);
        ui.panelOutline(x + 10f, y + 10f, 28f, 30f, TEXT_LIGHT);
    }

    private void drawFacingAccent(float x, float y) {
        switch (mapModel.facing()) {
            case UP -> ui.panel(x + 20f, y + 24f, 8f, 4f, TEXT_ACCENT);
            case DOWN -> ui.panel(x + 20f, y + 12f, 8f, 4f, TEXT_ACCENT);
            case LEFT -> ui.panel(x + 14f, y + 18f, 4f, 8f, TEXT_ACCENT);
            case RIGHT -> ui.panel(x + 30f, y + 18f, 4f, 8f, TEXT_ACCENT);
        }
    }

    private void drawLocationPlate() {
        ui.panel(54f, WINDOW_HEIGHT - 82f, 278f, 54f, WINDOW_BACKGROUND);
        ui.panelOutline(54f, WINDOW_HEIGHT - 82f, 278f, 54f, WINDOW_EDGE);
        ui.line("생일 방송 준비방", 76f, WINDOW_HEIGHT - 48f, 0.94f, TEXT_LIGHT);
        ui.line("전통 2D 쯔꾸르형 허브", 76f, WINDOW_HEIGHT - 68f, 0.62f, TEXT_MUTED);
    }

    private void drawFocusPlate() {
        if (dialogueState != null) {
            return;
        }

        HubMapEvent facingEvent = mapModel.findFacingEvent();
        if (facingEvent == null) {
            return;
        }

        boolean unlocked = progress.isUnlocked(facingEvent.id());
        float x = WINDOW_WIDTH - 302f;
        float y = WINDOW_HEIGHT - 82f;
        ui.panel(x, y, 248f, 48f, WINDOW_BACKGROUND);
        ui.panelOutline(x, y, 248f, 48f, unlocked ? TEXT_ACCENT : WINDOW_EDGE);
        ui.line(facingEvent.title(), x + 18f, y + 30f, 0.86f, TEXT_LIGHT);
        ui.line(unlocked ? "정면 조사 가능" : "아직 잠겨 있음", x + 18f, y + 13f, 0.56f, unlocked ? TEXT_ACCENT : TEXT_MUTED);
    }

    private void drawDebugPlate() {
        float x = WINDOW_WIDTH - 456f;
        float y = WINDOW_HEIGHT - 186f;
        ui.panel(x, y, 402f, 144f, WINDOW_BACKGROUND);
        ui.panelOutline(x, y, 402f, 144f, WINDOW_EDGE);
        ui.line("test mode", x + 18f, y + 118f, 0.82f, TEXT_ACCENT);
        ui.line("좌표 " + mapModel.playerTileX() + ", " + mapModel.playerTileY(), x + 18f, y + 88f, 0.78f, TEXT_LIGHT);
        ui.line("바라보는 방향 " + mapModel.facing().label(), x + 18f, y + 62f, 0.78f, TEXT_LIGHT);
        ui.paragraph(progress.getNextObjective(), x + 18f, y + 34f, 366f, 0.62f, TEXT_MUTED);

        HubMapEvent facingEvent = mapModel.findFacingEvent();
        if (facingEvent != null) {
            ui.line("앞 타일 " + facingEvent.title(), x + 210f, y + 118f, 0.72f, TEXT_MINT);
        }
    }

    private float tileToScreenX(int tileX) {
        return mapX + (tileX * HubMapModel.TILE_SIZE);
    }

    private float tileToScreenY(int tileY) {
        return MAP_Y + ((mapModel.rowCount() - tileY - 1) * HubMapModel.TILE_SIZE);
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

    private String buildFontCharacters() {
        Set<Character> characters = new LinkedHashSet<>();
        appendCharacters(characters, FreeTypeFontGenerator.DEFAULT_CHARS);

        List<String> texts = new ArrayList<>();
        texts.add(progress.getNextObjective());
        texts.add(progress.getEndingTitle());
        texts.add(progress.getEndingLine());
        texts.addAll(resolveInitialNoticePages());
        texts.addAll(List.of(
                "치즈냥",
                "생일 방송 준비방",
                "전통 2D 쯔꾸르형 허브",
                "정면 조사 가능",
                "아직 잠겨 있음",
                "test mode",
                "좌표",
                "바라보는 방향",
                "앞 타일",
                "방향키로 한 칸씩 움직이고, 조사하고 싶은 오브젝트 정면에서 ENTER나 SPACE를 누르자."
        ));

        for (HubMapEvent event : mapModel.events()) {
            texts.add(event.title());
            texts.add(event.lockedNotice());
            texts.addAll(event.interactionLines());
        }

        for (String text : texts) {
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
    public void dispose() {
        batch.dispose();
        font.dispose();
        pixelTexture.dispose();
        portraitTexture.dispose();
    }

    private static final class DialogueState {
        private final String speaker;
        private final List<String> pages;
        private final Runnable onFinish;
        private int pageIndex;

        private DialogueState(String speaker, List<String> pages, Runnable onFinish) {
            this.speaker = speaker;
            this.pages = pages;
            this.onFinish = onFinish;
            this.pageIndex = 0;
        }
    }
}
