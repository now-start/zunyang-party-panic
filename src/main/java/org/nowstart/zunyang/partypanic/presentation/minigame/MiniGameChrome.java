package org.nowstart.zunyang.partypanic.presentation.minigame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.nowstart.zunyang.partypanic.presentation.ui.PixelUiRenderer;

public final class MiniGameChrome {
    private MiniGameChrome() {
    }

    public static void drawBackdrop(PixelUiRenderer ui, Texture backgroundTexture) {
        ui.textureCover(backgroundTexture, 0f, 0f, MiniGameLayout.WINDOW_WIDTH, MiniGameLayout.WINDOW_HEIGHT);
        ui.panel(0f, 0f, MiniGameLayout.WINDOW_WIDTH, MiniGameLayout.WINDOW_HEIGHT, MiniGamePalette.OVERLAY_COLOR);
    }

    public static void drawFrames(PixelUiRenderer ui, Texture backgroundTexture, boolean showsOperationalUi) {
        ui.panel(
                MiniGameLayout.STAGE_X - 8f,
                MiniGameLayout.STAGE_Y - 8f,
                MiniGameLayout.STAGE_WIDTH + 16f,
                MiniGameLayout.STAGE_HEIGHT + 16f,
                MiniGamePalette.STAGE_FRAME
        );
        ui.textureCover(
                backgroundTexture,
                MiniGameLayout.STAGE_X,
                MiniGameLayout.STAGE_Y,
                MiniGameLayout.STAGE_WIDTH,
                MiniGameLayout.STAGE_HEIGHT
        );
        ui.panel(
                MiniGameLayout.STAGE_X,
                MiniGameLayout.STAGE_Y,
                MiniGameLayout.STAGE_WIDTH,
                MiniGameLayout.STAGE_HEIGHT,
                new Color(0.03f, 0.02f, 0.03f, 0.16f)
        );
        ui.panelOutline(
                MiniGameLayout.STAGE_X - 1f,
                MiniGameLayout.STAGE_Y - 1f,
                MiniGameLayout.STAGE_WIDTH + 2f,
                MiniGameLayout.STAGE_HEIGHT + 2f,
                MiniGamePalette.BORDER_COLOR
        );

        if (!showsOperationalUi) {
            return;
        }

        ui.panel(
                MiniGameLayout.PANEL_X,
                MiniGameLayout.PANEL_Y,
                MiniGameLayout.PANEL_WIDTH,
                MiniGameLayout.PANEL_HEIGHT,
                MiniGamePalette.PANEL_STRONG
        );
        ui.panelOutline(
                MiniGameLayout.PANEL_X,
                MiniGameLayout.PANEL_Y,
                MiniGameLayout.PANEL_WIDTH,
                MiniGameLayout.PANEL_HEIGHT,
                MiniGamePalette.BORDER_COLOR
        );

        ui.panel(
                MiniGameLayout.COMMAND_X,
                MiniGameLayout.COMMAND_Y,
                MiniGameLayout.COMMAND_WIDTH,
                MiniGameLayout.COMMAND_HEIGHT,
                MiniGamePalette.PANEL_STRONG
        );
        ui.panelOutline(
                MiniGameLayout.COMMAND_X,
                MiniGameLayout.COMMAND_Y,
                MiniGameLayout.COMMAND_WIDTH,
                MiniGameLayout.COMMAND_HEIGHT,
                MiniGamePalette.BORDER_COLOR
        );
    }

    public static void drawCommandBar(PixelUiRenderer ui, String text) {
        ui.line(text, MiniGameLayout.COMMAND_X + 22f, MiniGameLayout.COMMAND_Y + 38f, 0.90f, MiniGamePalette.TEXT_PRIMARY);
    }
}
