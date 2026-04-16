package org.nowstart.zunyang.partypanic.world;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameProgressTest {

    @Test
    void keepsBestScoreForEachActivity() {
        GameProgress progress = new GameProgress();

        progress.recordScore(GameProgress.BROADCAST_DESK, 12);
        progress.recordScore(GameProgress.BROADCAST_DESK, 9);
        progress.recordScore(GameProgress.BROADCAST_DESK, 18);

        assertTrue(progress.isCompleted(GameProgress.BROADCAST_DESK));
        assertEquals(18, progress.getBestScore(GameProgress.BROADCAST_DESK));
        assertEquals(1, progress.getCompletedCount());
    }

    @Test
    void updatesNextObjectiveAsActivitiesComplete() {
        GameProgress progress = new GameProgress();

        assertTrue(progress.getNextObjective().contains("방송 책상"));
        assertTrue(progress.isUnlocked(GameProgress.BROADCAST_DESK));
        assertEquals(7, progress.getTotalActivityCount());

        progress.recordScore(GameProgress.BROADCAST_DESK, 10);
        assertTrue(progress.getNextObjective().contains("장식 창고"));
        assertTrue(progress.isUnlocked(GameProgress.STORAGE_ROOM));

        progress.markCompleted(GameProgress.STORAGE_ROOM);
        assertTrue(progress.getNextObjective().contains("케이크 테이블"));
        assertTrue(progress.isUnlocked(GameProgress.CAKE_TABLE));

        progress.recordScore(GameProgress.CAKE_TABLE, 8);
        assertTrue(progress.getNextObjective().contains("포토존"));
        assertTrue(progress.isUnlocked(GameProgress.PHOTO_TIME));

        progress.recordScore(GameProgress.PHOTO_TIME, 12);
        assertTrue(progress.isUnlocked(GameProgress.BACKSTAGE));

        progress.markCompleted(GameProgress.BACKSTAGE);
        assertTrue(progress.isUnlocked(GameProgress.FAN_LETTER));

        progress.markCompleted(GameProgress.FAN_LETTER);
        assertTrue(progress.isUnlocked(GameProgress.FINALE_STAGE));
    }

    @Test
    void resolvesEndingToneFromBestScores() {
        GameProgress progress = new GameProgress();

        assertEquals("조용한 엔딩", progress.getEndingTitle());

        progress.recordScore(GameProgress.BROADCAST_DESK, 80);
        progress.recordScore(GameProgress.CAKE_TABLE, 70);
        progress.recordScore(GameProgress.PHOTO_TIME, 40);
        assertEquals("따뜻한 엔딩", progress.getEndingTitle());

        progress.recordScore(GameProgress.PHOTO_TIME, 100);
        assertEquals("진심 엔딩", progress.getEndingTitle());
    }
}
