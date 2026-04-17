package org.nowstart.zunyang.partypanic.domain.props;

public enum PropsItemId {
    RIBBON_BOX("리본 상자", true),
    TOPPER_CASE("토퍼 케이스", true),
    GEL_PACK("조명 젤 팩", true),
    FABRIC_ROLL("예비 천 롤", false),
    BASKET_SET("소형 바구니 세트", false),
    MEMORY_DECOR("기록 장식 소품", false);

    private final String label;
    private final boolean required;

    PropsItemId(String label, boolean required) {
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
