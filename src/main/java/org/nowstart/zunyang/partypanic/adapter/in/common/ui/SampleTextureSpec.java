package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

import com.badlogic.gdx.graphics.Color;

public record SampleTextureSpec(
    String assetPath,
    int width,
    int height,
    Color fill,
    Color panel,
    Color border
) {

    public static SampleTextureSpec forId(SampleTextureId id) {
        return switch (id) {
            case HUB_BACKGROUND -> new SampleTextureSpec(
                "assets/images/backgrounds/desk-party-stage.png",
                1280,
                720,
                Color.valueOf("2F3440"),
                Color.valueOf("5B677A"),
                Color.valueOf("F7D794")
            );
            case HELPER_ACTOR -> new SampleTextureSpec(
                null,
                256,
                384,
                Color.valueOf("405D72"),
                Color.valueOf("758694"),
                Color.valueOf("F7E7DC")
            );
            case SIGNAL_CARD -> new SampleTextureSpec(
                "assets/images/events/audio-pop-card.png",
                480,
                270,
                Color.valueOf("4D96FF"),
                Color.valueOf("6BCBFF"),
                Color.valueOf("EAF6FF")
            );
            case PROPS_CARD -> new SampleTextureSpec(
                "assets/images/choices/mini-game-card.png",
                480,
                270,
                Color.valueOf("A66CFF"),
                Color.valueOf("C499FF"),
                Color.valueOf("F5EFFF")
            );
            case CENTERPIECE_CARD -> new SampleTextureSpec(
                "assets/images/events/cake-balance-card.png",
                480,
                270,
                Color.valueOf("FF8B8B"),
                Color.valueOf("FFC2C2"),
                Color.valueOf("FFF1F1")
            );
            case PHOTO_CARD -> new SampleTextureSpec(
                "assets/images/events/photo-time-card.png",
                480,
                270,
                Color.valueOf("3EC1D3"),
                Color.valueOf("82DBD8"),
                Color.valueOf("E8FBFF")
            );
            case HANDOVER_CARD -> new SampleTextureSpec(
                "assets/images/backgrounds/mint-cats-stage.png",
                480,
                270,
                Color.valueOf("5F6F52"),
                Color.valueOf("A9B388"),
                Color.valueOf("F1F6E8")
            );
            case MESSAGE_CARD -> new SampleTextureSpec(
                "assets/images/choices/fan-letter-card.png",
                480,
                270,
                Color.valueOf("FFB84C"),
                Color.valueOf("FFD93D"),
                Color.valueOf("FFF7D6")
            );
            case LOCKED_CARD -> new SampleTextureSpec(
                null,
                480,
                270,
                Color.valueOf("444B58"),
                Color.valueOf("68707F"),
                Color.valueOf("D4D8DD")
            );
            case MESSAGE_PANEL -> new SampleTextureSpec(
                "assets/images/ui/result-card-background.png",
                1280,
                240,
                Color.valueOf("2D3250"),
                Color.valueOf("424769"),
                Color.valueOf("F6B17A")
            );
            case STREAMER_NPC -> new SampleTextureSpec(
                "assets/images/characters/zunyang-birthday-host.png",
                320,
                480,
                Color.valueOf("D9A7FF"),
                Color.valueOf("F1D4FF"),
                Color.valueOf("FFF6FF")
            );
            case FINALE_STAGE -> new SampleTextureSpec(
                "assets/images/backgrounds/finale-stage.png",
                640,
                360,
                Color.valueOf("7D5BA6"),
                Color.valueOf("B784E3"),
                Color.valueOf("FFF1FF")
            );
        };
    }
}
