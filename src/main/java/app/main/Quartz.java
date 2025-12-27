package app.main;

import app.audio.player.QuartzAudioController;
import app.audio.search.SystemSearch;
import app.components.buttons.control.FullScreenButton;
import app.components.containers.FullscreenPanel;
import app.components.containers.MainPanel;
import app.components.containers.PlaybackControlPanel;
import material.animation.MaterialFixedTimer;
import material.constants.Size;
import material.containers.MaterialPanel;
import material.theme.ThemeManager;
import material.utils.Log;
import material.window.MaterialWindow;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//TODO Convert all singletons into normal classes and create a tab manager to manage context switching
// TODO Add meta tag editor
public class Quartz {
    private static Quartz instance;
    //    private static final GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    private static final String NAME = "Quartz";
    public static final Size MIN_SIZE = new Size(640, 640 / 12 * 9);
//    private static final int TITLE_BAR_HEIGHT = 27;

    private static final MaterialPanel GLASS_PANE = new MaterialPanel(new MigLayout("debug,nogrid, flowy, fill, inset 0, gap 0")).setElevationDP(null);

    private static final MaterialWindow WINDOW = new MaterialWindow(NAME, MIN_SIZE, false,true);
    private final static MaterialPanel ROOT = WINDOW.getRootPanel();

    private static final MainPanel MAIN_PANEL = MainPanel.getInstance();
    private static final PlaybackControlPanel PLAYBACK_CONTROL_PANEL = PlaybackControlPanel.getInstance();
    private static final FullscreenPanel FULLSCREEN_PANEL = FullscreenPanel.getInstance();
    private Mode MODE = Mode.DEFAULT;

    private Quartz() {
        EventQueue.invokeLater(() -> {
            WINDOW.setGlassPane(GLASS_PANE);
            WINDOW.setFocusable(true);
            WINDOW.pack();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new QuartzControllerKeyEventDispatcher(WINDOW));
            switchToDefaultMode();
            addListeners();
            if (!ThemeManager.getInstance().isThemingSupported()) {
                Log.warn("Theme detection not available on OS");
            }
        });
    }


    private enum Mode {
        DEFAULT,
        FULLSCREEN
    }


    public void startApplication() {
        EventQueue.invokeLater(() -> {
            QuartzLoaderWindow loaderWindow = new QuartzLoaderWindow(this);
            loaderWindow.setVisible(true);
            SystemSearch.getInstance().forceSearch();
            SystemSearch.getInstance().onSearchComplete(() -> {
                loaderWindow.close();
                showMainWindow();
                loaderWindow.dispose();
            });
        });
    }

    public void showMainWindow() {
        if (!WINDOW.isVisible()) {
            WINDOW.setVisible(true);
            switch (MODE) {
                case FULLSCREEN -> switchToChillMode();
                case DEFAULT -> switchToDefaultMode();
            }
        }
    }


    public void switchToDefaultMode() {
        MODE = Mode.DEFAULT;
        restoreWindow();
        FullScreenButton.getInstance().setActive(false);
        ROOT.removeAll();
        ROOT.add(MAIN_PANEL, "grow");
        ROOT.add(PLAYBACK_CONTROL_PANEL, "south, h 100:100:100, w 100%!");
        ROOT.repaint();
        ROOT.revalidate();
    }

    public void switchToChillMode() {
        MODE = Mode.FULLSCREEN;
        fullscreenWindow();
        ROOT.removeAll();
        ROOT.add(FULLSCREEN_PANEL, "grow");
        ROOT.repaint();
        ROOT.revalidate();
    }


    public void restoreWindow() {
        WINDOW.setFullscreen(false);
    }

    public void fullscreenWindow() {
        WINDOW.setFullscreen(true);
    }


    public void hide() {
        SwingUtilities.invokeLater(() -> {
            if (WINDOW.isVisible())
                WINDOW.setVisible(false);
        });
    }

    public static Quartz getInstance() {
        if (instance == null)
            instance = new Quartz();
        return instance;
    }


    public MaterialWindow getWindow() {
        return WINDOW;
    }

    private void addListeners() {
        WINDOW.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                initializeAudioController();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                disposeEverything();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                Log.warn("Shutting down");
                Runtime.getRuntime().exit(0);
            }
        });
    }

    private void initializeAudioController() {
        QuartzAudioController.getInstance().init();
        QuartzAudioController.getInstance().installPlayerComponents(PLAYBACK_CONTROL_PANEL.getPlayerComponents());
    }

    private void disposeEverything() {
        QuartzAudioController.getInstance().dispose();
        MaterialFixedTimer.disposeAll();
    }

    public static MaterialPanel getGlassPane() {
        return GLASS_PANE;
    }

    public static String getTitle() {
        return NAME;
    }

    public void handleKeyEvent(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> {
                if (MODE == Mode.FULLSCREEN)
                    switchToDefaultMode();
//                System.exit(-1);
            }
            default -> QuartzAudioController.getInstance().handleKeyEvent(e);
        }

    }
}

