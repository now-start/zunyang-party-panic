package org.nowstart.zunyang.partypanic.adapter.in.hub.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.DebugHudRenderer;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleFontId;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleFontLibrary;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleIconId;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleIconLibrary;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleTextureId;
import org.nowstart.zunyang.partypanic.adapter.in.common.ui.SampleTextureLibrary;
import org.nowstart.zunyang.partypanic.application.dto.command.MoveHubActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.HubHotspotView;
import org.nowstart.zunyang.partypanic.application.dto.result.HubViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.InteractHubUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.LoadHubUseCase;
import org.nowstart.zunyang.partypanic.application.port.in.MoveHubActorUseCase;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.common.Direction;

public final class HubPreviewScreen extends ScreenAdapter {

    private static final float MARGIN = 48f;
    private static final float TOP_PANEL_HEIGHT = 140f;
    private static final float BOTTOM_PANEL_HEIGHT = 170f;
    private static final float HINT_ICON_SIZE = 26f;

    private final LoadHubUseCase loadHubUseCase;
    private final MoveHubActorUseCase moveHubActorUseCase;
    private final InteractHubUseCase interactHubUseCase;
    private final Consumer<ChapterId> onOpenChapter;
    private final Runnable onRestartSession;
    private final SpriteBatch batch = new SpriteBatch();
    private final ScreenViewport viewport = new ScreenViewport();
    private final SampleFontLibrary fontLibrary = new SampleFontLibrary();
    private final BitmapFont titleFont = fontLibrary.font(SampleFontId.TITLE);
    private final BitmapFont bodyFont = fontLibrary.font(SampleFontId.BODY);
    private final SampleIconLibrary iconLibrary = new SampleIconLibrary();
    private final SampleTextureLibrary textureLibrary = new SampleTextureLibrary();
    private final DebugHudRenderer debugHudRenderer = new DebugHudRenderer();
    private final Map<ChapterId, SampleTextureId> textureByChapter = new EnumMap<>(ChapterId.class);

    private HubViewResult hubView;
    private boolean debugHudVisible = true;

    public HubPreviewScreen(
        LoadHubUseCase loadHubUseCase,
        MoveHubActorUseCase moveHubActorUseCase,
        InteractHubUseCase interactHubUseCase,
        Consumer<ChapterId> onOpenChapter,
        Runnable onRestartSession
    ) {
        this.loadHubUseCase = loadHubUseCase;
        this.moveHubActorUseCase = moveHubActorUseCase;
        this.interactHubUseCase = interactHubUseCase;
        this.onOpenChapter = onOpenChapter;
        this.onRestartSession = onRestartSession;
        this.textureByChapter.put(ChapterId.SIGNAL, SampleTextureId.SIGNAL_CARD);
        this.textureByChapter.put(ChapterId.PROPS, SampleTextureId.PROPS_CARD);
        this.textureByChapter.put(ChapterId.CENTERPIECE, SampleTextureId.CENTERPIECE_CARD);
        this.textureByChapter.put(ChapterId.PHOTO, SampleTextureId.PHOTO_CARD);
        this.textureByChapter.put(ChapterId.HANDOVER, SampleTextureId.HANDOVER_CARD);
        this.textureByChapter.put(ChapterId.MESSAGE, SampleTextureId.MESSAGE_CARD);
        this.textureByChapter.put(ChapterId.FINALE, SampleTextureId.FINALE_STAGE);
    }

    @Override
    public void show() {
        this.hubView = loadHubUseCase.load();
    }

    @Override
    public void render(float delta) {
        handleInput();
        ScreenUtils.clear(0.08f, 0.09f, 0.12f, 1f);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(SampleTextureId.HUB_BACKGROUND), 0f, 0f, worldWidth, worldHeight);

