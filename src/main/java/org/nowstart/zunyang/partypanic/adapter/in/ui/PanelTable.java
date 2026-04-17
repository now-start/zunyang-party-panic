package org.nowstart.zunyang.partypanic.adapter.in.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public final class PanelTable extends Table {
    private final Texture pixelTexture;
    private final Color backgroundColor;
    private final Color borderColor;
    private final Color backgroundScratch = new Color();
    private final Color borderScratch = new Color();

    public PanelTable(Texture pixelTexture, Color backgroundColor, Color borderColor) {
        this.pixelTexture = pixelTexture;
        this.backgroundColor = new Color(backgroundColor);
        this.borderColor = new Color(borderColor);
        pad(18f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color previous = batch.getColor();
        float alpha = parentAlpha * getColor().a;

        drawRect(batch, getX(), getY(), getWidth(), getHeight(), withAlpha(backgroundScratch, backgroundColor, alpha));
        drawRect(batch, getX(), getY(), getWidth(), 2f, withAlpha(borderScratch, borderColor, alpha));
        drawRect(batch, getX(), getY() + getHeight() - 2f, getWidth(), 2f, withAlpha(borderScratch, borderColor, alpha));
        drawRect(batch, getX(), getY(), 2f, getHeight(), withAlpha(borderScratch, borderColor, alpha));
        drawRect(batch, getX() + getWidth() - 2f, getY(), 2f, getHeight(), withAlpha(borderScratch, borderColor, alpha));

        batch.setColor(previous);
        super.draw(batch, parentAlpha);
    }

    private void drawRect(Batch batch, float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(pixelTexture, x, y, width, height);
    }

    private Color withAlpha(Color target, Color source, float alpha) {
        return target.set(source.r, source.g, source.b, source.a * alpha);
    }
}
