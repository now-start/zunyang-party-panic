package org.nowstart.zunyang.partypanic.adapter.in.runtime;

import java.util.List;
import java.util.function.Predicate;

final class GameAssetCatalog {
    static final String BODY_FONT_ASSET = "ui-body.ttf";
    static final String BODY_FONT_FILE = "assets/fonts/NanumSquareRoundR.ttf";
    static final String TITLE_FONT_ASSET = "ui-title.ttf";
    static final String TITLE_FONT_FILE = "assets/fonts/GowunDodum-Regular.ttf";

    static final String HOST_TEXTURE = "assets/images/characters/zunyang-birthday-host.png";
    static final String TITLE_BACKGROUND_TEXTURE = "assets/images/backgrounds/finale-stage.png";
    static final String DESK_BACKGROUND_TEXTURE = "assets/images/backgrounds/desk-party-stage.png";
    static final String CAKE_BACKGROUND_TEXTURE = "assets/images/backgrounds/cake-rush-stage.png";
    static final String MINT_BACKGROUND_TEXTURE = "assets/images/backgrounds/mint-cats-stage.png";
    static final String CAKE_CARD_TEXTURE = "assets/images/events/cake-balance-card.png";
    static final String PHOTO_CARD_PRIMARY_TEXTURE = "assets/images/choices/photo-time-card.png";
    static final String PHOTO_CARD_FALLBACK_TEXTURE = "assets/images/events/photo-time-card.png";

    private GameAssetCatalog() {
    }

    static List<String> requiredTexturePaths(String photoCardTexturePath) {
        return List.of(
                HOST_TEXTURE,
                TITLE_BACKGROUND_TEXTURE,
                DESK_BACKGROUND_TEXTURE,
                CAKE_BACKGROUND_TEXTURE,
                MINT_BACKGROUND_TEXTURE,
                CAKE_CARD_TEXTURE,
                photoCardTexturePath
        );
    }

    static String resolvePhotoCardTexturePath(Predicate<String> exists) {
        for (String candidate : List.of(PHOTO_CARD_PRIMARY_TEXTURE, PHOTO_CARD_FALLBACK_TEXTURE)) {
            if (exists.test(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Missing texture. Tried: " + PHOTO_CARD_PRIMARY_TEXTURE + ", " + PHOTO_CARD_FALLBACK_TEXTURE);
    }
}
