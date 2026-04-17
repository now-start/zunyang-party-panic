package org.nowstart.zunyang.partypanic.domain.common;

public record Position(
    int x,
    int y
) {

    public Position translate(Direction direction) {
        return new Position(x + direction.dx(), y + direction.dy());
    }
}
