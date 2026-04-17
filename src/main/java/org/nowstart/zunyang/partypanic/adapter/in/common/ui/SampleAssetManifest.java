package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

public final class SampleAssetManifest {

    private static final SampleAssetManifest DEFAULT = new SampleAssetManifest();

    private SampleAssetManifest() {
    }

    public static SampleAssetManifest defaultManifest() {
        return DEFAULT;
    }

    public String texturePath(SampleTextureId id) {
        return switch (id) {
            case HUB_BACKGROUND -> "assets/images/backgrounds/desk-party-stage.png";
            case HELPER_ACTOR -> null;
            case SIGNAL_CARD -> "assets/images/events/audio-pop-card.png";
            case PROPS_CARD -> "assets/images/choices/mini-game-card.png";
            case CENTERPIECE_CARD -> "assets/images/events/cake-balance-card.png";
            case PHOTO_CARD -> "assets/images/events/photo-time-card.png";
            case HANDOVER_CARD -> "assets/images/backgrounds/mint-cats-stage.png";
            case MESSAGE_CARD -> "assets/images/choices/fan-letter-card.png";
            case LOCKED_CARD -> null;
            case MESSAGE_PANEL -> "assets/images/ui/result-card-background.png";
            case STREAMER_NPC -> "assets/images/characters/zunyang-birthday-host.png";
            case FINALE_STAGE -> "assets/images/backgrounds/finale-stage.png";
        };
    }

    public String fontPath(SampleFontId id) {
        return switch (id) {
            case TITLE -> "assets/fonts/ui-title.ttf";
            case BODY, COMPACT -> "assets/fonts/ui-body.ttf";
        };
    }

    public String iconPath(SampleIconId id) {
        return switch (id) {
            case MOVE -> "assets/images/ui/icons/move.png";
            case INTERACT -> "assets/images/ui/icons/interact.png";
            case OPEN_CHAPTER -> "assets/images/ui/icons/open-chapter.png";
            case NEXT -> "assets/images/ui/icons/next.png";
            case SKIP -> "assets/images/ui/icons/skip.png";
            case DEBUG -> "assets/images/ui/icons/debug.png";
            case RESTART -> "assets/images/ui/icons/restart.png";
            case HUB -> "assets/images/ui/icons/hub.png";
        };
    }
}
