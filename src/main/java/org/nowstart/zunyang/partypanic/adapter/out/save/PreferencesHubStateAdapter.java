package org.nowstart.zunyang.partypanic.adapter.out.save;

import com.badlogic.gdx.Preferences;
import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubLayoutPort;
import org.nowstart.zunyang.partypanic.application.port.out.LoadHubStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveHubStatePort;
import org.nowstart.zunyang.partypanic.domain.chapter.ChapterId;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.common.Position;
import org.nowstart.zunyang.partypanic.domain.hub.HubActor;
import org.nowstart.zunyang.partypanic.domain.hub.HubLayout;
import org.nowstart.zunyang.partypanic.domain.hub.HubState;

public final class PreferencesHubStateAdapter
    implements LoadHubStatePort, SaveHubStatePort {

    private static final String KEY_ACTOR_X = "hub.actorX";
    private static final String KEY_ACTOR_Y = "hub.actorY";
    private static final String KEY_FACING = "hub.facing";
    private static final String KEY_ACTIVE_HOTSPOT = "hub.activeHotspot";
    private static final String KEY_CURRENT_MESSAGE = "hub.currentMessage";

    private final Preferences preferences;
    private final LoadHubLayoutPort loadHubLayoutPort;

    public PreferencesHubStateAdapter(
        Preferences preferences,
        LoadHubLayoutPort loadHubLayoutPort
    ) {
        this.preferences = preferences;
        this.loadHubLayoutPort = loadHubLayoutPort;
    }

    @Override
    public Optional<HubState> load() {
        if (!preferences.contains(KEY_ACTOR_X)
            || !preferences.contains(KEY_ACTOR_Y)
            || !preferences.contains(KEY_FACING)) {
            return Optional.empty();
        }

        HubLayout layout = loadHubLayoutPort.load();
        Position actorPosition = new Position(
            preferences.getInteger(KEY_ACTOR_X),
            preferences.getInteger(KEY_ACTOR_Y)
        );
        if (!layout.isInside(actorPosition)) {
            throw new IllegalStateException("Saved hub actor position is outside layout: " + actorPosition);
        }

        Direction facing = Direction.valueOf(preferences.getString(KEY_FACING, Direction.UP.name()));
        ChapterId activeHotspot = parseChapterId(preferences.getString(KEY_ACTIVE_HOTSPOT, ""));
        String currentMessage = parseOptionalText(preferences.getString(KEY_CURRENT_MESSAGE, ""));

        return Optional.of(new HubState(
            layout,
            new HubActor(actorPosition, facing),
            activeHotspot,
            currentMessage
        ));
    }

    @Override
    public void save(HubState hubState) {
        preferences.putInteger(KEY_ACTOR_X, hubState.actor().position().x());
        preferences.putInteger(KEY_ACTOR_Y, hubState.actor().position().y());
        preferences.putString(KEY_FACING, hubState.actor().facing().name());
        preferences.putString(
            KEY_ACTIVE_HOTSPOT,
            hubState.activeHotspot() == null ? "" : hubState.activeHotspot().name()
        );
        preferences.putString(
            KEY_CURRENT_MESSAGE,
            hubState.currentMessage() == null ? "" : hubState.currentMessage()
        );
        preferences.flush();
    }

    private static ChapterId parseChapterId(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        return ChapterId.valueOf(rawValue.trim());
    }

    private static String parseOptionalText(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        return rawValue;
    }
}
