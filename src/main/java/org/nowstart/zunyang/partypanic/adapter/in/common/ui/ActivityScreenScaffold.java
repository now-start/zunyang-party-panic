package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

public final class ActivityScreenScaffold {

    private static final float MARGIN = 48f;
    private static final float GRID_HEIGHT = 420f;
    private static final float STATUS_HEIGHT = 180f;
    private static final float GRID_GAP = 24f;
    private static final float CARD_INSET_RATIO = 0.12f;
    private static final float CARD_SIZE_RATIO = 0.76f;
    private static final float ACTOR_WIDTH_RATIO = 0.46f;
    private static final float ACTOR_HEIGHT_RATIO = 0.64f;

    public ActivityFrame frame(float worldWidth, float worldHeight, int width, int height) {
        float gridBottom = MARGIN + STATUS_HEIGHT + GRID_GAP;
        float gridWidth = worldWidth - (MARGIN * 2f);
        return new ActivityFrame(
            worldWidth,
            worldHeight,
            gridBottom,
            gridWidth,
            gridWidth / width,
            GRID_HEIGHT / height
        );
    }

    public void drawHeader(
        SpriteBatch batch,
        BitmapFont titleFont,
        BitmapFont bodyFont,
        SampleTextureLibrary textureLibrary,
        ActivityFrame frame,
        SampleTextureId backgroundTextureId,
        String title,
        String instructions,
        String goalText
    ) {
        batch.setColor(Color.WHITE);
        batch.draw(textureLibrary.region(backgroundTextureId), 0f, 0f, frame.worldWidth(), frame.worldHeight());
        titleFont.draw(batch, title, MARGIN, frame.worldHeight() - 36f);
        bodyFont.draw(batch, instructions, MARGIN, frame.worldHeight() - 76f);
        bodyFont.draw(batch, goalText, MARGIN, frame.worldHeight() - 108f);
    }

    public void drawGridPanel(
        SpriteBatch batch,
        SampleTextureLibrary textureLibrary,
        ActivityFrame frame,
        Color panelTint
    ) {
        batch.setColor(panelTint);
        batch.draw(
            textureLibrary.region(SampleTextureId.MESSAGE_PANEL),
            MARGIN,
            frame.gridBottom(),
            frame.gridWidth(),
            GRID_HEIGHT
        );
        batch.setColor(Color.WHITE);
    }

    public GridCardBounds cardBounds(ActivityFrame frame, int gridX, int gridY) {
        return new GridCardBounds(
            MARGIN + (gridX * frame.cellWidth()) + (frame.cellWidth() * CARD_INSET_RATIO),
            frame.gridBottom() + (gridY * frame.cellHeight()) + (frame.cellHeight() * CARD_INSET_RATIO),
            frame.cellWidth() * CARD_SIZE_RATIO,
            frame.cellHeight() * CARD_SIZE_RATIO
        );
    }

    public void drawActor(
        SpriteBatch batch,
        BitmapFont bodyFont,
        SampleTextureLibrary textureLibrary,
        ActivityFrame frame,
        int actorX,
        int actorY
    ) {
        TextureRegion helper = textureLibrary.region(SampleTextureId.HELPER_ACTOR);
        float actorWidth = frame.cellWidth() * ACTOR_WIDTH_RATIO;
        float actorHeight = frame.cellHeight() * ACTOR_HEIGHT_RATIO;
        float actorDrawX = MARGIN + (actorX * frame.cellWidth()) + ((frame.cellWidth() - actorWidth) / 2f);
        float actorDrawY = frame.gridBottom() + (actorY * frame.cellHeight()) + ((frame.cellHeight() - actorHeight) / 2f);
        batch.draw(helper, actorDrawX, actorDrawY, actorWidth, actorHeight);
        bodyFont.draw(batch, "조력자", actorDrawX - 2f, actorDrawY - 6f);
    }

    public void drawFacing(
        SpriteBatch batch,
        BitmapFont bodyFont,
        ActivityFrame frame,
        String facing
    ) {
        bodyFont.draw(batch, "facing: " + facing, MARGIN, frame.roomTop() + 28f);
    }

    public void drawStatusPanel(
        SpriteBatch batch,
        BitmapFont bodyFont,
        SampleTextureLibrary textureLibrary,
        ActivityFrame frame,
        String statusMessage,
        String metricLine,
        String footer
    ) {
        batch.setColor(Color.WHITE);
        batch.draw(
            textureLibrary.region(SampleTextureId.MESSAGE_PANEL),
            MARGIN,
            MARGIN,
            frame.statusWidth(),
            STATUS_HEIGHT
        );
        bodyFont.draw(batch, "status", MARGIN + 24f, MARGIN + STATUS_HEIGHT - 24f);
        bodyFont.draw(
            batch,
            statusMessage,
            MARGIN + 24f,
            MARGIN + 116f,
            frame.statusWidth() - 48f,
            Align.left,
            true
        );
        bodyFont.draw(batch, metricLine, MARGIN + 24f, MARGIN + 66f);
        bodyFont.draw(batch, footer, MARGIN + 24f, MARGIN + 34f);
    }

    public record ActivityFrame(
        float worldWidth,
        float worldHeight,
        float gridBottom,
        float gridWidth,
        float cellWidth,
        float cellHeight
    ) {

        public float roomTop() {
            return gridBottom + GRID_HEIGHT;
        }

        public float statusWidth() {
            return worldWidth - (MARGIN * 2f);
        }
    }

    public record GridCardBounds(
        float x,
        float y,
        float width,
        float height
    ) {
    }
}
