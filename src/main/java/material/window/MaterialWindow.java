package material.window;

import material.component.MaterialComponent;
import material.constants.Size;
import material.containers.MaterialPanel;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.Elevation;
import material.utils.Log;
import material.utils.OsInfo;
import material.window.buttons.CloseButton;
import material.window.buttons.MaxRestoreButton;
import material.window.buttons.MinimizeButton;
import material.window.win32procedures.BorderlessWindowProc;
import material.window.win32procedures.DefaultDecorationParameter;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

// `xprop -name 'Quartz' -format _MOTIF_WM_HINTS 32c -set _MOTIF_WM_HINTS 2` command can be used to remove windows border in x11 linux distros
// `xprop -name 'Quartz' -format _MOTIF_WM_HINTS 32c -set _MOTIF_WM_HINTS '0x2, 0x2, 0x2, 0x2,0x2,0x2'` will remove windows border but won't remove mouse resize
// ProcessBuilder processBuilder = new ProcessBuilder();
//         processBuilder.command("xprop", "-name","" + getName() + "", "-format","_MOTIF_WM_HINTS", "32c", "-set", "_MOTIF_WM_HINTS", "0x2, 0x2, 0x2, 0x2,0x2,0x2"); [Took me 1 hour to figure this shit out]
//TODO Add 6 methods: 3 to add components as close,minimise and maximize buttons and 3 to remove those.
public class MaterialWindow extends JFrame {
    private static final int CAPTION_BUTTON_WIDTH = DefaultDecorationParameter.getIconWidth();
    private MaterialWindowGrip GRIP = MaterialWindowGrip.EXCLUDE_CAPTION_BAR_WIDTH.setGripHeight(DefaultDecorationParameter.getTitleBarHeight());
    // private static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
    private MaterialComponent maxRestoreButton;
    private MaterialComponent minimizeButton;
    private MaterialComponent closeButton;
    private final RootPanel _root = new RootPanel();
    private boolean isDefaultCaptionBarEnabled;
    private boolean isMaximized = false;
    private @Nullable BorderlessWindowProc windowProc;
    private static final String insetsLayoutConstraints = "nogrid, flowy, fill, gap 0,";
    private final MigLayout windowInsetsLayout = new MigLayout(insetsLayoutConstraints + "insets 1");
    private boolean isFullscreen = false;
    private Dimension nonMaximizedSize = null;
    private Point lastNonMaximizedPos = null;
    private boolean wasMaximized = false;
    private final MaterialPanel INSETS_ROOT_PANE = new MaterialPanel(windowInsetsLayout);
    private boolean isMouseOnMaximizeBtn = false;
    private final AtomicBoolean isMouseOnDragArea = new AtomicBoolean(false);

    private final MaterialPanel defaultCaptionBar = new MaterialPanel(new MigLayout("fill"));
    private MaterialPanel currentCaptionBar;
    private int CAPTION_BAR_HEIGHT = 30;


