package org.nowstart.zunyang.partypanic.adapter.in.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DialogueWindowRenderer {
    private static final Color WINDOW_BACKGROUND = new Color(0.15f, 0.11f, 0.13f, 0.96f);
    private static final Color WINDOW_EDGE = new Color(0.97f, 0.88f, 0.77f, 0.92f);
    private static final Color NAME_WINDOW = new Color(0.37f, 0.22f, 0.27f, 0.96f);

    private final PixelUiRenderer ui;

    public void draw(
            Texture portraitTexture,
            float sceneTime,
            float openProgress,
            String speaker,
            String text,
            Color textColor,
            String topRightText,
            Color topRightColor
    ) {
        float open = Math.max(0.22f, openProgress);
        float lift = (1f - open) * 18f;
        float portraitX = 44f;
        float portraitY = 32f - lift;
        float portraitWidth = 214f;
        float portraitHeight = 214f;
        float messageX = 280f;
        float messageY = 32f - lift;
        float messageWidth = 1276f;
        float messageHeight = 214f;

        ui.panel(portraitX, portraitY, portraitWidth, portraitHeight, WINDOW_BACKGROUND);
        ui.panelOutline(portraitX, portraitY, portraitWidth, portraitHeight, WINDOW_EDGE);
        ui.textureFit(portraitTexture, portraitX + 10f, portraitY + 10f, portraitWidth - 20f, portraitHeight - 20f);

        ui.panel(messageX, messageY, messageWidth, messageHeight, WINDOW_BACKGROUND);
        ui.panelOutline(messageX, messageY, messageWidth, messageHeight, WINDOW_EDGE);
        ui.panel(messageX + 20f, messageY + messageHeight - 38f, 184f, 30f, NAME_WINDOW);
        ui.panelOutline(messageX + 20f, messageY + messageHeight - 38f, 184f, 30f, WINDOW_EDGE);
        ui.line(speaker, messageX + 38f, messageY + messageHeight - 18f, 0.76f, textColor);

        ui.paragraph(text, messageX + 28f, messageY + 146f, messageWidth - 56f, 0.98f, textColor);
        drawContinueIndicator(sceneTime, messageX + messageWidth - 48f, messageY + 24f, textColor);

        if (topRightText == null || topRightText.isBlank()) {
            return;
        }
        ui.line(topRightText, messageX + messageWidth - 126f, messageY + messageHeight - 18f, 0.60f, topRightColor);
    }

    private void drawContinueIndicator(float sceneTime, float x, float y, Color color) {
        float pulse = 0.76f + (0.24f * ((MathUtils.sin(sceneTime * 5f) * 0.5f) + 0.5f));
        Color indicatorColor = ui.withAlpha(color, pulse);
        ui.panel(x, y, 10f, 4f, indicatorColor);
        ui.panel(x + 14f, y, 10f, 4f, indicatorColor);
        ui.panel(x + 28f, y, 10f, 4f, indicatorColor);
    }
}
