package org.nowstart.zunyang.partypanic.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class GameConfigLoader {
    private static final String RESOURCE_NAME = "game.properties";
    private static final String MODE_KEY = "app.mode";
    private static final String ENV_MODE_KEY = "APP_MODE";

    private GameConfigLoader() {
    }

    public static GameConfig load() {
        Properties properties = new Properties();

        try (InputStream stream = GameConfigLoader.class.getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
            if (stream != null) {
                properties.load(stream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load " + RESOURCE_NAME, exception);
        }

        String modeValue = firstNonBlank(
                System.getProperty(MODE_KEY),
                System.getenv(ENV_MODE_KEY),
                properties.getProperty(MODE_KEY)
        );

        return new GameConfig(AppMode.from(modeValue));
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
