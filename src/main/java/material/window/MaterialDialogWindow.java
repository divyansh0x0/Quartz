package material.window;

import material.component.MaterialComponent;
import material.constants.Size;
import material.containers.MaterialPanel;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.Elevation;
import material.theme.models.ElevationModel;
import material.utils.ComponentMover;
import material.utils.Log;
import material.utils.OsInfo;
import material.window.buttons.CloseButton;
import material.window.buttons.MaxRestoreButton;
import material.window.buttons.MinimizeButton;
import material.window.win32procedures.DialogWindowProc;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

public class MaterialDialogWindow extends JDialog implements ElevationModel {
    protected static final ComponentMover componentMover = new ComponentMover();
    private static final int CAPTION_BUTTON_SIZE = 30;
    private int CAPTION_BAR_HEIGHT = CAPTION_BUTTON_SIZE;
    private MaterialWindowGrip GRIP = MaterialWindowGrip.CUSTOM;
    // private static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
    private MaterialComponent maxRestoreButton;
    private MaterialComponent minimizeButton;
    private MaterialComponent closeButton;
    private final MaterialPanel _root = new RootPanel();
    private boolean isDefaultCaptionBarEnabled;
    private boolean isMaximized = false;
    private @Nullable DialogWindowProc windowProc;
    private static final String insetsLayoutConstraints = "nogrid, flowy, fill, gap 0,";
    private final MigLayout windowInsetsLayout = new MigLayout(insetsLayoutConstraints + "insets 1");
    private boolean isFullscreen = false;
    private final MaterialPanel INSETS_ROOT_PANE = new MaterialPanel(windowInsetsLayout);
    private final AtomicBoolean isMouseOnDragArea = new AtomicBoolean(false);

    private final MaterialPanel defaultCaptionBar = new MaterialPanel(new MigLayout("fill")).setElevationDP(null);
    private MaterialPanel currentCaptionBar;
    private Elevation elevationDP = Elevation._0;


    public MaterialDialogWindow(JFrame parent, String name, Size minimumSize, boolean addCaptionBar, boolean addWindowProcedure) {
        super(parent);
        this.isDefaultCaptionBarEnabled = addCaptionBar;
        if (OsInfo.isCustomWindowSupported() && addWindowProcedure) {
            windowProc = new DialogWindowProc();
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
        setDefaultCaptionBar(addCaptionBar);
        ThemeManager.getInstance().addThemeListener(this::updateTheme);

    }
    public MaterialDialogWindow(String name, Size minimumSize, boolean addCaptionBar, boolean addWindowProc) {
        this(null,name, minimumSize, addCaptionBar, addWindowProc);
    }

    public MaterialDialogWindow(String name, Size minimumSize, boolean addCaptionButtons) {
        this(name, minimumSize, addCaptionButtons, addCaptionButtons);
    }

    public MaterialDialogWindow(String name, Size minimumSize) {
        this(name, minimumSize, false, false);

    }
    public MaterialDialogWindow() {
        this("", Size.ZERO);

    }
    public MaterialDialogWindow(JFrame parent) {
        this(parent,"",Size.ZERO,false,false);

    }
    private void updateDragArea() {

    }

    protected void updateTheme() {
        _root.animateBG(ThemeColors.getColorByElevation(elevationDP));
        if (windowProc != null) {
            this.setBackground(ThemeColors.getColorByElevation(elevationDP));
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

    public MaterialPanel getRootPanel() {
        return _root;
    }


    public boolean isMaximized() {
        return isMaximized;
    }

    public MaterialWindowGrip getGrip() {
        return GRIP;
    }

    public void setGrip(MaterialWindowGrip GRIP) {
        this.GRIP = GRIP;
    }

    @Override
    public void setResizable(boolean resizable) {
        super.setResizable(resizable);
        if (windowProc != null) {
            windowProc.setResizable(resizable);
            updateCaptionBarComponents();
        }
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
            if (windowProc != null) {
                Log.info("Preparing window procedure for material window");
                windowProc.init(this);
                windowProc.setVisible(b);
            }
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

    public boolean isOnDragArea() {
//        Log.info("checking for drag: " + MouseDragArea);
        Point p = MousePointer.getPointerLocation();
//        SwingUtilities.convertPointFromScreen(p,this);
        int gripPosX = getX() + GRIP.x;
        int gripPosY = getY() + GRIP.y;
        switch (GRIP) {
            case EXCLUDE_CAPTION_BAR_WIDTH -> {
                int GripWidth = getWidth() - currentCaptionBar.getWidth();
                int GripHeight = GRIP.height;
                return p.y >= gripPosY && p.y <= gripPosY + GripHeight && p.x >= gripPosX && p.x <= gripPosX + GripWidth;
            }
            case CUSTOM -> {
                return p.y >= gripPosY && p.y <= gripPosY + GRIP.height && p.x >= gripPosX && p.x <= gripPosX + GRIP.width;
            }
            case FULL_WINDOW ->
            {
                return p.y >= getY() && p.y <= getY() + getHeight() && p.x >= getX() && p.x <= getX() + getWidth();
            }
            default-> {
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
    public void setCustomCaptionBar(MaterialPanel captionBar){
        if(!isDefaultCaptionBarEnabled)
            currentCaptionBar = captionBar;
        else
            throw new IllegalStateException("Already using default caption bar. Set it to false to use custom caption bar");
    }
    private void updateCaptionBarComponents() {
        if (isDefaultCaptionBarEnabled) {
            byte addedBtns = 0;
            if(closeButton == null)
                closeButton = new CloseButton();
            if(minimizeButton == null)
                minimizeButton = new MinimizeButton();
            if(maxRestoreButton == null)
                maxRestoreButton = new MaxRestoreButton();

            defaultCaptionBar.removeAll();
            defaultCaptionBar.add(closeButton,"east,w "+ CAPTION_BUTTON_SIZE +"!");
            if(isResizable()) {
                defaultCaptionBar.add(maxRestoreButton, "east, w " + CAPTION_BUTTON_SIZE + "!");
                addedBtns++;
            }
            defaultCaptionBar.add(minimizeButton,"east,w "+CAPTION_BUTTON_SIZE+"!");
            addedBtns += 2;
            currentCaptionBar = defaultCaptionBar;

            _root.add(defaultCaptionBar,"north, align right, w "+CAPTION_BUTTON_SIZE *addedBtns+ "!, h "+CAPTION_BAR_HEIGHT+"!");
        }
        else{
            _root.remove(defaultCaptionBar);
        }
    }
    @Override
    public @Nullable Elevation getElevation() {
        return elevationDP;
    }

    @Override
    public ElevationModel setElevation(@Nullable Elevation elevation) {
        elevationDP = elevation;
        return this;
    }
}