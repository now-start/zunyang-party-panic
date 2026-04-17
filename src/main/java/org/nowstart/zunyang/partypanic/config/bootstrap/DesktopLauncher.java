package org.nowstart.zunyang.partypanic.config.bootstrap;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.nowstart.zunyang.partypanic.config.runtime.GameRuntimeConfig;
import org.nowstart.zunyang.partypanic.config.runtime.GameWindowSpec;
import org.nowstart.zunyang.partypanic.config.wiring.GameModule;

public final class DesktopLauncher {

    private DesktopLauncher() {
    }

    public static void main(String[] args) {
        GameRuntimeConfig runtimeConfig = new GameRuntimeConfig();
        GameWindowSpec windowSpec = runtimeConfig.windowSpec();
        GameModule gameModule = new GameModule();

        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle(windowSpec.title());
        configuration.setWindowedMode(windowSpec.width(), windowSpec.height());
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);

        new Lwjgl3Application(gameModule.createGame(), configuration);
    }
}
