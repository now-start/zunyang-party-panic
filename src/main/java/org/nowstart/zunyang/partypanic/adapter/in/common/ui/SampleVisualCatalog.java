package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

import java.util.Locale;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;

public final class SampleVisualCatalog {

    private static final SampleVisualCatalog DEFAULT = new SampleVisualCatalog();

    private SampleVisualCatalog() {
    }

    public static SampleVisualCatalog defaultCatalog() {
        return DEFAULT;
    }

    public SampleTextureId chapterCard(ChapterId chapterId) {
        if (chapterId == null) {
            return SampleTextureId.LOCKED_CARD;
        }
        return switch (chapterId) {
            case SIGNAL -> SampleTextureId.SIGNAL_CARD;
            case PROPS -> SampleTextureId.PROPS_CARD;
            case CENTERPIECE -> SampleTextureId.CENTERPIECE_CARD;
            case PHOTO -> SampleTextureId.PHOTO_CARD;
            case HANDOVER -> SampleTextureId.HANDOVER_CARD;
            case MESSAGE -> SampleTextureId.MESSAGE_CARD;
            case FINALE -> SampleTextureId.FINALE_STAGE;
        };
    }

    public SampleTextureId visualToken(String visualToken) {
        if (visualToken == null || visualToken.isBlank()) {
            return SampleTextureId.HUB_BACKGROUND;
        }
        return switch (visualToken.trim().toLowerCase(Locale.ROOT)) {
            case "signal" -> SampleTextureId.SIGNAL_CARD;
            case "props" -> SampleTextureId.PROPS_CARD;
            case "centerpiece" -> SampleTextureId.CENTERPIECE_CARD;
            case "photo" -> SampleTextureId.PHOTO_CARD;
            case "handover" -> SampleTextureId.HANDOVER_CARD;
            case "message" -> SampleTextureId.MESSAGE_CARD;
            case "finale" -> SampleTextureId.FINALE_STAGE;
            default -> SampleTextureId.HUB_BACKGROUND;
        };
    }
}
