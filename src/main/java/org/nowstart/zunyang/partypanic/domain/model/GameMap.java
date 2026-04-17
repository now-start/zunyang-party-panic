package org.nowstart.zunyang.partypanic.domain.model;

import org.nowstart.zunyang.partypanic.domain.event.GameEvent;

import java.util.List;

public final class GameMap {
    private final List<String> layout;
    private final List<GameEvent> events;
    private final Position startingPosition;

    public GameMap(List<String> layout, List<GameEvent> events, Position startingPosition) {
        if (layout == null || layout.isEmpty()) {
            throw new IllegalArgumentException("Game map layout must not be empty.");
        }
        this.layout = List.copyOf(layout);
        this.events = List.copyOf(events);
        this.startingPosition = startingPosition;
    }

    public List<GameEvent> events() {
        return events;
    }

    public Position startingPosition() {
        return startingPosition;
    }

    public int rowCount() {
        return layout.size();
    }

    public int columnCount() {
        return layout.get(0).length();
    }

    public char tileAt(int row, int column) {
        return layout.get(row).charAt(column);
    }

    public char tileAt(Position position) {
        return tileAt(position.y(), position.x());
    }

    public boolean isInside(Position position) {
        return position.x() >= 0
                && position.x() < columnCount()
                && position.y() >= 0
                && position.y() < rowCount();
    }

    public boolean isWalkable(Position position) {
        return isInside(position)
                && tileAt(position) != '#'
                && eventAt(position) == null;
    }

    public GameEvent eventAt(Position position) {
        for (GameEvent event : events) {
            if (event.position().equals(position)) {
                return event;
            }
        }
        return null;
    }
}