    public MaterialWindow(String name, Size minimumSize, boolean isDefaultCaptionBarEnabled, boolean addWindowProcedure) {
        this.isDefaultCaptionBarEnabled = isDefaultCaptionBarEnabled;
        if (OsInfo.isCustomWindowSupported() && addWindowProcedure) {
            windowProc = new BorderlessWindowProc();
        }
        setProperties();

        // Container for root so that the insets of the whole window can be changed
        INSETS_ROOT_PANE.setElevationDP(Elevation._0);
        //Actual root container
        MigLayout rootLayout = new MigLayout("nogrid, flowy, fill, inset 0, gap 0");
        _root.setLayout(rootLayout);

        INSETS_ROOT_PANE.add(_root, "grow, h 0:100%:100%, w 0:100%:100%");

        this.setName(name);
        this.setTitle(name);
        this.setMinimumSize(minimumSize);
        this.setPreferredSize(minimumSize);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setBackground(ThemeColors.getBackground());
        this.add(INSETS_ROOT_PANE);

        //CaptionBar
        setDefaultCaptionBar(isDefaultCaptionBarEnabled);
        ThemeManager.getInstance().addThemeListener(this::updateTheme);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateDragArea();
            }
        });
        addWindowStateListener(e -> {
            synchronized (this) {
                if (windowProc == null) {
                    setMaximizedFlag(e.getNewState() == MAXIMIZED_BOTH);
                } else
                    setMaximizedFlag(e.getNewState() == MAXIMIZED_BOTH);
            }
        });
    }


    public MaterialWindow(String name, Size minimumSize, boolean addCaptionButtons) {
        this(name, minimumSize, addCaptionButtons, false);
    }

    public MaterialWindow(String name, Size minimumSize) {
        this(name, minimumSize, true, false);

    }

    private void updateDragArea() {

    }

    private void updateTheme() {
        _root.animateBG(ThemeColors.getBackground());
        if (windowProc != null) {
            this.setBackground(ThemeColors.getBackground());
        }
    }


    /* *************************************************************************
     *                              UTILITIES
     ***************************************************************************/
    public synchronized void close() {
        try {
            SwingUtilities.invokeLater(() -> {
                this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                this.dispose();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void minimize() {
        if (windowProc != null) {
            windowProc.minimizeWindow();
        } else {
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_ICONIFIED));
            this.setExtendedState(ICONIFIED);
        }
    }

    public synchronized void toggleMaximize() {
        if (!isMaximized) {
            nonMaximizedSize = this.getSize();
            lastNonMaximizedPos = this.getLocationOnScreen();
        }
        if (windowProc != null) {
            windowProc.toggleMaximize();
        } else {
            if (!isMaximized()) {
                setMaximizedFlag(true);
                this.setExtendedState(MAXIMIZED_BOTH);
            } else {
                setMaximizedFlag(false);
                this.setExtendedState(NORMAL);
            }
        }
    }

    public void restore() {
        this.setExtendedState(this.getExtendedState() | NORMAL);

    }

    /* **************************************************************************
     *                                SETTERS
     ****************************************************************************/

    /* *************************************************************************
     *                              GETTERS
     ***************************************************************************/
    public boolean isDefaultCaptionBarEnabled() {
        return isDefaultCaptionBarEnabled;
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public @NotNull JFrame getJFrame() {
        return this;
    }

    public MaterialPanel getRootPanel() {
        return _root;
    }


    public boolean isMaximized() {
        return isMaximized;
    }

    public MaterialWindowGrip getGRIP() {
        return GRIP;
    }

    public void setGrip(MaterialWindowGrip GRIP) {
        this.GRIP = GRIP;
    }

    private void setMaximizedFlag(boolean b) {
        synchronized (getTreeLock()) {
            this.isMaximized = b;
            if (b) {
                windowInsetsLayout.setLayoutConstraints(insetsLayoutConstraints + "insets " + DefaultDecorationParameter.getResizeBorderThickness());
            } else {
                windowInsetsLayout.setLayoutConstraints(insetsLayoutConstraints + "insets 1");
            }
        }
        INSETS_ROOT_PANE.paintImmediately(0, 0, getWidth(), getHeight());
        INSETS_ROOT_PANE.revalidate();
    }

    public void setFullscreen(boolean isFullscreen) {
        if (!this.isFullscreen && this.isShowing()) {
            if (!isMaximized) {
                nonMaximizedSize = this.getSize();
                lastNonMaximizedPos = this.getLocationOnScreen();
            }
            wasMaximized = isMaximized();
        }
        this.isFullscreen = isFullscreen;
        if (windowProc == null) {
            GraphicsDevice device = this.getGraphicsConfiguration().getDevice();
            if (device.isFullScreenSupported()) {
                if (isFullscreen) {
                    device.setFullScreenWindow(this);
                } else {
                    device.setFullScreenWindow(null);
                }
            } else
                Log.error("Full screen mode not available");
        } else {
            windowProc.setFullscreen(isFullscreen);
            //if fullscreen was exited
            if (!this.isFullscreen && nonMaximizedSize != null && lastNonMaximizedPos != null) {
                //TODO fix this, size should change if last state was not maximized. When fullscreen is exited, it will be in non full screen mode without the toggleMaximize()
                //restore window size and location
                this.setLocation(lastNonMaximizedPos);
                this.setSize(nonMaximizedSize);
                //If window was maximized before entering fullscreen, then exiting a fullscreen will not maximize the window
                //This fixes that
                if (wasMaximized)
                    toggleMaximize();
            }

        }
    }

    @Override
    public void setResizable(boolean resizable) {
        super.setResizable(resizable);
        if (windowProc != null)
            windowProc.setResizable(resizable);
        updateCaptionBarComponents();
//        if (captionBar != null) {
//            if (!resizable)
//                captionBar.remove(1);
//            else if (captionBar.getComponents().length < 3)
//                captionBar.add(new MaxRestoreButton(), CAPTION_BUTTONS_CONSTRAINT, 1);
//            captionBar.revalidate();
//        }
    }


    private void setProperties() {
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("Swing.aatext", "true");

    }


    public void setFont(Font f) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        try {
//            synchronized (getTreeLock()) {
                if (windowProc != null) {
                    if (!windowProc.isInitialized()) {
                        windowProc.init(this);
//                    } else {
//                        windowProc.setVisible(b);

                    }
                }
//            }
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        } catch (Exception e) {
            Log.error(e.getMessage());
        }
        updateTheme();
        updateDragArea();
    }


    /* **************************************************************************
     *                                NATIVES
     ****************************************************************************/
    public boolean isMouseOnClose() {
        return isMouseOnMaximizeBtn;
    }

    public void setMouseOnCloseBtn(boolean b) {
        this.isMouseOnMaximizeBtn = b;
    }

    public boolean isMouseOnMinimize() {
        return isMouseOnMaximizeBtn;
    }

    public void setMouseOnMinBtn(boolean b) {
        this.isMouseOnMaximizeBtn = b;
    }

    public boolean isMouseOnMaximize() {
        return isMouseOnMaximizeBtn;
    }

    public void setMouseOnMaxBtn(boolean b) {
        this.isMouseOnMaximizeBtn = b;
    }

    public boolean isOnDragArea() {
//        Log.info("checking for drag: " + MouseDragArea);
        Point p = MousePointer.getPointerLocation();
//        SwingUtilities.convertPointFromScreen(p,this);
        int x = getX() + GRIP.x;
        int y = getY() + GRIP.y;
        switch (GRIP) {
            case EXCLUDE_CAPTION_BAR_WIDTH -> {
                int GripWidth = getWidth() - currentCaptionBar.getWidth();
                int GripHeight = GRIP.height;
                return p.y >= y && p.y <= y + GripHeight && p.x >= x && p.x <= x + GripWidth;
            }
            case CUSTOM -> {
                return p.y >= y && p.y <= y + GRIP.height && p.x >= x && p.x <= x + GRIP.width;
            }
            default -> {
                return false;
            }
        }
    }

    public synchronized void allowWindowDrag(boolean b) {
        this.isMouseOnDragArea.set(b);
    }


    public void setDefaultCaptionBar(boolean isEnabled) {
        isDefaultCaptionBarEnabled = isEnabled;
        updateCaptionBarComponents();
    }

    public void setCustomCaptionBar(MaterialPanel captionBar) {
        if (!isDefaultCaptionBarEnabled)
            currentCaptionBar = captionBar;
        else
            throw new IllegalStateException("Already using default caption bar. Set it to false to use custom caption bar");
    }
    private void updateCaptionBarComponents() {
        if (isDefaultCaptionBarEnabled) {
            byte btnsAdded = 0;
            if (closeButton == null)
                closeButton = new CloseButton();
            if (minimizeButton == null)
                minimizeButton = new MinimizeButton();
            if (maxRestoreButton == null)
                maxRestoreButton = new MaxRestoreButton();

            defaultCaptionBar.removeAll();
            defaultCaptionBar.add(closeButton, "east,w " + CAPTION_BUTTON_WIDTH + "!");
            if (isResizable()) {
                defaultCaptionBar.add(maxRestoreButton, "east, w " + CAPTION_BUTTON_WIDTH + "!");
                btnsAdded++;
            }
            defaultCaptionBar.add(minimizeButton, "east,w " + CAPTION_BUTTON_WIDTH + "!");
            btnsAdded += 2;
            currentCaptionBar = defaultCaptionBar;

            _root.add(defaultCaptionBar, "north, w " + CAPTION_BUTTON_WIDTH * btnsAdded + "!, align right, h " + CAPTION_BAR_HEIGHT + "!");
        } else {
            _root.remove(defaultCaptionBar);
        }
    }


}
