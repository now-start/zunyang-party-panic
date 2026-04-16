package org.nowstart.zunyang.partypanic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.nowstart.zunyang.partypanic.screen.PartyPanicScreen;

public final class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("zunyang-party-panic");
        configuration.setWindowedMode(1600, 900);
        configuration.setForegroundFPS(60);
        configuration.useVsync(true);
        new Lwjgl3Application(new Game() {
            @Override
            public void create() {
                setScreen(new PartyPanicScreen());
            }
        }, configuration);
    }
}
