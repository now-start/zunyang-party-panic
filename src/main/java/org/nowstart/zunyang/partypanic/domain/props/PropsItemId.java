package org.nowstart.zunyang.partypanic.domain.props;

import org.nowstart.zunyang.partypanic.domain.common.Position;

public enum PropsItemId {
    RIBBON_BOX("리본 상자", new Position(1, 3), true),
    TOPPER_CASE("토퍼 케이스", new Position(3, 3), true),
    GEL_PACK("조명 젤 팩", new Position(5, 3), true),
    FABRIC_ROLL("예비 천 롤", new Position(1, 1), false),
    BASKET_SET("소형 바구니 세트", new Position(3, 1), false),
    MEMORY_DECOR("기록 장식 소품", new Position(5, 1), false);

    private final String label;
    private final Position position;
    private final boolean required;

    PropsItemId(String label, Position position, boolean required) {
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
