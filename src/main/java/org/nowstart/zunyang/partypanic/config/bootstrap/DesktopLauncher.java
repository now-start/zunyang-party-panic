package org.nowstart.zunyang.partypanic.config.bootstrap;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.nowstart.zunyang.partypanic.adapter.in.runtime.GameViewportConfig;
import org.nowstart.zunyang.partypanic.config.GameConfig;
import org.nowstart.zunyang.partypanic.config.GameConfigLoader;

public final class DesktopLauncher {
    public static void main(String[] args) {
        GameConfig config = GameConfigLoader.load();
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("zunyang-party-panic" + config.windowTitleSuffix());
        configuration.setWindowedMode(GameViewportConfig.WINDOW_WIDTH, GameViewportConfig.WINDOW_HEIGHT);
        configuration.setForegroundFPS(60);
        configuration.useVsync(true);
        new Lwjgl3Application(new PartyPanicGame(config), configuration);
    }
}
