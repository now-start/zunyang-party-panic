package org.nowstart.zunyang.partypanic.domain.session;

public final class EndingGradePolicy {

    private EndingGradePolicy() {
    }

    public static EndingGrade evaluate(EndingSignals signals) {
        if (signals.setupCareScore() >= 4
            && signals.archiveDepthScore() >= 2
            && signals.messageWarmthScore() >= 2) {
            return EndingGrade.SHARED_STAGE;
        }

        if (signals.setupCareScore() >= 2
            && (signals.archiveDepthScore() >= 1 || signals.messageWarmthScore() >= 1)
            && signals.totalScore() >= 4) {
            return EndingGrade.WARM_NIGHT;
        }

        return EndingGrade.STEADY_START;
    }
}
