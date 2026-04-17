package org.nowstart.zunyang.partypanic.domain.message;

public enum MessageNoteId {
    FIRST_GREETING("첫 인사", "당신답게 시작했으면 좋겠어요.", true),
    QUIET_MOMENT("조용한 순간", "조용한 순간도 좋아해요.", true),
    WAITING_LINE("기다린 인사", "늘 기다리게 되는 첫 인사.", false),
    MEMORY_WISH("기억의 장면", "오래 기억할 장면이 되길.", false),
    TONIGHT_WISH("오늘의 밤", "오늘 밤이 오래 웃어 줬으면.", false);

    private final String label;
    private final String excerpt;
    private final boolean required;

    MessageNoteId(String label, String excerpt, boolean required) {
        this.label = label;
        this.excerpt = excerpt;
        this.required = required;
    }

    public String label() {
        return label;
    }

    public String excerpt() {
        return excerpt;
    }

    public boolean required() {
        return required;
    }
}
