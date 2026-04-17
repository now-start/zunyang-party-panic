package org.nowstart.zunyang.partypanic.domain.centerpiece;

import org.nowstart.zunyang.partypanic.domain.common.Position;

public enum CenterpiecePlacementId {
    TOPPER_SLOT("토퍼 위치", new Position(3, 4), true),
    CANDLE_ARC("촛불 아치", new Position(1, 2), true),
    RIBBON_LINE("리본 선", new Position(5, 2), true),
    CLOTH_FOLD("테이블 천", new Position(3, 0), false),
    SIDE_DECOR("주변 장식", new Position(5, 4), false);

    private final String label;
    private final Position position;
    private final boolean required;

    CenterpiecePlacementId(String label, Position position, boolean required) {
        this.label = label;
        this.position = position;
        this.required = required;
    }

    public String label() {
        return label;
    }

    public Position position() {
        return position;
    }

    public boolean required() {
        return required;
    }
}
