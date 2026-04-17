package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

public enum SampleTextureId {
    HUB_BACKGROUND("준비동 허브"),
    HELPER_ACTOR("조력자"),
    SIGNAL_CARD("첫 신호"),
    PROPS_CARD("소품 회수"),
    CENTERPIECE_CARD("중심 연출"),
    PHOTO_CARD("포토 베이"),
    HANDOVER_CARD("기록 복도"),
    MESSAGE_CARD("메시지 월"),
    LOCKED_CARD("잠긴 챕터"),
    MESSAGE_PANEL("메시지 패널"),
    STREAMER_NPC("스트리머"),
    FINALE_STAGE("피날레");

    private final String label;

    SampleTextureId(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
