package org.nowstart.zunyang.partypanic.adapter.in.runtime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.utils.Disposable;

public final class GameAssets implements Disposable {
    private static final int UI_FONT_SIZE = 24;

    private final AssetManager assetManager;
    private final SpriteBatch batch;
    private final Texture pixelTexture;
    private final String photoCardTexturePath;

    private GameAssets(AssetManager assetManager, SpriteBatch batch, Texture pixelTexture, String photoCardTexturePath) {
        this.assetManager = assetManager;
        this.batch = batch;
        this.pixelTexture = pixelTexture;
        this.photoCardTexturePath = photoCardTexturePath;
    }

    public static GameAssets load() {
        AssetManager assetManager = new AssetManager();
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        String photoCardTexturePath = GameAssetCatalog.resolvePhotoCardTexturePath(path -> Gdx.files.internal(path).exists());
        for (String texturePath : GameAssetCatalog.requiredTexturePaths(photoCardTexturePath)) {
            assetManager.load(texturePath, Texture.class);
        }

        loadFont(assetManager, GameAssetCatalog.BODY_FONT_ASSET, GameAssetCatalog.BODY_FONT_FILE);
        loadFont(assetManager, GameAssetCatalog.TITLE_FONT_ASSET, GameAssetCatalog.TITLE_FONT_FILE);
        assetManager.finishLoading();

        return new GameAssets(assetManager, new SpriteBatch(), createPixelTexture(), photoCardTexturePath);
    }

    public SpriteBatch batch() {
        return batch;
    }

    public BitmapFont bodyFont() {
        return assetManager.get(GameAssetCatalog.BODY_FONT_ASSET, BitmapFont.class);
    }

    public BitmapFont titleFont() {
        return assetManager.get(GameAssetCatalog.TITLE_FONT_ASSET, BitmapFont.class);
    }

    public Texture pixelTexture() {
        return pixelTexture;
    }

    public Texture texture(String assetPath) {
        return assetManager.get(assetPath, Texture.class);
    }

    public Texture hostTexture() {
        return texture(GameAssetCatalog.HOST_TEXTURE);
    }

    public Texture titleBackgroundTexture() {
        return texture(GameAssetCatalog.TITLE_BACKGROUND_TEXTURE);
    }

    public Texture cakeCardTexture() {
        return texture(GameAssetCatalog.CAKE_CARD_TEXTURE);
    }

    public Texture photoCardTexture() {
        return texture(photoCardTexturePath);
    }

    @Override
    public void dispose() {
        batch.dispose();
        pixelTexture.dispose();
        assetManager.dispose();
    }

    private static void loadFont(AssetManager assetManager, String assetName, String fontFileName) {
        FreeTypeFontLoaderParameter parameter = new FreeTypeFontLoaderParameter();
        parameter.fontFileName = fontFileName;
        parameter.fontParameters.size = UI_FONT_SIZE;
        parameter.fontParameters.incremental = true;
        parameter.fontParameters.minFilter = Texture.TextureFilter.Linear;
        parameter.fontParameters.magFilter = Texture.TextureFilter.Linear;
        assetManager.load(assetName, BitmapFont.class, parameter);
    }

    private static Texture createPixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
