package org.nowstart.zunyang.partypanic.domain.photo;

import org.nowstart.zunyang.partypanic.domain.common.Position;

public enum PhotoFocusId {
    FRAME_GUIDE("프레임 가이드", new Position(3, 4), true),
    STOOL_MARK("스툴 위치", new Position(1, 2), true),
    KEY_LIGHT("키 라이트", new Position(5, 2), true),
    BACKDROP_LINE("배경 천 선", new Position(3, 0), false),
    FLOOR_DECOR("바닥 장식", new Position(5, 4), false);

    private final String label;
    private final Position position;
    private final boolean required;

    PhotoFocusId(String label, Position position, boolean required) {
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
