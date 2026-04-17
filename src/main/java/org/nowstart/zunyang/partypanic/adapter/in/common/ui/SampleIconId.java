package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

public enum SampleIconId {
    MOVE("이동"),
    INTERACT("조사"),
    OPEN_CHAPTER("챕터 진입"),
    NEXT("다음"),
    SKIP("스킵"),
    DEBUG("디버그"),
    RESTART("재시작"),
    HUB("허브 복귀");

    private final String label;

    SampleIconId(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
