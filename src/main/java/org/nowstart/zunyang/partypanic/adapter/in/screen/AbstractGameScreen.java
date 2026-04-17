package org.nowstart.zunyang.partypanic.adapter.in.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameAssets;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameViewportConfig;
import org.nowstart.zunyang.partypanic.application.port.out.GameNavigator;

public abstract class AbstractGameScreen extends ScreenAdapter {
    protected final GameNavigator navigator;
    protected final GameAssets assets;
    protected final SpriteBatch batch;
    protected final OrthographicCamera camera;
    protected final Viewport viewport;
    private InputMultiplexer inputMultiplexer;
    private Stage uiStage;

    protected AbstractGameScreen(GameNavigator navigator, GameAssets assets) {
        this.navigator = navigator;
        this.assets = assets;
        this.batch = assets.batch();
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameViewportConfig.WORLD_WIDTH, GameViewportConfig.WORLD_HEIGHT, camera);
        this.viewport.apply(true);
    }

    @Override
    public void show() {
        viewport.apply(true);
        if (uiStage == null) {
            uiStage = buildUiStage();
        }

        inputMultiplexer = new InputMultiplexer();
        if (uiStage != null) {
            inputMultiplexer.addProcessor(uiStage);
        }

        InputProcessor inputProcessor = createInputProcessor();
        if (inputProcessor != null) {
            inputMultiplexer.addProcessor(inputProcessor);
        }

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (uiStage != null) {
            uiStage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == inputMultiplexer) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        if (uiStage != null) {
            uiStage.dispose();
            uiStage = null;
        }
    }

    protected final boolean showsOperationalUi() {
        return navigator.showsOperationalUi();
    }

    protected InputProcessor createInputProcessor() {
        return null;
    }

    protected Stage buildUiStage() {
        return null;
    }

    protected final Stage uiStage() {
        return uiStage;
    }

    protected final void drawUiStage(float delta) {
        if (uiStage != null) {
            uiStage.act(delta);
            uiStage.draw();
        }
    }

    protected final void beginFrame() {
        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    protected final void endFrame() {
        batch.end();
    }
}
