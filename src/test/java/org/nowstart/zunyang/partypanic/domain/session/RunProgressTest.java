package org.nowstart.zunyang.partypanic.domain.session;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;

class RunProgressTest {

    @Test
    void chapters_unlock_in_story_order() {
        RunProgress progress = RunProgress.initial();

        assertNull(progress.endingGrade());
        assertTrue(progress.isUnlocked(ChapterId.SIGNAL));
        assertFalse(progress.isUnlocked(ChapterId.PROPS));

        progress = progress.markCompleted(ChapterId.SIGNAL);
        assertTrue(progress.isUnlocked(ChapterId.PROPS));
        assertFalse(progress.isUnlocked(ChapterId.CENTERPIECE));

        progress = progress.markCompleted(ChapterId.PROPS)
            .markCompleted(ChapterId.CENTERPIECE)
            .markCompleted(ChapterId.PHOTO)
            .markCompleted(ChapterId.HANDOVER)
            .markCompleted(ChapterId.MESSAGE);

        assertTrue(progress.isUnlocked(ChapterId.FINALE));
    }

    @Test
    void completing_finale_moves_session_to_completed() {
        RunProgress progress = RunProgress.initial()
            .markCompleted(ChapterId.SIGNAL)
            .markCompleted(ChapterId.PROPS)
            .markCompleted(ChapterId.CENTERPIECE)
            .markCompleted(ChapterId.PHOTO)
            .markCompleted(ChapterId.HANDOVER)
            .markCompleted(ChapterId.MESSAGE)
            .markCompleted(ChapterId.FINALE);

        assertEquals(SessionPhase.COMPLETED, progress.phase());
        assertTrue(progress.isCompleted(ChapterId.FINALE));
    }

    @Test
    void with_ending_grade_preserves_progress_and_sets_grade() {
        RunProgress progress = RunProgress.initial()
            .markCompleted(ChapterId.SIGNAL)
            .withEndingGrade(EndingGrade.WARM_NIGHT);

        assertTrue(progress.isCompleted(ChapterId.SIGNAL));
        assertEquals(EndingGrade.WARM_NIGHT, progress.endingGrade());
    }
}
