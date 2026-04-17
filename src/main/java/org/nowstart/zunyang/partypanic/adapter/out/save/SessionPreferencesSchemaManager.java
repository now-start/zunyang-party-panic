package org.nowstart.zunyang.partypanic.adapter.out.save;

import com.badlogic.gdx.Preferences;
import java.util.Objects;

public final class SessionPreferencesSchemaManager {

    static final String KEY_SCHEMA_VERSION = "session.schemaVersion";
    static final int CURRENT_SCHEMA_VERSION = 1;

    public void ensureCurrentSchema(Preferences preferences) {
        Objects.requireNonNull(preferences, "preferences must not be null");

        int storedVersion = preferences.getInteger(KEY_SCHEMA_VERSION, -1);
        if (storedVersion == CURRENT_SCHEMA_VERSION) {
            return;
        }

        preferences.clear();
        preferences.putInteger(KEY_SCHEMA_VERSION, CURRENT_SCHEMA_VERSION);
        preferences.flush();
    }
}
