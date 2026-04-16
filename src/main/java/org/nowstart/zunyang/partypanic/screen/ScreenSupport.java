package org.nowstart.zunyang.partypanic.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class ScreenSupport {
    private ScreenSupport() {
    }

    static BitmapFont createFont(String characters) {
        Path fontPath = resolveFontPath();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.absolute(fontPath.toString()));
        try {
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 24;
            parameter.incremental = false;
            parameter.minFilter = Texture.TextureFilter.Linear;
            parameter.magFilter = Texture.TextureFilter.Linear;
            parameter.characters = characters;
            return generator.generateFont(parameter);
        } finally {
            generator.dispose();
        }
    }

    static Texture createPixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    static Texture loadTexture(String... candidates) {
        for (String candidate : candidates) {
            FileHandle handle = Gdx.files.internal(candidate);
            if (handle.exists()) {
                return new Texture(handle);
            }
        }

        throw new IllegalStateException("Missing texture. Tried: " + String.join(", ", candidates));
    }

    private static Path resolveFontPath() {
        List<Path> candidates = new ArrayList<>();
        candidates.add(Path.of("asset", "fonts", "NotoSansKR-Regular.ttf").toAbsolutePath());
        candidates.add(Path.of("assets", "fonts", "NotoSansKR-Regular.ttf").toAbsolutePath());
        candidates.add(Path.of("asset", "fonts", "malgun.ttf").toAbsolutePath());
        candidates.add(Path.of("assets", "fonts", "malgun.ttf").toAbsolutePath());
        candidates.add(Path.of("/mnt/c/Windows/Fonts/malgun.ttf"));
        candidates.add(Path.of("/mnt/c/Windows/Fonts/malgunbd.ttf"));
        candidates.add(Path.of("/mnt/c/Windows/Fonts/malgunsl.ttf"));

        Path windowsMalgunFont = resolveWindowsFont("malgun.ttf");
        if (windowsMalgunFont != null) {
            candidates.add(windowsMalgunFont);
        }

        Path windowsMalgunBoldFont = resolveWindowsFont("malgunbd.ttf");
        if (windowsMalgunBoldFont != null) {
            candidates.add(windowsMalgunBoldFont);
        }

        Path windowsMalgunLightFont = resolveWindowsFont("malgunsl.ttf");
        if (windowsMalgunLightFont != null) {
            candidates.add(windowsMalgunLightFont);
        }

        candidates.add(Path.of("asset", "fonts", "NotoSansKR-VF.ttf").toAbsolutePath());
        candidates.add(Path.of("assets", "fonts", "NotoSansKR-VF.ttf").toAbsolutePath());

        Path windowsNotoFont = resolveWindowsFont("NotoSansKR-VF.ttf");
        if (windowsNotoFont != null) {
            candidates.add(windowsNotoFont);
        }

        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate) && !isUnsupportedVariableFont(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "Korean font not found. Add a TTF under asset/fonts or install NotoSansKR/Malgun Gothic."
        );
    }

    private static Path resolveWindowsFont(String fileName) {
        String windowsDirectory = System.getenv("WINDIR");
        if (windowsDirectory == null || windowsDirectory.isBlank()) {
            return null;
        }
        return Path.of(windowsDirectory, "Fonts", fileName);
    }

    private static boolean isUnsupportedVariableFont(Path candidate) {
        String fileName = candidate.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.contains("-vf.");
    }
}
