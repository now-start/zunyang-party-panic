package org.nowstart.zunyang.partypanic.domain.model;

public record Position(int x, int y) {
    public Position translate(Direction direction) {
        return new Position(x + direction.getDx(), y + direction.getDy());
    }
}
