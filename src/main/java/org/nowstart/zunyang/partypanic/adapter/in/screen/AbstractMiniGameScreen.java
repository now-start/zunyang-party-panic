package org.nowstart.zunyang.partypanic.adapter.in.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.MiniGameChrome;
import org.nowstart.zunyang.partypanic.adapter.in.renderer.PixelUiRenderer;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameAssets;
import org.nowstart.zunyang.partypanic.application.port.out.GameNavigator;
import org.nowstart.zunyang.partypanic.domain.progress.GameProgress;

public abstract class AbstractMiniGameScreen extends AbstractGameScreen {
    protected final GameProgress progress;

    protected PixelUiRenderer ui;

    private Texture backgroundTexture;

    protected AbstractMiniGameScreen(GameNavigator navigator, GameProgress progress, GameAssets assets) {
        super(navigator, assets);
        this.progress = progress;
    }

    protected final void initializeUi(String backgroundPath) {
        this.backgroundTexture = assets.texture(backgroundPath);
        this.ui = new PixelUiRenderer(batch, assets.bodyFont(), assets.titleFont(), assets.pixelTexture());
    }

    @Override
    public final void render(float delta) {
        if (!handleInput()) {
            return;
        }

        updateState(delta);
        ScreenUtils.clear(0.06f, 0.04f, 0.05f, 1f);

        beginFrame();
        MiniGameChrome.drawBackdrop(ui, backgroundTexture);
        MiniGameChrome.drawFrames(ui, backgroundTexture, showsOperationalUi());
        drawMiniGameStage();
        if (showsOperationalUi()) {
            drawOperationalUi();
            MiniGameChrome.drawCommandBar(ui, commandHint());
        } else {
            drawLiveHud();
        }
        endFrame();
    }

    protected abstract boolean handleInput();

    protected abstract void updateState(float delta);

    protected abstract void drawMiniGameStage();

    protected abstract void drawOperationalUi();

    protected abstract void drawLiveHud();

    protected abstract String commandHint();

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
}
