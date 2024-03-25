package material.window;

import material.constants.Size;
import material.fonts.MaterialFonts;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.Elevation;
import material.theme.models.ElevationModel;
import material.utils.Log;
import material.utils.OsInfo;
import material.window.win32procedures.PopupWindowProc;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

public class MaterialPopup extends JFrame implements ElevationModel {
    public static final Size MAX_SIZE = new Size(1920, 1920 / 12 * 9);
    private PopupWindowProc windowProc;
    private Elevation elevation = Elevation._24;

    public MaterialPopup(LayoutManager layoutManager) {
        super();

        if (OsInfo.isCustomWindowSupported())
            windowProc = new PopupWindowProc();
        else
            setUndecorated(true);

//        this.setBackground(ThemeColors.getColorByElevation(ElevationDP._16));
        setFont(MaterialFonts.getInstance().getDefaultFont());
//        getRootPane().setBackground();
        this.setMaximumSize(MAX_SIZE);
        this.setFont(Font.getFont(Font.SERIF));
        this.getContentPane().setLayout(layoutManager);
        this.setType(Type.UTILITY);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        ThemeManager.getInstance().addThemeListener(this::updateTheme);
        updateTheme();
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                SwingUtilities.invokeLater(() -> setVisible(false));
                Log.info("Hiding Popup");
            }
        });
    }

    public MaterialPopup() throws HeadlessException {
        this(new MigLayout("flowY, insets 0, gap 0"));
    }

    @Override
    public void setMinimumSize(Dimension minimumSize) {
        super.setMinimumSize(minimumSize);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (windowProc != null) {
            windowProc.init(this);
            windowProc.setVisible(b);
        }
    }

    public synchronized void close() {
        this.dispose();
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

    private void updateTheme() {
        Color bg = elevation != null ? ThemeColors.getColorByElevation(elevation) : ThemeColors.getBackground();
        this.getRootPane().setBackground(bg);
        this.getContentPane().setBackground(bg);
        this.setBackground(bg);
    }

    public void show(Point location) {
        SwingUtilities.invokeLater(() -> {
            updateTheme();
            this.pack();
            calculatePositionForWindow(location);
            this.setLocation(location);
            this.setVisible(true);
        });
    }

    private void calculatePositionForWindow(Point location) {
        //fix window clipping in y direction
        Log.info(location + " is location of popup");
        int newX = location.x;
        int newY = location.y;
        if (location.x + this.getWidth() > OsInfo.getScreenSize().width) {
            newX = OsInfo.getScreenSize().width - this.getWidth();
        }

        if (location.y + this.getHeight() > OsInfo.getScreenSize().height) {
            newY = OsInfo.getScreenSize().height - this.getHeight();
        }
        location.move(newX, newY);
        Log.info(location + " is corrected location of popup");
    }

    @Override
    public @Nullable Elevation getElevation() {
        return elevation;
    }

    public ElevationModel setElevation(Elevation elevation) {
        this.elevation = elevation;
        updateTheme();
        return null;
    }
}
