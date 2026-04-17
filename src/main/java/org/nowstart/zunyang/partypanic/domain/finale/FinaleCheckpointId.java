package org.nowstart.zunyang.partypanic.domain.finale;

public enum FinaleCheckpointId {
    COUNTDOWN_LAMP("카운트다운 램프", true),
    STREAMER_MARK("입장 위치 마크", true),
    GO_CUE_PANEL("큐 사인 패널", true),
    SIDE_CURTAIN("사이드 커튼", false),
    PROP_TABLE("대기 소품 테이블", false);

    private final String label;
    private final boolean required;

    FinaleCheckpointId(String label, boolean required) {
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
