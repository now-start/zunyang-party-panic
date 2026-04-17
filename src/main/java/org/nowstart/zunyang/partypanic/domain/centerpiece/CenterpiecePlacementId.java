package org.nowstart.zunyang.partypanic.domain.centerpiece;

public enum CenterpiecePlacementId {
    TOPPER_SLOT("토퍼 위치", true),
    CANDLE_ARC("촛불 아치", true),
    RIBBON_LINE("리본 선", true),
    CLOTH_FOLD("테이블 천", false),
    SIDE_DECOR("주변 장식", false);

    private final String label;
    private final boolean required;

    CenterpiecePlacementId(String label, boolean required) {
        this.label = label;
        this.required = required;
    }

    public String label() {
        return label;
    }

    public boolean required() {
        return required;
    }
}
