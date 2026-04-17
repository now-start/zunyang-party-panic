package org.nowstart.zunyang.partypanic.adapter.in.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    protected final boolean showsOperationalUi() {
        return navigator.showsOperationalUi();
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
