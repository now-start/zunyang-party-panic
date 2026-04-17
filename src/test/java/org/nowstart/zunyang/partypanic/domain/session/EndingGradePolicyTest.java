package org.nowstart.zunyang.partypanic.domain.session;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EndingGradePolicyTest {

    @Test
    void returns_shared_stage_for_high_scores_across_all_signals() {
        EndingGrade endingGrade = EndingGradePolicy.evaluate(new EndingSignals(4, 2, 2));

        assertEquals(EndingGrade.SHARED_STAGE, endingGrade);
    }

    @Test
    void returns_warm_night_for_mid_tier_supportive_run() {
        EndingGrade endingGrade = EndingGradePolicy.evaluate(new EndingSignals(2, 1, 1));

        assertEquals(EndingGrade.WARM_NIGHT, endingGrade);
    }

    @Test
    void returns_steady_start_when_scores_stay_minimal() {
        EndingGrade endingGrade = EndingGradePolicy.evaluate(new EndingSignals(1, 0, 0));

        assertEquals(EndingGrade.STEADY_START, endingGrade);
    }
}
