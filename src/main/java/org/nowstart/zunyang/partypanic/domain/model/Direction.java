package org.nowstart.zunyang.partypanic.domain.model;

import lombok.Getter;

@Getter
public enum Direction {
    UP(0, -1, "상"),
    DOWN(0, 1, "하"),
    LEFT(-1, 0, "좌"),
    RIGHT(1, 0, "우");

    private final int dx;
    private final int dy;
    private final String label;

    Direction(int dx, int dy, String label) {
        this.dx = dx;
        this.dy = dy;
        this.label = label;
    }
}
