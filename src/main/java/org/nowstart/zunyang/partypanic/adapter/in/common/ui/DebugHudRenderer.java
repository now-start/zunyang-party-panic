package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import java.util.List;

public final class DebugHudRenderer {

    private static final float HUD_WIDTH = 420f;
    private static final float HUD_MARGIN = 16f;
    private static final float HUD_PADDING = 14f;
    private static final float TITLE_GAP = 28f;
    private static final float LINE_HEIGHT = 22f;

    public void draw(
        SpriteBatch batch,
        BitmapFont font,
        TextureRegion panelRegion,
        float worldWidth,
        float worldHeight,
        String title,
        List<String> lines
    ) {
        float hudHeight = HUD_PADDING + TITLE_GAP + (lines.size() * LINE_HEIGHT) + HUD_PADDING;
        float hudX = worldWidth - HUD_WIDTH - HUD_MARGIN;
        float hudY = worldHeight - hudHeight - HUD_MARGIN;

        batch.setColor(1f, 1f, 1f, 0.94f);
        batch.draw(panelRegion, hudX, hudY, HUD_WIDTH, hudHeight);
        batch.setColor(Color.WHITE);

        font.draw(batch, title, hudX + HUD_PADDING, hudY + hudHeight - HUD_PADDING);
        float lineY = hudY + hudHeight - HUD_PADDING - TITLE_GAP;
        float textWidth = HUD_WIDTH - (HUD_PADDING * 2f);

        for (String line : lines) {
            font.draw(batch, line, hudX + HUD_PADDING, lineY, textWidth, Align.left, true);
            lineY -= LINE_HEIGHT;
        }
    }
}
