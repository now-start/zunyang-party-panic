package org.nowstart.zunyang.partypanic.adapter.in.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public final class PixelUiRenderer {
    private final SpriteBatch batch;
    private final BitmapFont bodyFont;
    private final BitmapFont titleFont;
    private final Texture pixelTexture;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    public PixelUiRenderer(SpriteBatch batch, BitmapFont bodyFont, Texture pixelTexture) {
        this(batch, bodyFont, bodyFont, pixelTexture);
    }

    public PixelUiRenderer(SpriteBatch batch, BitmapFont bodyFont, BitmapFont titleFont, Texture pixelTexture) {
        this.batch = batch;
        this.bodyFont = bodyFont;
        this.titleFont = titleFont;
        this.pixelTexture = pixelTexture;
    }

    public void panel(float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(pixelTexture, x, y, width, height);
        batch.setColor(Color.WHITE);
    }

    public void panelOutline(float x, float y, float width, float height, Color color) {
        panel(x, y, width, 2f, color);
        panel(x, y + height - 2f, width, 2f, color);
        panel(x, y, 2f, height, color);
        panel(x + width - 2f, y, 2f, height, color);
    }

    public void textureCover(Texture texture, float x, float y, float width, float height) {
        float targetAspect = width / height;
        float textureAspect = texture.getWidth() / (float) texture.getHeight();
        int srcX = 0;
        int srcY = 0;
        int srcWidth = texture.getWidth();
        int srcHeight = texture.getHeight();

        if (textureAspect > targetAspect) {
            srcWidth = Math.round(texture.getHeight() * targetAspect);
            srcX = (texture.getWidth() - srcWidth) / 2;
        } else if (textureAspect < targetAspect) {
            srcHeight = Math.round(texture.getWidth() / targetAspect);
            srcY = (texture.getHeight() - srcHeight) / 2;
        }

        batch.draw(texture, x, y, width, height, srcX, srcY, srcWidth, srcHeight, false, false);
    }

    public void textureFit(Texture texture, float x, float y, float width, float height) {
        float scale = Math.min(width / texture.getWidth(), height / texture.getHeight());
        float drawWidth = texture.getWidth() * scale;
        float drawHeight = texture.getHeight() * scale;
        float drawX = x + ((width - drawWidth) * 0.5f);
        float drawY = y + ((height - drawHeight) * 0.5f);
        batch.draw(texture, drawX, drawY, drawWidth, drawHeight);
    }

    public void line(String text, float x, float y, float scale, Color color) {
        drawText(bodyFont, text, x, y, scale, color);
    }

    public void titleLine(String text, float x, float y, float scale, Color color) {
        drawText(titleFont, text, x, y, scale, color);
    }

    public void paragraph(String text, float x, float y, float width, float scale, Color color) {
        drawWrappedText(bodyFont, text, x, y, width, scale, color);
    }

    public Color withAlpha(Color color, float alpha) {
        return new Color(color.r, color.g, color.b, alpha);
    }

    private void drawText(BitmapFont font, String text, float x, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        glyphLayout.setText(font, text, color, 0f, Align.left, false);
        font.draw(batch, glyphLayout, x, y);
        font.setColor(Color.WHITE);
        font.getData().setScale(1f);
    }

    private void drawWrappedText(BitmapFont font, String text, float x, float y, float width, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        glyphLayout.setText(font, text, color, width, Align.left, true);
        font.draw(batch, glyphLayout, x, y);
        font.setColor(Color.WHITE);
        font.getData().setScale(1f);
    }
}
