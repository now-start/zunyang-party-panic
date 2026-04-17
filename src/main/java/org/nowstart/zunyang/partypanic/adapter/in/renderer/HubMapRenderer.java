package org.nowstart.zunyang.partypanic.adapter.in.renderer;

import com.badlogic.gdx.graphics.Color;
import org.nowstart.zunyang.partypanic.application.service.EventResolver;
import org.nowstart.zunyang.partypanic.domain.event.EventVisual;
import org.nowstart.zunyang.partypanic.domain.event.GameEvent;
import org.nowstart.zunyang.partypanic.domain.model.GameState;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

public final class HubMapRenderer {
    public static final float WINDOW_WIDTH = 1600f;
    public static final float WINDOW_HEIGHT = 900f;
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

    public HubMapRenderer(PixelUiRenderer ui, GameProgress progress, EventResolver eventResolver) {
        this.ui = ui;
        this.progress = progress;
        this.eventResolver = eventResolver;
    }

    public void draw(GameState currentState, float mapX, float playerDrawX, float playerDrawY, boolean operationalUi) {
        drawBackdrop(currentState, mapX);
        drawMapFrame(currentState, mapX);
        drawMapTiles(currentState, mapX);
        drawMapEvents(currentState, mapX, operationalUi);
        drawPlayer(currentState, playerDrawX, playerDrawY);
        drawLocationPlate();
        if (operationalUi) {
            drawDebugPlate(currentState);
        } else {
            drawFocusPlate(currentState);
        }
    }

    private void drawBackdrop(GameState currentState, float mapX) {
        ui.panel(0f, 0f, WINDOW_WIDTH, WINDOW_HEIGHT, OUTER_BACKGROUND);
        ui.panel(0f, MAP_Y - 64f, WINDOW_WIDTH, mapHeight(currentState) + 128f, new Color(0.17f, 0.12f, 0.13f, 1f));
        ui.panel(0f, 0f, WINDOW_WIDTH, 124f, new Color(0.09f, 0.07f, 0.08f, 1f));
        ui.panel(0f, WINDOW_HEIGHT - 120f, WINDOW_WIDTH, 120f, new Color(0.09f, 0.07f, 0.08f, 1f));
    }

    private void drawMapFrame(GameState currentState, float mapX) {
        ui.panel(mapX - 16f, MAP_Y - 16f, mapWidth(currentState) + 32f, mapHeight(currentState) + 32f, FRAME_COLOR);
        ui.panelOutline(mapX - 16f, MAP_Y - 16f, mapWidth(currentState) + 32f, mapHeight(currentState) + 32f, WINDOW_EDGE);
        ui.panel(mapX - 6f, MAP_Y - 6f, mapWidth(currentState) + 12f, mapHeight(currentState) + 12f, new Color(0.24f, 0.18f, 0.17f, 0.52f));
    }

    private void drawMapTiles(GameState currentState, float mapX) {
        for (int row = 0; row < currentState.gameMap().rowCount(); row += 1) {
            for (int column = 0; column < currentState.gameMap().columnCount(); column += 1) {
                drawTile(currentState, mapX, column, row, currentState.gameMap().tileAt(row, column));
            }
        }
    }

    private void drawTile(GameState currentState, float mapX, int column, int row, char tile) {
        float x = tileToScreenX(mapX, column);
        float y = tileToScreenY(currentState, row);
        boolean even = (column + row) % 2 == 0;

        switch (tile) {
            case '#' -> {
                ui.panel(x, y, TILE_SIZE, TILE_SIZE, WALL_COLOR);
                ui.panel(x, y + TILE_SIZE - 12f, TILE_SIZE, 12f, WALL_TRIM);
                ui.panel(x, y, TILE_SIZE, 8f, new Color(0.34f, 0.24f, 0.24f, 1f));
            }
            case '=' -> ui.panel(x, y, TILE_SIZE, TILE_SIZE, even ? RUG_LIGHT : RUG_DARK);
            default -> ui.panel(x, y, TILE_SIZE, TILE_SIZE, even ? FLOOR_LIGHT : FLOOR_DARK);
        }

        ui.panelOutline(x, y, TILE_SIZE, TILE_SIZE, TILE_OUTLINE);
    }

    private void drawMapEvents(GameState currentState, float mapX, boolean operationalUi) {
        GameEvent suggestedEvent = eventResolver.findSuggestedEvent(currentState, progress).orElse(null);
        GameEvent facingEvent = eventResolver.findFacingEvent(currentState).orElse(null);

        for (GameEvent event : currentState.gameMap().events()) {
            float x = tileToScreenX(mapX, event.position().x());
            float y = tileToScreenY(currentState, event.position().y());
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

    private float mapWidth(GameState currentState) {
        return currentState.gameMap().columnCount() * TILE_SIZE;
    }

    private float mapHeight(GameState currentState) {
        return currentState.gameMap().rowCount() * TILE_SIZE;
    }

    private float tileToScreenX(float mapX, int tileX) {
        return mapX + (tileX * TILE_SIZE);
    }

    private float tileToScreenY(GameState currentState, int tileY) {
        return MAP_Y + ((currentState.gameMap().rowCount() - tileY - 1) * TILE_SIZE);
    }
}
