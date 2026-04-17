package org.nowstart.zunyang.partypanic.adapter.out.content;

import com.badlogic.gdx.utils.Json;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubLayoutPort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.hub.HubHotspot;
import org.nowstart.zunyang.partypanic.domain.hub.HubLayout;

public final class ResourceHubLayoutAdapter implements LoadHubLayoutPort {

    private static final String RESOURCE_PATH = "content/layouts/hub.json";

    private final Json json = new Json();

    @Override
    public HubLayout load() {
        return parse(RESOURCE_PATH, readResource(RESOURCE_PATH));
    }

    HubLayout parse(String resourcePath, String rawDocument) {
        Objects.requireNonNull(resourcePath, "resourcePath must not be null");
        Objects.requireNonNull(rawDocument, "rawDocument must not be null");

        HubLayoutDocument document = json.fromJson(HubLayoutDocument.class, rawDocument);
        if (document == null) {
            throw new IllegalStateException("Unable to parse hub layout resource: " + resourcePath);
        }
        if (document.width <= 0 || document.height <= 0) {
            throw new IllegalStateException("Hub layout width/height must be positive: " + resourcePath);
        }
        if (document.startPosition == null) {
            throw new IllegalStateException("Missing startPosition in " + resourcePath);
        }
        if (document.hotspots == null || document.hotspots.length == 0) {
            throw new IllegalStateException("Hub hotspots must not be empty: " + resourcePath);
        }

        HubLayout layout = new HubLayout(
            document.width,
            document.height,
            new Position(document.startPosition.x, document.startPosition.y),
            Arrays.stream(document.hotspots)
                .map(hotspot -> new HubHotspot(
                    parseChapterId(resourcePath, hotspot.chapterId),
                    requireText(hotspot.label, "hotspot.label", resourcePath),
                    new Position(hotspot.x, hotspot.y),
                    requireText(hotspot.interactionText, "hotspot.interactionText", resourcePath),
                    requireText(hotspot.lockedText, "hotspot.lockedText", resourcePath)
                ))
                .toList()
        );

        if (!layout.isInside(layout.startPosition())) {
            throw new IllegalStateException("Hub startPosition must be inside layout: " + resourcePath);
        }
        return layout;
    }

    private String readResource(String resourcePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing hub layout resource: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to read hub layout resource: " + resourcePath, exception);
        }
    }

    private ChapterId parseChapterId(String resourcePath, String rawChapterId) {
        String chapterIdText = requireText(rawChapterId, "hotspot.chapterId", resourcePath);
        try {
            return ChapterId.valueOf(chapterIdText);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                "Unsupported hotspot.chapterId in " + resourcePath + ": " + chapterIdText,
                exception
            );
        }
    }

    private String requireText(String value, String fieldName, String resourcePath) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing or blank " + fieldName + " in " + resourcePath);
        }
        return value.strip();
    }

    private static final class HubLayoutDocument {
        public int width;
        public int height;
        public PositionDocument startPosition;
        public HubHotspotDocument[] hotspots;
    }

    private static final class PositionDocument {
        public int x;
        public int y;
    }

    private static final class HubHotspotDocument {
        public String chapterId;
        public String label;
        public int x;
        public int y;
        public String interactionText;
        public String lockedText;
    }
}
