package org.nowstart.zunyang.partypanic.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.nowstart.zunyang.partypanic.PartyPanicGame;

public final class Lwjgl3Launcher {
    private Lwjgl3Launcher() {
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("zunyang-party-panic");
        configuration.setWindowedMode(1600, 900);
        configuration.setForegroundFPS(60);
        configuration.useVsync(true);
        new Lwjgl3Application(new PartyPanicGame(), configuration);
    }
}
