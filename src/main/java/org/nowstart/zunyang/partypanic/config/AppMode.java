package org.nowstart.zunyang.partypanic.config;

import java.util.Locale;

public enum AppMode {
    TEST,
    LIVE;

    public static AppMode from(String value) {
        if (value == null || value.isBlank()) {
            return TEST;
        }

        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "live", "prod", "production", "release" -> LIVE;
            default -> TEST;
        };
    }

    public boolean showsOperationalUi() {
        return this == TEST;
    }
}
