package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;
import java.util.EnumMap;
import java.util.Map;

public final class SampleFontLibrary implements Disposable {

    private final SampleAssetManifest assetManifest;
    private final Map<SampleFontId, BitmapFont> fonts = new EnumMap<>(SampleFontId.class);
    private final Map<SampleFontId, FontSource> fontSources = new EnumMap<>(SampleFontId.class);

    public SampleFontLibrary() {
        this(SampleAssetManifest.defaultManifest());
    }

    public SampleFontLibrary(SampleAssetManifest assetManifest) {
        this.assetManifest = assetManifest;
    }

    public BitmapFont font(SampleFontId id) {
        ensureLoaded(id);
        return fonts.get(id);
    }

    public String debugSource(SampleFontId id) {
        ensureLoaded(id);
        FontSource source = fontSources.get(id);
        return source.placeholder() ? "placeholder" : "asset";
    }

    private void ensureLoaded(SampleFontId id) {
        if (fonts.containsKey(id)) {
            return;
        }
        LoadedFont loadedFont = loadFont(id);
        fonts.put(id, loadedFont.font());
        fontSources.put(id, loadedFont.source());
    }

    private LoadedFont loadFont(SampleFontId id) {
        SampleFontSpec spec = SampleFontSpec.forId(id);
        String assetPath = assetManifest.fontPath(id);
        if (assetPath != null && Gdx.files != null) {
            FileHandle fileHandle = Gdx.files.internal(assetPath);
            if (fileHandle.exists()) {
                return new LoadedFont(loadAssetFont(fileHandle, spec), new FontSource(false));
            }
        }
        return new LoadedFont(createPlaceholder(spec), new FontSource(true));
    }

    private BitmapFont loadAssetFont(FileHandle fileHandle, SampleFontSpec spec) {
        String lowerPath = fileHandle.path().toLowerCase();
        if (lowerPath.endsWith(".fnt")) {
            BitmapFont bitmapFont = new BitmapFont(fileHandle);
            bitmapFont.getData().setScale(spec.fallbackScale());
            return bitmapFont;
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fileHandle);
        try {
            FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = spec.pixelSize();
            return generator.generateFont(parameter);
        } finally {
            generator.dispose();
        }
    }

    private BitmapFont createPlaceholder(SampleFontSpec spec) {
        BitmapFont bitmapFont = new BitmapFont();
        bitmapFont.getData().setScale(spec.fallbackScale());
        return bitmapFont;
    }

    @Override
    public void dispose() {
        for (BitmapFont font : fonts.values()) {
            font.dispose();
        }
        fonts.clear();
        fontSources.clear();
    }

    private record LoadedFont(BitmapFont font, FontSource source) {
    }

    private record FontSource(boolean placeholder) {
    }
}
