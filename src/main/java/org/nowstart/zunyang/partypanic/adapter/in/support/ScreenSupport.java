package org.nowstart.zunyang.partypanic.adapter.in.support;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public final class ScreenSupport {
    private static final int DEFAULT_FONT_SIZE = 24;
    private static final String BODY_FONT_PATH = "assets/fonts/NanumSquareRoundR.ttf";
    private static final String TITLE_FONT_PATH = "assets/fonts/GowunDodum-Regular.ttf";

    private ScreenSupport() {
    }

    public static BitmapFont createFont(String characters) {
        return createBodyFont(characters);
    }

    public static BitmapFont createBodyFont(String characters) {
        FileHandle fontHandle = resolveBodyFontHandle();
        return createFont(fontHandle, characters);
    }

    public static BitmapFont createTitleFont(String characters) {
        FileHandle fontHandle = resolveTitleFontHandle();
        return createFont(fontHandle, characters);
    }

    private static BitmapFont createFont(FileHandle fontHandle, String characters) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontHandle);
        try {
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = DEFAULT_FONT_SIZE;
            parameter.incremental = false;
            parameter.minFilter = Texture.TextureFilter.Linear;
            parameter.magFilter = Texture.TextureFilter.Linear;
            parameter.characters = characters;
            return generator.generateFont(parameter);
        } finally {
            generator.dispose();
        }
    }

    public static Texture createPixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public static Texture loadTexture(String... candidates) {
        for (String candidate : candidates) {
            FileHandle handle = Gdx.files.internal(candidate);
            if (handle.exists()) {
                return new Texture(handle);
            }
        }

        throw new IllegalStateException("Missing texture. Tried: " + String.join(", ", candidates));
    }

    private static FileHandle resolveBodyFontHandle() {
        return requireInternalFile(BODY_FONT_PATH);
    }

    private static FileHandle resolveTitleFontHandle() {
        FileHandle titleHandle = Gdx.files.internal(TITLE_FONT_PATH);
        return titleHandle.exists() ? titleHandle : resolveBodyFontHandle();
    }

    private static FileHandle requireInternalFile(String path) {
        FileHandle handle = Gdx.files.internal(path);
        if (!handle.exists()) {
            throw new IllegalStateException("Required font missing: " + path);
        }
        return handle;
    }
}
