package org.nowstart.zunyang.partypanic.adapter.in.common.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;

class SampleAssetManifestTest {

    @Test
    void default_manifest_exposes_default_asset_paths() {
        SampleAssetManifest manifest = SampleAssetManifest.defaultManifest();

        assertEquals(
            "assets/images/backgrounds/desk-party-stage.png",
            manifest.texturePath(SampleTextureId.HUB_BACKGROUND)
        );
        assertEquals(
            "assets/fonts/ui-title.ttf",
            manifest.fontPath(SampleFontId.TITLE)
        );
        assertEquals(
            "assets/images/ui/icons/next.png",
            manifest.iconPath(SampleIconId.NEXT)
        );
        assertNull(manifest.texturePath(SampleTextureId.HELPER_ACTOR));
    }

    @Test
    void default_visual_catalog_resolves_chapter_and_visual_tokens() {
        SampleVisualCatalog catalog = SampleVisualCatalog.defaultCatalog();

        assertEquals(SampleTextureId.SIGNAL_CARD, catalog.chapterCard(ChapterId.SIGNAL));
        assertEquals(SampleTextureId.MESSAGE_CARD, catalog.visualToken("message"));
        assertEquals(SampleTextureId.HUB_BACKGROUND, catalog.visualToken("unknown"));
    }
}
