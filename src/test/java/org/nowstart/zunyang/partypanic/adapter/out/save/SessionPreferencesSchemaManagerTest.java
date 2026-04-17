package org.nowstart.zunyang.partypanic.adapter.out.save;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SessionPreferencesSchemaManagerTest {

    @Test
    void ensure_current_schema_initializes_empty_store() {
        TestPreferences preferences = new TestPreferences();
        SessionPreferencesSchemaManager manager = new SessionPreferencesSchemaManager();

        manager.ensureCurrentSchema(preferences);

        assertEquals(
            SessionPreferencesSchemaManager.CURRENT_SCHEMA_VERSION,
            preferences.getInteger(SessionPreferencesSchemaManager.KEY_SCHEMA_VERSION)
        );
    }

    @Test
    void ensure_current_schema_clears_store_when_version_is_missing() {
        TestPreferences preferences = new TestPreferences();
        SessionPreferencesSchemaManager manager = new SessionPreferencesSchemaManager();
        preferences.putString("session.phase", "PREP_CALL");
        preferences.putInteger("hub.actorX", 2);

        manager.ensureCurrentSchema(preferences);

        assertFalse(preferences.contains("session.phase"));
        assertFalse(preferences.contains("hub.actorX"));
        assertEquals(1, preferences.get().size());
        assertEquals(
            SessionPreferencesSchemaManager.CURRENT_SCHEMA_VERSION,
            preferences.getInteger(SessionPreferencesSchemaManager.KEY_SCHEMA_VERSION)
        );
    }

    @Test
    void ensure_current_schema_keeps_store_when_version_matches() {
        TestPreferences preferences = new TestPreferences();
        SessionPreferencesSchemaManager manager = new SessionPreferencesSchemaManager();
        preferences.putInteger(
            SessionPreferencesSchemaManager.KEY_SCHEMA_VERSION,
            SessionPreferencesSchemaManager.CURRENT_SCHEMA_VERSION
        );
        preferences.putString("session.phase", "PREP_CALL");

        manager.ensureCurrentSchema(preferences);

        assertTrue(preferences.contains("session.phase"));
        assertEquals("PREP_CALL", preferences.getString("session.phase"));
    }
}
