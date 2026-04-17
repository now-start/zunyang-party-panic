package org.nowstart.zunyang.partypanic.adapter.out.save;

import com.badlogic.gdx.Preferences;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.session.EndingGrade;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;
import org.nowstart.zunyang.partypanic.domain.session.SessionPhase;

public final class PreferencesSessionSnapshotAdapter
    implements LoadSessionSnapshotPort, SaveSessionSnapshotPort {

    private static final String KEY_PHASE = "session.phase";
    private static final String KEY_COMPLETED_CHAPTERS = "session.completedChapters";
    private static final String KEY_PLACEHOLDER_ART = "session.placeholderArtEnabled";
    private static final String KEY_ENDING_GRADE = "session.endingGrade";

    private final Preferences preferences;

    public PreferencesSessionSnapshotAdapter(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Optional<RunProgress> load() {
        if (!preferences.contains(KEY_PHASE)) {
            return Optional.empty();
        }

        SessionPhase phase = SessionPhase.valueOf(
            preferences.getString(KEY_PHASE, SessionPhase.PREP_CALL.name())
        );
        Set<ChapterId> completedChapters = parseCompletedChapters(
            preferences.getString(KEY_COMPLETED_CHAPTERS, "")
        );
        boolean placeholderArtEnabled = preferences.getBoolean(KEY_PLACEHOLDER_ART, true);
        EndingGrade endingGrade = parseEndingGrade(preferences.getString(KEY_ENDING_GRADE, ""));

        return Optional.of(new RunProgress(phase, completedChapters, placeholderArtEnabled, endingGrade));
    }

    @Override
    public void save(RunProgress runProgress) {
        preferences.putString(KEY_PHASE, runProgress.phase().name());
        preferences.putString(
            KEY_COMPLETED_CHAPTERS,
            runProgress.completedChapters().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","))
        );
        preferences.putBoolean(KEY_PLACEHOLDER_ART, runProgress.placeholderArtEnabled());
        preferences.putString(
            KEY_ENDING_GRADE,
            runProgress.endingGrade() == null ? "" : runProgress.endingGrade().name()
        );
        preferences.flush();
    }

    private static Set<ChapterId> parseCompletedChapters(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Set.of();
        }

        EnumSet<ChapterId> completed = EnumSet.noneOf(ChapterId.class);
        Arrays.stream(rawValue.split(","))
            .map(String::trim)
            .filter(value -> !value.isEmpty())
            .map(ChapterId::valueOf)
            .forEach(completed::add);
        return completed;
    }

    private static EndingGrade parseEndingGrade(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        return EndingGrade.valueOf(rawValue.trim());
    }
}
