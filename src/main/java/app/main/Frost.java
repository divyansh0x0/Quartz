package app.main;

import app.audio.player.FrostPlayerController;
import app.components.buttons.control.FullScreenButton;
import app.components.containers.FullscreenPanel;
import app.components.containers.MainPanel;
import app.components.containers.PlaybackControlPanel;
import material.constants.Size;
import material.containers.MaterialPanel;
import material.theme.ThemeManager;
import material.utils.Log;
import material.window.MaterialWindow;
import material.window.RootPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// TODO Add meta tag editor
public class Frost {
    private static Frost instance;
    private static final GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    private static final String NAME = "Frost";
    public static final Size MIN_SIZE = new Size(640, 640 / 12 * 9);
    private static final int TITLE_BAR_HEIGHT = 27;

    private static final MaterialPanel GLASS_PANE = new MaterialPanel(new MigLayout("debug,nogrid, flowy, fill, inset 0, gap 0")).setElevationDP(null);

    private static final MaterialWindow WINDOW = new MaterialWindow(NAME, MIN_SIZE,false);
    private final static RootPanel ROOT = WINDOW.getRootPanel();

    private static final MainPanel MAIN_PANEL = MainPanel.getInstance();
    private static final PlaybackControlPanel PLAYBACK_CONTROL_PANEL = PlaybackControlPanel.getInstance();
    private static final FullscreenPanel FULLSCREEN_PANEL = FullscreenPanel.getInstance();
    private Mode MODE = Mode.DEFAULT;


    private enum Mode {
        DEFAULT,
        FULLSCREEN
    }

    private Frost() {
        EventQueue.invokeLater(() -> {
            WINDOW.setGlassPane(GLASS_PANE);
            WINDOW.setFocusable(true);
            WINDOW.pack();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new FrostKeyEventDispatcher(WINDOW));
            switchToDefaultMode();
            addListeners();
            if (!ThemeManager.getInstance().isThemingSupported()) {
                Log.warn("Theme detection not available on OS");
            }
        });
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

    public void show() {
        SwingUtilities.invokeLater(() -> {
            if (!WINDOW.isVisible()) {
                WINDOW.setVisible(true);
                switch (MODE) {
                    case FULLSCREEN -> switchToChillMode();
                    case DEFAULT -> switchToDefaultMode();
                }
            }
        });
    }

    public void hide() {
        SwingUtilities.invokeLater(() -> {
            if (WINDOW.isVisible())
                WINDOW.setVisible(false);
        });
    }

    public static Frost getInstance() {
        if (instance == null)
            instance = new Frost();
        return instance;
    }


    public MaterialWindow getWindow() {
        return WINDOW;
    }

    private void addListeners() {
        WINDOW.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                initializeFrostController();
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

    private void initializeFrostController() {
        FrostPlayerController.getInstance().init();
        FrostPlayerController.getInstance().installPlayerComponents(PLAYBACK_CONTROL_PANEL.getPlayerComponents());
    }

    private void disposeEverything() {
        FrostPlayerController.getInstance().dispose();

    }

    public static MaterialPanel getGlassPane() {
        return GLASS_PANE;
    }

    public static String getTitle() {
        return NAME;
    }

    public void handleKeyEvent(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_ESCAPE -> {
                if(MODE == Mode.FULLSCREEN)
                    switchToDefaultMode();
//                System.exit(-1);
            }
            default -> FrostPlayerController.getInstance().handleKeyEvent(e);
        }

    }
}

