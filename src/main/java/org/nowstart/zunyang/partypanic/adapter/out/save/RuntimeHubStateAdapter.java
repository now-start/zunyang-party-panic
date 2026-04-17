package org.nowstart.zunyang.partypanic.adapter.out.save;

import com.badlogic.gdx.Preferences;
import java.util.Objects;
import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHubStatePort;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;

public final class RuntimeHubStateAdapter implements LoadHubStatePort, SaveHubStatePort {

    private final InMemoryHubStateAdapter fallbackAdapter = new InMemoryHubStateAdapter();

    private LoadHubStatePort loadDelegate = fallbackAdapter;
    private SaveHubStatePort saveDelegate = fallbackAdapter;

    @Override
    public Optional<HubState> load() {
        return loadDelegate.load();
    }

    @Override
    public void save(HubState hubState) {
        saveDelegate.save(hubState);
    }

    public void bindToPreferences(Preferences preferences, LoadHubLayoutPort loadHubLayoutPort) {
        Objects.requireNonNull(preferences, "preferences must not be null");
        Objects.requireNonNull(loadHubLayoutPort, "loadHubLayoutPort must not be null");
        PreferencesHubStateAdapter adapter = new PreferencesHubStateAdapter(preferences, loadHubLayoutPort);
        bind(adapter, adapter);
    }

    void bind(LoadHubStatePort loadHubStatePort, SaveHubStatePort saveHubStatePort) {
        Objects.requireNonNull(loadHubStatePort, "loadHubStatePort must not be null");
        Objects.requireNonNull(saveHubStatePort, "saveHubStatePort must not be null");

        Optional<HubState> currentState = loadDelegate.load();
        loadDelegate = loadHubStatePort;
        saveDelegate = saveHubStatePort;

        if (loadDelegate.load().isEmpty() && currentState.isPresent()) {
            saveDelegate.save(currentState.orElseThrow());
        }
    }
}
