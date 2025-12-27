package app.main;

import app.audio.search.SystemSearch;
import app.settings.StartupSettings;
import material.animation.MaterialFixedTimer;
import material.theme.ThemeManager;
import material.utils.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.LogManager;

public class MainClass {
    public static void main(String[] args) {
        startApp();
        if (!ThemeManager.getInstance().isThemingSupported())
            ThemeManager.getInstance().setThemeType(StartupSettings.DEFAULT_THEME_TYPE);
        ThemeManager.getInstance().enableTintedBackground(StartupSettings.DYNAMIC_BACKGROUND_THEMING_ENABLED);
    }

    public static synchronized void startApp() {
        Quartz.getInstance().startApplication();
    }

    private static MaterialFixedTimer gcCaller;
}
