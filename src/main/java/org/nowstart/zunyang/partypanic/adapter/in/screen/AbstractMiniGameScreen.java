package org.nowstart.zunyang.partypanic.adapter.in.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.MiniGameChrome;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.PixelUiRenderer;
import org.nowstart.zunyang.partypanic.adapter.in.support.ScreenSupport;
import org.nowstart.zunyang.partypanic.application.port.out.GameNavigator;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractMiniGameScreen extends ScreenAdapter {
    protected final GameNavigator navigator;
    protected final GameProgress progress;
    protected final SpriteBatch batch = new SpriteBatch();

    protected PixelUiRenderer ui;

    private BitmapFont bodyFont;
    private BitmapFont titleFont;
    private Texture pixelTexture;
    private Texture backgroundTexture;

    protected final void initializeUi(String backgroundPath, String fontCharacters) {
        this.bodyFont = ScreenSupport.createBodyFont(fontCharacters);
        this.titleFont = ScreenSupport.createTitleFont(fontCharacters);
        this.pixelTexture = ScreenSupport.createPixelTexture();
        this.backgroundTexture = ScreenSupport.loadTexture(backgroundPath);
        this.ui = new PixelUiRenderer(batch, bodyFont, titleFont, pixelTexture);
    }

    @Override
    public final void render(float delta) {
        if (!handleInput()) {
            return;
        }

        updateState(delta);
        ScreenUtils.clear(0.06f, 0.04f, 0.05f, 1f);

        batch.begin();
        MiniGameChrome.drawBackdrop(ui, backgroundTexture);
        MiniGameChrome.drawFrames(ui, backgroundTexture, showsOperationalUi());
        drawMiniGameStage();
        if (showsOperationalUi()) {
            drawOperationalUi();
            MiniGameChrome.drawCommandBar(ui, commandHint());
        } else {
            drawLiveHud();
        }
        batch.end();
    }

    protected abstract boolean handleInput();

    protected abstract void updateState(float delta);

    protected abstract void drawMiniGameStage();

    protected abstract void drawOperationalUi();

    protected abstract void drawLiveHud();

    protected abstract String commandHint();

    protected boolean showsOperationalUi() {
        return navigator.showsOperationalUi();
    }

    protected final void drawPanel(float x, float y, float width, float height, Color color) {
        ui.panel(x, y, width, height, color);
    }

    protected final void drawPanelOutline(float x, float y, float width, float height, Color color) {
        ui.panelOutline(x, y, width, height, color);
    }

    protected final void drawTextureCover(Texture texture, float x, float y, float width, float height) {
        ui.textureCover(texture, x, y, width, height);
    }

    protected final void drawTextureFit(Texture texture, float x, float y, float width, float height) {
        ui.textureFit(texture, x, y, width, height);
    }

    protected final void drawLine(String text, float x, float y, float scale, Color color) {
        ui.line(text, x, y, scale, color);
    }

    protected final void drawTitleLine(String text, float x, float y, float scale, Color color) {
        ui.titleLine(text, x, y, scale, color);
    }

    protected final void drawParagraph(String text, float x, float y, float width, float scale, Color color) {
        ui.paragraph(text, x, y, width, scale, color);
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (bodyFont != null) {
            bodyFont.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (pixelTexture != null) {
            pixelTexture.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        disposeResources();
    }

    protected void disposeResources() {
    }
}
