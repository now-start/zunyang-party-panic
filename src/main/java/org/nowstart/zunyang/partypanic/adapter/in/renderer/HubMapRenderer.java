package org.nowstart.zunyang.partypanic.adapter.in.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameViewportConfig;
import org.nowstart.zunyang.partypanic.domain.event.EventVisual;
import org.nowstart.zunyang.partypanic.domain.event.GameEvent;
import org.nowstart.zunyang.partypanic.domain.model.GameMap;
import org.nowstart.zunyang.partypanic.domain.model.GameState;
import org.nowstart.zunyang.partypanic.domain.policy.EventResolver;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

public final class HubMapRenderer implements Disposable {
    public static final float WINDOW_WIDTH = GameViewportConfig.WORLD_WIDTH;
    public static final float WINDOW_HEIGHT = GameViewportConfig.WORLD_HEIGHT;
    public static final float MAP_Y = 164f;
    public static final int TILE_SIZE = 48;

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

    private final PixelUiRenderer ui;
    private final GameProgress progress;
    private final EventResolver eventResolver;
    private final GameMap gameMap;
    private final float mapX;
    private final Array<Texture> ownedTextures = new Array<>();
    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer tiledRenderer;
    private final StaticTiledMapTile floorLightTile;
    private final StaticTiledMapTile floorDarkTile;
    private final StaticTiledMapTile wallTile;
    private final StaticTiledMapTile rugLightTile;
    private final StaticTiledMapTile rugDarkTile;

