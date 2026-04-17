package org.nowstart.zunyang.partypanic.adapter.in.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
public final class MiniGameChrome {
    private MiniGameChrome() {
    }

    public static void drawBackdrop(PixelUiRenderer ui, Texture backgroundTexture) {
        ui.textureCover(backgroundTexture, 0f, 0f, MiniGameLayout.WINDOW_WIDTH, MiniGameLayout.WINDOW_HEIGHT);
        ui.panel(0f, 0f, MiniGameLayout.WINDOW_WIDTH, MiniGameLayout.WINDOW_HEIGHT, MiniGamePalette.OVERLAY_COLOR);
    }

    public static void drawFrames(PixelUiRenderer ui, Texture backgroundTexture) {
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
    }
}
