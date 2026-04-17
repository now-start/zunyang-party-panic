package org.nowstart.zunyang.partypanic.domain.handover;

import org.nowstart.zunyang.partypanic.domain.common.Position;

public enum HandoverClueId {
    OLD_CUESHEET("낡은 큐시트", new Position(1, 3), true),
    PHOTO_FRAME("예전 사진 프레임", new Position(3, 3), true),
    MEMO_BOARD("메모 보드", new Position(5, 3), true),
    PROJECTOR("프로젝터", new Position(1, 1), false),
    ACCESS_PASS("이전 출입 패스", new Position(3, 1), false),
    PACKED_BOX("포장 흔적 상자", new Position(5, 1), false);

    private final String label;
    private final Position position;
    private final boolean required;

    HandoverClueId(String label, Position position, boolean required) {
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
