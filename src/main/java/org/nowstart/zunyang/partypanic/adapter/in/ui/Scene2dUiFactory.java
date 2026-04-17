package org.nowstart.zunyang.partypanic.adapter.in.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public final class Scene2dUiFactory {
    private final Texture pixelTexture;
    private final BitmapFont bodyFont;
    private final BitmapFont titleFont;

    public Scene2dUiFactory(Texture pixelTexture, BitmapFont bodyFont, BitmapFont titleFont) {
        this.pixelTexture = pixelTexture;
        this.bodyFont = bodyFont;
        this.titleFont = titleFont;
    }

    public PanelTable panel(Color backgroundColor, Color borderColor) {
        return new PanelTable(pixelTexture, backgroundColor, borderColor);
    }

    public Label bodyLabel(String text, float scale, Color color) {
        return label(text, bodyFont, scale, color);
    }

    public Label titleLabel(String text, float scale, Color color) {
        return label(text, titleFont, scale, color);
    }

    public ProgressBar progressBar(Color backgroundColor, Color fillColor) {
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = tintedDrawable(backgroundColor);
        style.knobBefore = tintedDrawable(fillColor);
        style.knob = tintedDrawable(fillColor);

        ProgressBar progressBar = new ProgressBar(0f, 1f, 0.01f, false, style);
        progressBar.setAnimateDuration(0f);
        return progressBar;
    }

    private Label label(String text, BitmapFont font, float scale, Color color) {
        Label label = new Label(text, new Label.LabelStyle(font, color));
        label.setFontScale(scale);
        label.setWrap(true);
        return label;
    }

    private Drawable tintedDrawable(Color color) {
        return new TextureRegionDrawable(new TextureRegion(pixelTexture)).tint(color);
    }
}
