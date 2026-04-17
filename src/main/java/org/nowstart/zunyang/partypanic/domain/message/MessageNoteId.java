package org.nowstart.zunyang.partypanic.domain.message;

import org.nowstart.zunyang.partypanic.domain.common.Position;

public enum MessageNoteId {
    FIRST_GREETING("첫 인사", "당신답게 시작했으면 좋겠어요.", new Position(1, 3), true),
    QUIET_MOMENT("조용한 순간", "조용한 순간도 좋아해요.", new Position(5, 3), true),
    WAITING_LINE("기다린 인사", "늘 기다리게 되는 첫 인사.", new Position(1, 1), false),
    MEMORY_WISH("기억의 장면", "오래 기억할 장면이 되길.", new Position(3, 1), false),
    TONIGHT_WISH("오늘의 밤", "오늘 밤이 오래 웃어 줬으면.", new Position(5, 1), false);

    private final String label;
    private final String excerpt;
    private final Position position;
    private final boolean required;

    MessageNoteId(String label, String excerpt, Position position, boolean required) {
        this.label = label;
        this.excerpt = excerpt;
        this.position = position;
        this.required = required;
    }

    public String label() {
        return label;
    }

    public String excerpt() {
        return excerpt;
    }

    public Position position() {
        return position;
    }

    public boolean required() {
        return required;
    }
}
