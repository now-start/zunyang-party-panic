package org.nowstart.zunyang.partypanic.domain.model;

public record Player(Position position, Direction facing) {
    public Player face(Direction direction) {
        return new Player(position, direction);
    }

    public Player moveTo(Position nextPosition) {
        return new Player(nextPosition, facing);
    }
}
