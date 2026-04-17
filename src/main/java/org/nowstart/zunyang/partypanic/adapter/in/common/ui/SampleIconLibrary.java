package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import java.util.EnumMap;
import java.util.Map;

public final class SampleIconLibrary implements Disposable {

    private final SampleAssetManifest assetManifest;
    private final Map<SampleIconId, Texture> textures = new EnumMap<>(SampleIconId.class);
    private final Map<SampleIconId, IconSource> iconSources = new EnumMap<>(SampleIconId.class);

    public SampleIconLibrary() {
        this(SampleAssetManifest.defaultManifest());
    }

    public SampleIconLibrary(SampleAssetManifest assetManifest) {
        this.assetManifest = assetManifest;
    }

    public TextureRegion region(SampleIconId id) {
        ensureLoaded(id);
        return new TextureRegion(textures.get(id));
    }

    public String debugSource(SampleIconId id) {
        ensureLoaded(id);
        IconSource source = iconSources.get(id);
        return source.placeholder() ? "placeholder" : "asset";
    }

    private void ensureLoaded(SampleIconId id) {
        if (textures.containsKey(id)) {
            return;
        }
        LoadedIcon loadedIcon = loadIcon(id);
        textures.put(id, loadedIcon.texture());
        iconSources.put(id, loadedIcon.source());
    }

    private LoadedIcon loadIcon(SampleIconId id) {
        SampleIconSpec spec = SampleIconSpec.forId(id);
        String assetPath = assetManifest.iconPath(id);
        if (assetPath != null && Gdx.files != null) {
            FileHandle fileHandle = Gdx.files.internal(assetPath);
            if (fileHandle.exists()) {
                return new LoadedIcon(new Texture(fileHandle), new IconSource(false));
            }
        }
        return new LoadedIcon(createPlaceholder(id, spec), new IconSource(true));
    }

    private Texture createPlaceholder(SampleIconId id, SampleIconSpec spec) {
        Pixmap pixmap = new Pixmap(spec.size(), spec.size(), Pixmap.Format.RGBA8888);
        try {
            pixmap.setColor(spec.fill());
            pixmap.fill();

            pixmap.setColor(spec.panel());
            pixmap.fillRectangle(8, 8, spec.size() - 16, spec.size() - 16);

            pixmap.setColor(spec.border());
            pixmap.drawRectangle(4, 4, spec.size() - 8, spec.size() - 8);
            pixmap.drawRectangle(8, 8, spec.size() - 16, spec.size() - 16);

            pixmap.setColor(spec.accent());
            drawGlyph(pixmap, id, spec.size());

            return new Texture(pixmap);
        } finally {
            pixmap.dispose();
        }
    }

    private void drawGlyph(Pixmap pixmap, SampleIconId id, int size) {
        switch (id) {
            case MOVE -> drawMoveGlyph(pixmap, size);
            case INTERACT -> drawInteractGlyph(pixmap, size);
            case OPEN_CHAPTER -> drawOpenGlyph(pixmap, size);
            case NEXT -> drawNextGlyph(pixmap, size);
            case SKIP -> drawSkipGlyph(pixmap, size);
            case DEBUG -> drawDebugGlyph(pixmap, size);
            case RESTART -> drawRestartGlyph(pixmap, size);
            case HUB -> drawHubGlyph(pixmap, size);
        }
    }

    private void drawMoveGlyph(Pixmap pixmap, int size) {
        int center = size / 2;
        pixmap.drawLine(center, 16, center, size - 16);
        pixmap.drawLine(16, center, size - 16, center);
        pixmap.fillTriangle(center, size - 14, center - 6, size - 24, center + 6, size - 24);
        pixmap.fillTriangle(center, 14, center - 6, 24, center + 6, 24);
        pixmap.fillTriangle(size - 14, center, size - 24, center - 6, size - 24, center + 6);
        pixmap.fillTriangle(14, center, 24, center - 6, 24, center + 6);
    }

    private void drawInteractGlyph(Pixmap pixmap, int size) {
        pixmap.drawCircle(size / 2, size / 2, 14);
        pixmap.fillCircle(size / 2, size / 2, 4);
        pixmap.drawLine(size / 2, size / 2 + 14, size / 2, size - 18);
    }

    private void drawOpenGlyph(Pixmap pixmap, int size) {
        pixmap.drawRectangle(16, 18, 18, 28);
        pixmap.drawLine(28, size / 2, size - 18, size / 2);
        pixmap.fillTriangle(size - 14, size / 2, size - 24, size / 2 + 7, size - 24, size / 2 - 7);
    }

    private void drawNextGlyph(Pixmap pixmap, int size) {
        drawChevronRight(pixmap, 20, 18, 12, 14);
        drawChevronRight(pixmap, 34, 18, 12, 14);
    }

    private void drawSkipGlyph(Pixmap pixmap, int size) {
        drawChevronRight(pixmap, 18, 18, 12, 14);
        drawChevronRight(pixmap, 32, 18, 12, 14);
        pixmap.fillRectangle(size - 18, 18, 4, 28);
    }

    private void drawDebugGlyph(Pixmap pixmap, int size) {
        int center = size / 2;
        pixmap.drawCircle(center, center, 14);
        pixmap.drawLine(center, 14, center, size - 14);
        pixmap.drawLine(14, center, size - 14, center);
        pixmap.fillCircle(center, center, 3);
    }

    private void drawRestartGlyph(Pixmap pixmap, int size) {
        pixmap.drawCircle(size / 2, size / 2, 14);
        pixmap.fillTriangle(18, size / 2 + 6, 26, size / 2 + 12, 28, size / 2);
        pixmap.fillTriangle(size / 2 + 8, 18, size / 2 + 16, 14, size / 2 + 16, 24);
        pixmap.setColor(Color.CLEAR);
        pixmap.fillCircle(size / 2, size / 2, 8);
    }

    private void drawHubGlyph(Pixmap pixmap, int size) {
        pixmap.drawLine(18, 28, size / 2, 16);
        pixmap.drawLine(size / 2, 16, size - 18, 28);
        pixmap.drawRectangle(20, 28, size - 40, 18);
        pixmap.drawRectangle(size / 2 - 5, 28, 10, 18);
    }

    private void drawChevronRight(Pixmap pixmap, int startX, int startY, int width, int height) {
        pixmap.drawLine(startX, startY, startX + width, startY + (height / 2));
        pixmap.drawLine(startX + width, startY + (height / 2), startX, startY + height);
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();
        iconSources.clear();
    }

    private record LoadedIcon(Texture texture, IconSource source) {
    }

    private record IconSource(boolean placeholder) {
    }
}
