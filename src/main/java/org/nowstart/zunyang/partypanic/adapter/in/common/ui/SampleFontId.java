package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

public enum SampleFontId {
    TITLE("타이틀 폰트"),
    BODY("기본 본문 폰트"),
    COMPACT("조밀한 본문 폰트");

    private final String label;

    SampleFontId(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
