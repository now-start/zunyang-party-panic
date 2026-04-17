package org.nowstart.zunyang.partypanic.domain.photo;

public enum PhotoFocusId {
    FRAME_GUIDE("프레임 가이드", true),
    STOOL_MARK("스툴 위치", true),
    KEY_LIGHT("키 라이트", true),
    BACKDROP_LINE("배경 천 선", false),
    FLOOR_DECOR("바닥 장식", false);

    private final String label;
    private final boolean required;

    PhotoFocusId(String label, boolean required) {
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
