package org.nowstart.zunyang.partypanic.config;

import java.util.Objects;

public final class GameConfig {
    private final AppMode mode;

    public GameConfig(AppMode mode) {
        this.mode = Objects.requireNonNullElse(mode, AppMode.TEST);
    }

    public AppMode mode() {
        return mode;
    }

    public boolean isLiveMode() {
        return mode == AppMode.LIVE;
    }

    public boolean showsOperationalUi() {
        return mode.showsOperationalUi();
    }

    public String windowTitleSuffix() {
        return isLiveMode() ? "" : " [TEST]";
    }
}
