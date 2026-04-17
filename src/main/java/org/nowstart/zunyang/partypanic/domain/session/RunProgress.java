package org.nowstart.zunyang.partypanic.domain.session;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;

public record RunProgress(
    SessionPhase phase,
    Set<ChapterId> completedChapters,
    boolean placeholderArtEnabled,
    EndingGrade endingGrade
) {

    public RunProgress {
        Objects.requireNonNull(phase, "phase must not be null");
        Objects.requireNonNull(completedChapters, "completedChapters must not be null");

        EnumSet<ChapterId> normalizedCompleted = EnumSet.noneOf(ChapterId.class);
        normalizedCompleted.addAll(completedChapters);
        completedChapters = Collections.unmodifiableSet(normalizedCompleted);
    }

    public static RunProgress initial() {
        return new RunProgress(SessionPhase.PREP_CALL, Set.of(), true, null);
    }

    public boolean isCompleted(ChapterId chapterId) {
        return completedChapters.contains(chapterId);
    }

    public boolean isUnlocked(ChapterId chapterId) {
        Objects.requireNonNull(chapterId, "chapterId must not be null");
        if (chapterId == ChapterId.SIGNAL) {
            return true;
        }
        ChapterId[] storyOrder = ChapterId.values();
        return completedChapters.contains(storyOrder[chapterId.ordinal() - 1]);
    }

    public RunProgress markCompleted(ChapterId chapterId) {
        Objects.requireNonNull(chapterId, "chapterId must not be null");
        if (completedChapters.contains(chapterId)) {
            return this;
        }

        EnumSet<ChapterId> nextCompleted = EnumSet.noneOf(ChapterId.class);
        nextCompleted.addAll(completedChapters);
        nextCompleted.add(chapterId);

        return new RunProgress(nextPhase(nextCompleted), nextCompleted, placeholderArtEnabled, endingGrade);
    }

    public RunProgress withEndingGrade(EndingGrade endingGrade) {
        Objects.requireNonNull(endingGrade, "endingGrade must not be null");
        return new RunProgress(phase, completedChapters, placeholderArtEnabled, endingGrade);
    }

    private static SessionPhase nextPhase(Set<ChapterId> completedChapters) {
        if (completedChapters.contains(ChapterId.FINALE)) {
            return SessionPhase.COMPLETED;
        }
        if (completedChapters.contains(ChapterId.MESSAGE)) {
            return SessionPhase.FINALE_READY;
        }
        if (completedChapters.isEmpty()) {
            return SessionPhase.PREP_CALL;
        }
        return SessionPhase.HUB_EXPLORATION;
    }
}
