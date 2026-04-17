package org.nowstart.zunyang.partypanic.domain.common;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record GridActivityLayout<T extends Enum<T>>(
    int width,
    int height,
    Position actorStart,
    Map<T, Position> points
) {

    public GridActivityLayout {
        Objects.requireNonNull(actorStart, "actorStart must not be null");
        Objects.requireNonNull(points, "points must not be null");

        if (width <= 0) {
            throw new IllegalArgumentException("width must be positive");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be positive");
        }
        if (!isInside(actorStart, width, height)) {
            throw new IllegalArgumentException("actorStart must be inside layout");
        }

        LinkedHashMap<T, Position> normalized = new LinkedHashMap<>();
        for (Map.Entry<T, Position> entry : points.entrySet()) {
            T id = Objects.requireNonNull(entry.getKey(), "point id must not be null");
            Position position = Objects.requireNonNull(entry.getValue(), "point position must not be null");
            if (!isInside(position, width, height)) {
                throw new IllegalArgumentException("point must be inside layout: " + id);
            }
            normalized.put(id, position);
        }
        points = Collections.unmodifiableMap(normalized);
    }

    public boolean isInside(Position position) {
        return isInside(position, width, height);
    }

    public boolean isWalkable(Position position) {
        return isInside(position) && pointAt(position) == null;
    }

    public Position positionOf(T id) {
        Position position = points.get(id);
        if (position == null) {
            throw new IllegalArgumentException("Missing point definition for id: " + id);
        }
        return position;
    }

    public T pointAt(Position position) {
        return points.entrySet().stream()
            .filter(entry -> entry.getValue().equals(position))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }

    private static boolean isInside(Position position, int width, int height) {
        return position.x() >= 0
            && position.y() >= 0
            && position.x() < width
            && position.y() < height;
    }
}
