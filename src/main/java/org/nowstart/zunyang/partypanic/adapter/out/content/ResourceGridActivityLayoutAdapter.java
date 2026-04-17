package org.nowstart.zunyang.partypanic.adapter.out.content;

import com.badlogic.gdx.utils.Json;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.nowstart.zunyang.partypanic.application.port.out.LoadGridActivityLayoutPort;
import org.nowstart.zunyang.partypanic.domain.common.GridActivityLayout;
import org.nowstart.zunyang.partypanic.domain.common.Position;

public final class ResourceGridActivityLayoutAdapter<T extends Enum<T>>
    implements LoadGridActivityLayoutPort<T> {

    private final String resourcePath;
    private final Class<T> pointType;
    private final Json json = new Json();

    public ResourceGridActivityLayoutAdapter(String resourcePath, Class<T> pointType) {
        this.resourcePath = Objects.requireNonNull(resourcePath, "resourcePath must not be null");
        this.pointType = Objects.requireNonNull(pointType, "pointType must not be null");
    }

    @Override
    public GridActivityLayout<T> load() {
        return parse(resourcePath, readResource(resourcePath));
    }

    GridActivityLayout<T> parse(String resourcePath, String rawDocument) {
        Objects.requireNonNull(resourcePath, "resourcePath must not be null");
        Objects.requireNonNull(rawDocument, "rawDocument must not be null");

        GridActivityLayoutDocument document = json.fromJson(GridActivityLayoutDocument.class, rawDocument);
        if (document == null) {
            throw new IllegalStateException("Unable to parse layout resource: " + resourcePath);
        }
        if (document.width <= 0 || document.height <= 0) {
            throw new IllegalStateException("Layout width/height must be positive: " + resourcePath);
        }
        if (document.actorStart == null) {
            throw new IllegalStateException("Missing actorStart in " + resourcePath);
        }
        if (document.points == null || document.points.length == 0) {
            throw new IllegalStateException("Layout points must not be empty: " + resourcePath);
        }

        Map<T, Position> points = new LinkedHashMap<>();
        for (LayoutPointDocument point : document.points) {
            if (point == null || point.id == null || point.id.isBlank()) {
                throw new IllegalStateException("Layout point id must not be blank: " + resourcePath);
            }
            T pointId = parsePointId(resourcePath, point.id);
            if (points.put(pointId, new Position(point.x, point.y)) != null) {
                throw new IllegalStateException("Duplicate layout point id in " + resourcePath + ": " + pointId);
            }
        }

        if (points.size() != pointType.getEnumConstants().length) {
            throw new IllegalStateException("Layout point count mismatch in " + resourcePath);
        }

        return new GridActivityLayout<>(
            document.width,
            document.height,
            new Position(document.actorStart.x, document.actorStart.y),
            points
        );
    }

    private T parsePointId(String resourcePath, String rawId) {
        try {
            return Enum.valueOf(pointType, rawId.strip());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                "Unsupported layout point id in " + resourcePath + ": " + rawId,
                exception
            );
        }
    }

    private String readResource(String resourcePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing layout resource: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to read layout resource: " + resourcePath, exception);
        }
    }

    private static final class GridActivityLayoutDocument {
        public int width;
        public int height;
        public PositionDocument actorStart;
        public LayoutPointDocument[] points;
    }

    private static final class PositionDocument {
        public int x;
        public int y;
    }

    private static final class LayoutPointDocument {
        public String id;
        public int x;
        public int y;
    }
}
