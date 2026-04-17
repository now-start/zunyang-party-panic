package org.nowstart.zunyang.partypanic.domain.session;

public record EndingSignals(
    int setupCareScore,
    int archiveDepthScore,
    int messageWarmthScore
) {

    public EndingSignals {
        if (setupCareScore < 0 || archiveDepthScore < 0 || messageWarmthScore < 0) {
            throw new IllegalArgumentException("Ending signal scores must not be negative");
        }
    }

    public int totalScore() {
        return setupCareScore + archiveDepthScore + messageWarmthScore;
    }
}
