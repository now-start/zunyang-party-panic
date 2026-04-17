package org.nowstart.zunyang.partypanic.adapter.out.save;

import com.badlogic.gdx.Preferences;
import java.util.Objects;
import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSessionSnapshotPort;
import org.nowstart.zunyang.partypanic.domain.session.RunProgress;

public final class RuntimeSessionSnapshotAdapter
    implements LoadSessionSnapshotPort, SaveSessionSnapshotPort {

    private final InMemoryRunProgressAdapter fallbackAdapter = new InMemoryRunProgressAdapter();

    private LoadSessionSnapshotPort loadDelegate = fallbackAdapter;
    private SaveSessionSnapshotPort saveDelegate = fallbackAdapter;

    @Override
    public Optional<RunProgress> load() {
        return loadDelegate.load();
    }

    @Override
    public void save(RunProgress runProgress) {
        saveDelegate.save(runProgress);
    }

    public void bindToPreferences(Preferences preferences) {
        Objects.requireNonNull(preferences, "preferences must not be null");
        PreferencesSessionSnapshotAdapter adapter = new PreferencesSessionSnapshotAdapter(preferences);
        bind(adapter, adapter);
    }

    void bind(
        LoadSessionSnapshotPort loadSessionSnapshotPort,
        SaveSessionSnapshotPort saveSessionSnapshotPort
    ) {
        Objects.requireNonNull(loadSessionSnapshotPort, "loadSessionSnapshotPort must not be null");
        Objects.requireNonNull(saveSessionSnapshotPort, "saveSessionSnapshotPort must not be null");

        Optional<RunProgress> currentSnapshot = loadDelegate.load();
        loadDelegate = loadSessionSnapshotPort;
        saveDelegate = saveSessionSnapshotPort;

        if (loadDelegate.load().isEmpty() && currentSnapshot.isPresent()) {
            saveDelegate.save(currentSnapshot.orElseThrow());
        }
    }
}
