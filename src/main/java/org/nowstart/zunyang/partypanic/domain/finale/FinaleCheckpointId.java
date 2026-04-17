package org.nowstart.zunyang.partypanic.domain.finale;

import org.nowstart.zunyang.partypanic.domain.common.Position;

public enum FinaleCheckpointId {
    COUNTDOWN_LAMP("카운트다운 램프", new Position(1, 3), true),
    STREAMER_MARK("입장 위치 마크", new Position(3, 3), true),
    GO_CUE_PANEL("큐 사인 패널", new Position(5, 3), true),
    SIDE_CURTAIN("사이드 커튼", new Position(1, 1), false),
    PROP_TABLE("대기 소품 테이블", new Position(5, 1), false);

    private final String label;
    private final Position position;
    private final boolean required;

    FinaleCheckpointId(String label, Position position, boolean required) {
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
