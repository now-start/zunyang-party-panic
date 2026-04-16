package org.nowstart.zunyang.partypanic.domain.progress;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameProgressTest {

    @Test
    void keepsBestScoreForEachActivity() {
        GameProgress progress = new GameProgress();

        progress.recordScore(ActivityId.BROADCAST_DESK, 12);
        progress.recordScore(ActivityId.BROADCAST_DESK, 9);
        progress.recordScore(ActivityId.BROADCAST_DESK, 18);

        assertTrue(progress.isCompleted(ActivityId.BROADCAST_DESK));
        assertEquals(18, progress.getBestScore(ActivityId.BROADCAST_DESK));
        assertEquals(1, progress.getCompletedCount());
    }

    @Test
    void updatesNextObjectiveAsActivitiesComplete() {
        GameProgress progress = new GameProgress();

        assertTrue(progress.getNextObjective().contains("방송 책상"));
        assertTrue(progress.isUnlocked(ActivityId.BROADCAST_DESK));
        assertEquals(7, progress.getTotalActivityCount());

        progress.recordScore(ActivityId.BROADCAST_DESK, 10);
        assertTrue(progress.getNextObjective().contains("장식 창고"));
        assertTrue(progress.isUnlocked(ActivityId.STORAGE_ROOM));

        progress.markCompleted(ActivityId.STORAGE_ROOM);
        assertTrue(progress.getNextObjective().contains("케이크 테이블"));
        assertTrue(progress.isUnlocked(ActivityId.CAKE_TABLE));

        progress.recordScore(ActivityId.CAKE_TABLE, 8);
        assertTrue(progress.getNextObjective().contains("포토존"));
        assertTrue(progress.isUnlocked(ActivityId.PHOTO_TIME));

        progress.recordScore(ActivityId.PHOTO_TIME, 12);
        assertTrue(progress.isUnlocked(ActivityId.BACKSTAGE));

        progress.markCompleted(ActivityId.BACKSTAGE);
        assertTrue(progress.isUnlocked(ActivityId.FAN_LETTER));

        progress.markCompleted(ActivityId.FAN_LETTER);
        assertTrue(progress.isUnlocked(ActivityId.FINALE_STAGE));
    }

    @Test
    void resolvesEndingToneFromBestScores() {
        GameProgress progress = new GameProgress();

        assertEquals("조용한 엔딩", progress.getEndingTitle());

        progress.recordScore(ActivityId.BROADCAST_DESK, 80);
        progress.recordScore(ActivityId.CAKE_TABLE, 70);
        progress.recordScore(ActivityId.PHOTO_TIME, 40);
        assertEquals("따뜻한 엔딩", progress.getEndingTitle());

        progress.recordScore(ActivityId.PHOTO_TIME, 100);
        assertEquals("진심 엔딩", progress.getEndingTitle());
    }
}