    public HubMapRenderer(PixelUiRenderer ui, GameProgress progress, EventResolver eventResolver, GameMap gameMap, float mapX) {
        this.ui = ui;
        this.progress = progress;
        this.eventResolver = eventResolver;
        this.gameMap = gameMap;
        this.mapX = mapX;
        this.floorLightTile = createTile(createFloorTexture(FLOOR_LIGHT, new Color(0.87f, 0.79f, 0.72f, 1f)));
        this.floorDarkTile = createTile(createFloorTexture(FLOOR_DARK, new Color(0.84f, 0.76f, 0.69f, 1f)));
        this.wallTile = createTile(createWallTexture());
        this.rugLightTile = createTile(createRugTexture(RUG_LIGHT, new Color(0.89f, 0.52f, 0.67f, 0.75f)));
        this.rugDarkTile = createTile(createRugTexture(RUG_DARK, new Color(0.82f, 0.46f, 0.61f, 0.75f)));
        this.tiledMap = createTiledMap();
        this.tiledRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1f);
    }

    public void drawBackdropAndFrame() {
        drawBackdrop();
        drawMapFrame();
    }

    public void renderMap(OrthographicCamera camera) {
        tiledRenderer.setView(camera);
        tiledRenderer.render();
    }

    public void drawOverlay(GameState currentState, float playerDrawX, float playerDrawY, boolean operationalUi) {
        drawMapEvents(currentState, operationalUi);
        drawPlayer(currentState, playerDrawX, playerDrawY);
        drawLocationPlate();
        if (operationalUi) {
            drawDebugPlate(currentState);
        } else {
            drawFocusPlate(currentState);
        }
    }

    @Override
    public void dispose() {
        tiledRenderer.dispose();
        tiledMap.dispose();
        for (Texture texture : ownedTextures) {
            texture.dispose();
        }
        ownedTextures.clear();
    }

    private void drawBackdrop() {
        ui.panel(0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT, OUTER_BACKGROUND);
        ui.panel(0f, MAP_Y - 64f, WINDOW_WIDTH, mapHeight() + 128f, new Color(0.17f, 0.12f, 0.13f, 1f));
        ui.panel(0f, 0f, WINDOW_WIDTH, 124f, new Color(0.09f, 0.07f, 0.08f, 1f));
        ui.panel(0f, WINDOW_HEIGHT - 120f, WINDOW_WIDTH, 120f, new Color(0.09f, 0.07f, 0.08f, 1f));
    }

    private void drawMapFrame() {
        ui.panel(mapX - 16f, MAP_Y - 16f, mapWidth() + 32f, mapHeight() + 32f, FRAME_COLOR);
        ui.panelOutline(mapX - 16f, MAP_Y - 16f, mapWidth() + 32f, mapHeight() + 32f, WINDOW_EDGE);
        ui.panel(mapX - 6f, MAP_Y - 6f, mapWidth() + 12f, mapHeight() + 12f, new Color(0.24f, 0.18f, 0.17f, 0.52f));
    }

    private void drawMapEvents(GameState currentState, boolean operationalUi) {
        GameEvent suggestedEvent = eventResolver.findSuggestedEvent(currentState, progress).orElse(null);
        GameEvent facingEvent = eventResolver.findFacingEvent(currentState).orElse(null);

        for (GameEvent event : gameMap.events()) {
            float x = tileToScreenX(event.position().x());
            float y = tileToScreenY(event.position().y());
            boolean unlocked = progress.isUnlocked(event.activityId());
            boolean completed = progress.isCompleted(event.activityId());
            boolean suggested = suggestedEvent != null && suggestedEvent.activityId() == event.activityId();
            boolean facing = facingEvent != null && facingEvent.activityId() == event.activityId();

            ui.panel(x + 10f, y + 4f, TILE_SIZE - 20f, 8f, new Color(0f, 0f, 0f, 0.18f));
            drawEventObject(event.visual(), x, y, unlocked, completed);

            Color outline = completed ? TEXT_MINT : suggested ? TEXT_ACCENT : unlocked ? WINDOW_EDGE : TEXT_MUTED;
            if (facing) {
                outline = TEXT_ACCENT;
            }
            ui.panelOutline(x + 4f, y + 4f, TILE_SIZE - 8f, TILE_SIZE - 8f, outline);

            if (operationalUi) {
                ui.panel(x - 2f, y + TILE_SIZE + 4f, 62f, 18f, WINDOW_BACKGROUND);
                ui.line(event.title(), x + 4f, y + TILE_SIZE + 17f, 0.46f, unlocked ? TEXT_LIGHT : TEXT_MUTED);
            }
        }
    }

    private void drawEventObject(EventVisual visual, float x, float y, boolean unlocked, boolean completed) {
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

    private Color resolveEventBaseColor(EventVisual visual, boolean unlocked) {
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

    private Color resolveEventAccentColor(EventVisual visual, boolean unlocked, boolean completed) {
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

    private void drawPlayer(GameState currentState, float playerDrawX, float playerDrawY) {
        float x = playerDrawX;
        float y = playerDrawY;

        ui.panel(x + 10f, y + 4f, TILE_SIZE - 20f, 8f, new Color(0f, 0f, 0f, 0.20f));
        ui.panel(x + 18f, y + 10f, 12f, 12f, new Color(0.96f, 0.82f, 0.70f, 1f));
        ui.panel(x + 14f, y + 20f, 20f, 14f, new Color(0.97f, 0.90f, 0.55f, 1f));
        ui.panel(x + 12f, y + 30f, 24f, 10f, new Color(0.88f, 0.41f, 0.57f, 1f));
        ui.panel(x + 10f, y + 36f, 8f, 6f, new Color(0.97f, 0.90f, 0.55f, 1f));
        ui.panel(x + 30f, y + 36f, 8f, 6f, new Color(0.97f, 0.90f, 0.55f, 1f));
        drawFacingAccent(currentState, x, y);
        ui.panelOutline(x + 10f, y + 10f, 28f, 30f, TEXT_LIGHT);
    }

    private void drawFacingAccent(GameState currentState, float x, float y) {
        switch (currentState.player().facing()) {
            case UP -> ui.panel(x + 20f, y + 24f, 8f, 4f, TEXT_ACCENT);
            case DOWN -> ui.panel(x + 20f, y + 12f, 8f, 4f, TEXT_ACCENT);
            case LEFT -> ui.panel(x + 14f, y + 18f, 4f, 8f, TEXT_ACCENT);
            case RIGHT -> ui.panel(x + 30f, y + 18f, 4f, 8f, TEXT_ACCENT);
        }
    }

    private void drawLocationPlate() {
        ui.panel(54f, WINDOW_HEIGHT - 82f, 278f, 54f, WINDOW_BACKGROUND);
        ui.panelOutline(54f, WINDOW_HEIGHT - 82f, 278f, 54f, WINDOW_EDGE);
        ui.titleLine("생일 방송 준비방", 76f, WINDOW_HEIGHT - 48f, 0.94f, TEXT_LIGHT);
        ui.line("전통 2D 쯔꾸르형 허브", 76f, WINDOW_HEIGHT - 68f, 0.62f, TEXT_MUTED);
    }

    private void drawFocusPlate(GameState currentState) {
        GameEvent facingEvent = eventResolver.findFacingEvent(currentState).orElse(null);
        if (facingEvent == null) {
            return;
        }

        boolean unlocked = progress.isUnlocked(facingEvent.activityId());
        float x = WINDOW_WIDTH - 302f;
        float y = WINDOW_HEIGHT - 82f;
        ui.panel(x, y, 248f, 48f, WINDOW_BACKGROUND);
        ui.panelOutline(x, y, 248f, 48f, unlocked ? TEXT_ACCENT : WINDOW_EDGE);
        ui.line(facingEvent.title(), x + 18f, y + 30f, 0.86f, TEXT_LIGHT);
        ui.line(unlocked ? "정면 조사 가능" : "아직 잠겨 있음", x + 18f, y + 13f, 0.56f, unlocked ? TEXT_ACCENT : TEXT_MUTED);
    }

    private void drawDebugPlate(GameState currentState) {
        float x = WINDOW_WIDTH - 456f;
        float y = WINDOW_HEIGHT - 186f;
        ui.panel(x, y, 402f, 144f, WINDOW_BACKGROUND);
        ui.panelOutline(x, y, 402f, 144f, WINDOW_EDGE);
        ui.line("test mode", x + 18f, y + 118f, 0.82f, TEXT_ACCENT);
        ui.line("좌표 " + currentState.player().position().x() + ", " + currentState.player().position().y(), x + 18f, y + 88f, 0.78f, TEXT_LIGHT);
        ui.line("바라보는 방향 " + currentState.player().facing().getLabel(), x + 18f, y + 62f, 0.78f, TEXT_LIGHT);
        ui.paragraph(progress.getNextObjective(), x + 18f, y + 34f, 366f, 0.62f, TEXT_MUTED);

        GameEvent facingEvent = eventResolver.findFacingEvent(currentState).orElse(null);
        if (facingEvent != null) {
            ui.line("앞 타일 " + facingEvent.title(), x + 210f, y + 118f, 0.72f, TEXT_MINT);
        }
    }

    private TiledMap createTiledMap() {
        TiledMap map = new TiledMap();
        TiledMapTileLayer baseLayer = new TiledMapTileLayer(gameMap.columnCount(), gameMap.rowCount(), TILE_SIZE, TILE_SIZE);
        baseLayer.setOffsetX(mapX);
        baseLayer.setOffsetY(MAP_Y);

        for (int row = 0; row < gameMap.rowCount(); row += 1) {
            int layerY = gameMap.rowCount() - row - 1;
            for (int column = 0; column < gameMap.columnCount(); column += 1) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(resolveTile(gameMap.tileAt(row, column), column, row));
                baseLayer.setCell(column, layerY, cell);
            }
        }

        map.getLayers().add(baseLayer);
        return map;
    }

    private StaticTiledMapTile resolveTile(char tile, int column, int row) {
        boolean even = (column + row) % 2 == 0;
        return switch (tile) {
            case '#' -> wallTile;
            case '=' -> even ? rugLightTile : rugDarkTile;
            default -> even ? floorLightTile : floorDarkTile;
        };
    }

    private StaticTiledMapTile createTile(Texture texture) {
        ownedTextures.add(texture);
        return new StaticTiledMapTile(new TextureRegion(texture));
    }

    private Texture createFloorTexture(Color fillColor, Color accentColor) {
        Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(fillColor);
        pixmap.fill();
        pixmap.setColor(accentColor);
        pixmap.fillRectangle(0, TILE_SIZE - 10, TILE_SIZE, 10);
        pixmap.fillRectangle(0, 0, 6, TILE_SIZE);
        pixmap.setColor(TILE_OUTLINE);
        pixmap.drawRectangle(0, 0, TILE_SIZE, TILE_SIZE);
        return toTexture(pixmap);
    }

    private Texture createWallTexture() {
        Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(WALL_COLOR);
        pixmap.fill();
        pixmap.setColor(WALL_TRIM);
        pixmap.fillRectangle(0, TILE_SIZE - 12, TILE_SIZE, 12);
        pixmap.setColor(new Color(0.34f, 0.24f, 0.24f, 1f));
        pixmap.fillRectangle(0, 0, TILE_SIZE, 8);
        pixmap.setColor(TILE_OUTLINE);
        pixmap.drawRectangle(0, 0, TILE_SIZE, TILE_SIZE);
        return toTexture(pixmap);
    }

    private Texture createRugTexture(Color fillColor, Color stripeColor) {
        Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
        pixmap.setColor(fillColor);
        pixmap.fill();
        pixmap.setColor(stripeColor);
        for (int x = 8; x < TILE_SIZE; x += 12) {
            pixmap.fillRectangle(x, 0, 4, TILE_SIZE);
        }
        pixmap.setColor(new Color(fillColor.r * 0.92f, fillColor.g * 0.92f, fillColor.b * 0.92f, 1f));
        pixmap.fillRectangle(0, TILE_SIZE - 6, TILE_SIZE, 6);
        pixmap.setColor(TILE_OUTLINE);
        pixmap.drawRectangle(0, 0, TILE_SIZE, TILE_SIZE);
        return toTexture(pixmap);
    }

    private Texture toTexture(Pixmap pixmap) {
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
        return texture;
    }

    private float mapWidth() {
        return gameMap.columnCount() * TILE_SIZE;
    }

    private float mapHeight() {
        return gameMap.rowCount() * TILE_SIZE;
    }

    private float tileToScreenX(int tileX) {
        return mapX + (tileX * TILE_SIZE);
    }

    private float tileToScreenY(int tileY) {
        return MAP_Y + ((gameMap.rowCount() - tileY - 1) * TILE_SIZE);
    }
}
