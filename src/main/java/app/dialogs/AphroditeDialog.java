package app.dialogs;

import app.main.Aphrodite;
import app.settings.constraints.ComponentParameters;
import material.component.MaterialLabel;
import material.component.enums.LabelStyle;
import material.constants.Size;
import material.containers.MaterialPanel;
import material.fonts.MaterialFonts;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.utils.OsUtils;
import material.window.MaterialWindow;
import material.window.PopupWindowProc;
import material.window.buttons.CloseButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Enumeration;

import static material.MaterialParameters.CORNER_RADIUS;

public abstract class AphroditeDialog extends JDialog {
    public static final Size MAX_SIZE = new Size(640, 640 / 12 * 9);
    //    private static CustomWindow instance;
//    private final MaterialPanel container = new MaterialPanel().setElevationDP(null);
    private static final MaterialWindow window = Aphrodite.getInstance().getWindow();
    private final DialogRootPanel _root = new DialogRootPanel();
    private PopupWindowProc windowProc;

    public AphroditeDialog(String title, Size minimumSize) {
        super(window.getJFrame());


        if(OsUtils.isWindows7OrLater()){
            windowProc = new PopupWindowProc();
        }

        setFont(MaterialFonts.getInstance().getDefaultFont());
        setProperties();

        CloseButton _close_btn = new CloseButton();
        MaterialLabel _titleLabel = new MaterialLabel().setLabelStyle(LabelStyle.PRIMARY);
        MaterialPanel captionBar = new MaterialPanel(new MigLayout("fill, insets 0 5 0 5"));

        captionBar.setElevationDP(null);
        captionBar.add(_close_btn, ComponentParameters.CAPTION_BUTTONS_CONSTRAINT);
        captionBar.add(_titleLabel, "growY");
        _root.add(captionBar, ComponentParameters.CAPTION_BAR_CONSTRAINT);
//        _root.add(container, "grow, gapY 0");
        _titleLabel.setFontSize(14);
        _titleLabel.setText(title);

        this.setName(title);
        this.setTitle(title);
        this.setMinimumSize(minimumSize);
        this.setPreferredSize(minimumSize);
        this.setMaximumSize(MAX_SIZE);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(window);
        this.setFont(Font.getFont(Font.SERIF));
        this.add(_root);

        ThemeManager.getInstance().addThemeListener(this::updateTheme);
        updateTheme();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if(windowProc == null) {
                    setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS));
                    setLocationRelativeTo(window);
                }
            }
        });
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if(windowProc != null){
            windowProc.init(this);
            windowProc.setVisible(true);
//            windowProc.setGripSize(DecorationParameters.getTitleBarHeight());
        }
    }

    @Override
    public void setMinimumSize(Dimension minimumSize) {
        super.setMinimumSize(minimumSize);
    }

    public synchronized void close() {
        this.dispose();
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

    private void updateTheme() {
        _root.setBackground(ThemeColors.getBackground());
        this.setBackground(ThemeColors.getBackground());
    }

    public DialogRootPanel getRootPanel() {
        return _root;

    }
}