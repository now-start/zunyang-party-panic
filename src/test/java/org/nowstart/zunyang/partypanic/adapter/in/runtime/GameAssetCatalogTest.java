package org.nowstart.zunyang.partypanic.adapter.in.runtime;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameAssetCatalogTest {

    @Test
    void resolvePhotoCardTexturePathPrefersPrimaryPath() {
        String resolvedPath = GameAssetCatalog.resolvePhotoCardTexturePath(
                path -> path.equals(GameAssetCatalog.PHOTO_CARD_PRIMARY_TEXTURE)
        );

        assertEquals(GameAssetCatalog.PHOTO_CARD_PRIMARY_TEXTURE, resolvedPath);
    }

    @Test
    void resolvePhotoCardTexturePathFallsBackToSecondaryPath() {
        String resolvedPath = GameAssetCatalog.resolvePhotoCardTexturePath(
                path -> path.equals(GameAssetCatalog.PHOTO_CARD_FALLBACK_TEXTURE)
        );

        assertEquals(GameAssetCatalog.PHOTO_CARD_FALLBACK_TEXTURE, resolvedPath);
    }

    @Test
    void requiredTexturePathsContainNoDuplicates() {
        List<String> texturePaths = GameAssetCatalog.requiredTexturePaths(GameAssetCatalog.PHOTO_CARD_PRIMARY_TEXTURE);
        Set<String> uniqueTexturePaths = new HashSet<>(texturePaths);

        assertEquals(uniqueTexturePaths.size(), texturePaths.size());
        assertTrue(texturePaths.contains(GameAssetCatalog.HOST_TEXTURE));
        assertTrue(texturePaths.contains(GameAssetCatalog.PHOTO_CARD_PRIMARY_TEXTURE));
        assertTrue(texturePaths.contains(GameAssetCatalog.TITLE_BACKGROUND_TEXTURE));
    }
}
