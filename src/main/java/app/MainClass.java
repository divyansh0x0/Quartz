package app;

import app.audio.indexer.FrostIndexer;
import app.audio.search.SystemSearch;
import app.loader.FrostLoader;
import app.main.Frost;
import app.settings.StartupSettings;
import material.animation.MaterialFixedTimer;
import material.theme.ThemeManager;
import material.utils.Log;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.LogManager;

public class MainClass {
    public static void main(String[] args) {
        startApp();
//        testStartApp();

        disableJAudioTaggerLogs();
//        redirectTerminalOutput();
        if (args.length > 0 && args[0].equals("-gc:true"))
            callGCPeriodically();
        disableJAudioTaggerLogs();
        if (!ThemeManager.getInstance().isThemingSupported())
            ThemeManager.getInstance().setThemeType(StartupSettings.DEFAULT_THEME_TYPE);
        ThemeManager.getInstance().enableTintedBackground(StartupSettings.DYNAMIC_BACKGROUND_THEMING_ENABLED);
    }

    private static void testStartApp() {
        Frost.getInstance().show();
//        SystemSearch.getInstance().forceSearch();
    }

    public static void disableJAudioTaggerLogs() {
        LogManager logManager = LogManager.getLogManager();
        String properties = """
                handlers=java.util.logging.ConsoleHandler
                org.jaudiotagger.level=OFF
                """;
        try {
            logManager.readConfiguration(new ByteArrayInputStream(properties.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException ignored) {
        }
    }

    private static void redirectTerminalOutput() {
        try {
            String fileName = "terminalOutput.txt";
            File file = new File(fileName);
            if (file.exists() || file.createNewFile()) {
                System.setOut(new PrintStream((fileName)));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void startApp() {
        try {
            FrostLoader.getInstance().show();
            Log.info("Preparing system search");
            FrostIndexer.getInstance().addIndexUpdatedListener(MainClass::hideLoaderAndLoadApp);
            SystemSearch.getInstance().forceSearch();
            if (FrostIndexer.getInstance().isIndexed()) {
                hideLoaderAndLoadApp();
            }
        } catch (Exception e) {
            Log.error(e);
            e.printStackTrace();
        }
    }

    private static void hideLoaderAndLoadApp() {
        Log.info("Hiding frost loader");
        SwingUtilities.invokeLater(() -> {
            Frost.getInstance().show();
            Log.success("Frost player opened");
        });
        FrostLoader.getInstance().hide();
    }

    public static void noLoaderSearch() {
        SystemSearch.getInstance().onSearchComplete(() -> {
            Log.info("Hiding frost loader");

            SwingUtilities.invokeLater(() -> {
//                Frost.getInstance().show();

                Log.success("Frost player opened");
            });
            FrostLoader.getInstance().hide();
        });
        SystemSearch.getInstance().forceSearch();
    }

    private static MaterialFixedTimer gcCaller;

    private static void callGCPeriodically() {
        Log.warn("Periodic GC call is on");
        if (gcCaller != null) {
            gcCaller.stop();
            gcCaller = null;
        }
        gcCaller = new MaterialFixedTimer(5000) {
            @Override
            public void tick(float deltaMillis) {
                System.gc();
            }
        };
        gcCaller.start();
    }
}
