package org.nowstart.zunyang.partypanic.domain.handover;

public enum HandoverClueId {
    OLD_CUESHEET("낡은 큐시트", true),
    PHOTO_FRAME("예전 사진 프레임", true),
    MEMO_BOARD("메모 보드", true),
    PROJECTOR("프로젝터", false),
    ACCESS_PASS("이전 출입 패스", false),
    PACKED_BOX("포장 흔적 상자", false);

    private final String label;
    private final boolean required;

    HandoverClueId(String label, boolean required) {
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