        drawHeader(worldHeight);
        drawGrid(worldWidth, worldHeight);
        drawFooter(worldWidth);
        if (debugHudVisible) {
            drawDebugHud(worldWidth, worldHeight);
        }
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugHudVisible = !debugHudVisible;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            onRestartSession.run();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            hubView = moveHubActorUseCase.move(new MoveHubActorCommand(Direction.LEFT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            hubView = moveHubActorUseCase.move(new MoveHubActorCommand(Direction.RIGHT));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            hubView = moveHubActorUseCase.move(new MoveHubActorCommand(Direction.UP));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            hubView = moveHubActorUseCase.move(new MoveHubActorCommand(Direction.DOWN));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Z)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            hubView = interactHubUseCase.interact();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.X) && hubView.activeHotspotId() != null) {
            onOpenChapter.accept(ChapterId.valueOf(hubView.activeHotspotId()));
        }
    }

    private void drawHeader(float worldHeight) {
        titleFont.draw(batch, "Hub Preview", MARGIN, worldHeight - 36f);
        float hintY = worldHeight - 106f;
        drawControlHint(MARGIN, hintY, SampleIconId.MOVE, "arrow");
        drawControlHint(MARGIN + 150f, hintY, SampleIconId.INTERACT, "z/enter");
        drawControlHint(MARGIN + 320f, hintY, SampleIconId.OPEN_CHAPTER, "x");
        drawControlHint(MARGIN + 420f, hintY, SampleIconId.DEBUG, "f3");
        drawControlHint(MARGIN + 520f, hintY, SampleIconId.RESTART, "f5");
        bodyFont.draw(batch, "semantic keys choose window skins, fonts, and icons before falling back to placeholders", MARGIN, worldHeight - 132f);
    }

    private void drawGrid(float worldWidth, float worldHeight) {
        float gridBottom = MARGIN + BOTTOM_PANEL_HEIGHT;
        float gridTop = worldHeight - TOP_PANEL_HEIGHT;
        float gridWidth = worldWidth - (MARGIN * 2f);
        float gridHeight = gridTop - gridBottom;
        float cellWidth = gridWidth / hubView.width();
        float cellHeight = gridHeight / hubView.height();
        float cardWidth = cellWidth * 0.82f;
        float cardHeight = cellHeight * 0.82f;

        for (HubHotspotView hotspot : hubView.hotspots()) {
            float x = MARGIN + (hotspot.x() * cellWidth) + ((cellWidth - cardWidth) / 2f);
            float y = gridBottom + (hotspot.y() * cellHeight) + ((cellHeight - cardHeight) / 2f);
            SampleTextureId textureId = hotspot.unlocked()
                ? textureByChapter.get(ChapterId.valueOf(hotspot.id()))
                : SampleTextureId.LOCKED_CARD;
            batch.draw(textureLibrary.region(textureId), x, y, cardWidth, cardHeight);
            bodyFont.draw(batch, hotspotLabel(hotspot), x + 12f, y + 22f);
        }

        drawPlayer(MARGIN, gridBottom, cellWidth, cellHeight);
    }

    private void drawPlayer(float gridLeft, float gridBottom, float cellWidth, float cellHeight) {
        float actorWidth = cellWidth * 0.48f;
        float actorHeight = cellHeight * 0.68f;
        float actorX = gridLeft + (hubView.actorX() * cellWidth) + ((cellWidth - actorWidth) / 2f);
        float actorY = gridBottom + (hubView.actorY() * cellHeight) + ((cellHeight - actorHeight) / 2f);
        TextureRegion helper = textureLibrary.region(SampleTextureId.HELPER_ACTOR);
        batch.draw(helper, actorX, actorY, actorWidth, actorHeight);
        bodyFont.draw(batch, "조력자", actorX - 4f, actorY - 8f);
        bodyFont.draw(batch, hubView.facing(), actorX - 4f, actorY + actorHeight + 18f);
    }

    private void drawFooter(float worldWidth) {
        float panelY = MARGIN;
        float panelHeight = BOTTOM_PANEL_HEIGHT - 24f;
        batch.draw(textureLibrary.region(SampleTextureId.MESSAGE_PANEL), MARGIN, panelY, worldWidth - (MARGIN * 2f), panelHeight);

        bodyFont.draw(batch, "active hotspot: " + valueOrDash(hubView.activeHotspotId()), MARGIN + 24f, panelY + panelHeight - 24f);
        bodyFont.draw(batch, "current message:", MARGIN + 24f, panelY + panelHeight - 60f);

        String message = hubView.currentMessage() == null ? "아직 조사하지 않았다." : hubView.currentMessage();
        bodyFont.draw(
            batch,
            message,
            MARGIN + 24f,
            panelY + 54f,
            worldWidth - (MARGIN * 2f) - 48f,
            Align.left,
            true
        );
    }

    private void drawControlHint(float x, float y, SampleIconId iconId, String label) {
        TextureRegion icon = iconLibrary.region(iconId);
        batch.draw(icon, x, y, HINT_ICON_SIZE, HINT_ICON_SIZE);
        bodyFont.draw(batch, label, x + HINT_ICON_SIZE + 10f, y + 20f);
    }

    private String valueOrDash(String value) {
        return value == null ? "-" : value;
    }

    private String hotspotLabel(HubHotspotView hotspot) {
        return hotspot.unlocked() ? hotspot.label() : hotspot.label() + " (잠김)";
    }

    private void drawDebugHud(float worldWidth, float worldHeight) {
        SampleTextureId activeCardTexture = activeCardTexture();
        String activeCardLine = activeCardTexture == null
            ? "card=-"
            : "card=" + activeCardTexture.name() + ":" + textureLibrary.debugSource(activeCardTexture);

        List<String> lines = List.of(
            "actor=(" + hubView.actorX() + "," + hubView.actorY() + ") facing=" + hubView.facing(),
            "active=" + valueOrDash(hubView.activeHotspotId()) + " unlocked=" + unlockedHotspotCount() + "/" + hubView.hotspots().size(),
            "phase=" + hubView.sessionPhase() + " completed=" + hubView.completedChapterCount(),
            "ending=" + valueOrDash(hubView.endingGradeTitle()),
            "placeholderArt=" + (hubView.placeholderArtEnabled() ? "on" : "off"),
            "bg=" + textureLibrary.debugSource(SampleTextureId.HUB_BACKGROUND)
                + " panel=" + textureLibrary.debugSource(SampleTextureId.MESSAGE_PANEL),
            "helper=" + textureLibrary.debugSource(SampleTextureId.HELPER_ACTOR) + " " + activeCardLine,
            "font=title:" + fontLibrary.debugSource(SampleFontId.TITLE)
                + " body:" + fontLibrary.debugSource(SampleFontId.BODY),
            "icon=move:" + iconLibrary.debugSource(SampleIconId.MOVE)
                + " inspect:" + iconLibrary.debugSource(SampleIconId.INTERACT)
        );

        debugHudRenderer.draw(
            batch,
            bodyFont,
            textureLibrary.region(SampleTextureId.MESSAGE_PANEL),
            worldWidth,
            worldHeight,
            "DEBUG HUB",
            lines
        );
    }

    private int unlockedHotspotCount() {
        return (int) hubView.hotspots().stream().filter(HubHotspotView::unlocked).count();
    }

    private SampleTextureId activeCardTexture() {
        if (hubView.activeHotspotId() == null) {
            return null;
        }
        return hubView.hotspots().stream()
            .filter(hotspot -> hotspot.id().equals(hubView.activeHotspotId()))
            .findFirst()
            .map(hotspot -> hotspot.unlocked()
                ? textureByChapter.get(ChapterId.valueOf(hotspot.id()))
                : SampleTextureId.LOCKED_CARD)
            .orElse(null);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        textureLibrary.dispose();
        iconLibrary.dispose();
        fontLibrary.dispose();
        batch.dispose();
    }
}
