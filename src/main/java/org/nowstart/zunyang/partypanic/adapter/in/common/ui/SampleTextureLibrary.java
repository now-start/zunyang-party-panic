package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import java.util.EnumMap;
import java.util.Map;

public final class SampleTextureLibrary implements Disposable {

    private final Map<SampleTextureId, Texture> textures = new EnumMap<>(SampleTextureId.class);
    private final Map<SampleTextureId, TextureSource> textureSources = new EnumMap<>(SampleTextureId.class);

    public TextureRegion region(SampleTextureId id) {
        ensureLoaded(id);
        return new TextureRegion(textures.get(id));
    }

    public String debugSource(SampleTextureId id) {
        ensureLoaded(id);
        TextureSource source = textureSources.get(id);
        return source.placeholder() ? "placeholder" : "asset";
    }

    private void ensureLoaded(SampleTextureId id) {
        if (textures.containsKey(id)) {
            return;
        }
        LoadedTexture loadedTexture = loadTexture(id);
        textures.put(id, loadedTexture.texture());
        textureSources.put(id, loadedTexture.source());
    }

    private LoadedTexture loadTexture(SampleTextureId id) {
        SampleTextureSpec spec = SampleTextureSpec.forId(id);
        if (spec.assetPath() != null) {
            FileHandle fileHandle = Gdx.files.internal(spec.assetPath());
            if (fileHandle.exists()) {
                return new LoadedTexture(
                    new Texture(fileHandle),
                    new TextureSource(false)
                );
            }
        }
        return new LoadedTexture(createPlaceholder(spec), new TextureSource(true));
    }

    private Texture createPlaceholder(SampleTextureSpec spec) {
        Pixmap pixmap = new Pixmap(spec.width(), spec.height(), Pixmap.Format.RGBA8888);
        try {
            pixmap.setColor(spec.fill());
            pixmap.fill();

            pixmap.setColor(spec.panel());
            pixmap.fillRectangle(16, 16, spec.width() - 32, spec.height() - 32);

            pixmap.setColor(spec.border());
            pixmap.drawRectangle(8, 8, spec.width() - 16, spec.height() - 16);
            pixmap.drawRectangle(16, 16, spec.width() - 32, spec.height() - 32);

            pixmap.fillRectangle(24, 24, spec.width() - 48, 18);
            pixmap.fillRectangle(24, spec.height() - 42, spec.width() - 48, 10);

            drawCornerAccent(pixmap, spec.width() - 72, 24);
            drawCornerAccent(pixmap, 24, spec.height() - 72);

            return new Texture(pixmap);
        } finally {
            pixmap.dispose();
        }
    }

    private void drawCornerAccent(Pixmap pixmap, int startX, int startY) {
        for (int offset = 0; offset < 28; offset++) {
            pixmap.drawLine(startX + offset, startY, startX, startY + offset);
        }
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();
        textureSources.clear();
    }

    private record LoadedTexture(Texture texture, TextureSource source) {
    }

    private record TextureSource(boolean placeholder) {
    }
}
