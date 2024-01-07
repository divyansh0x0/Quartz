package app.components.containers;

import app.components.enums.NavigationLink;
import app.main.Frost;
import app.settings.StartupSettings;
import app.settings.constraints.ComponentParameters;
import material.Padding;
import material.component.MaterialLabel;
import material.component.MaterialNavButton;
import material.component.enums.LabelStyle;
import material.containers.MaterialPanel;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.Elevation;
import material.theme.enums.ThemeType;
import material.tools.ColorUtils;
import material.utils.HorizontalComponentResizer;
import material.utils.OsUtils;
import material.utils.StringUtils;
import material.window.DecorationParameters;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fluentui.FluentUiFilledAL;
import org.kordamp.ikonli.fluentui.FluentUiFilledMZ;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TreeMap;

public class SideNavigationPanel extends MaterialPanel {
    private static final int RESIZE_GRIP_SIZE = 10;
    private static final int FONT_SIZE = 12;
    private boolean drag = false;
    private Point dragLocation = new Point();
    private MaterialNavButton lastActiveBtn;

    private static SideNavigationPanel instance;
    private static final Padding NAV_BTN_PADDING = new Padding(10, 20);
    private static final Cursor resizeCursor = new Cursor(Cursor.E_RESIZE_CURSOR);
    private static final TreeMap<String, MaterialNavButton> LinkMap = new TreeMap<>();
    private static final MaterialLabel titleLabel = new MaterialLabel(Frost.getTitle(), LabelStyle.PRIMARY, MaterialLabel.HorizontalAlignment.CENTER, MaterialLabel.VerticalAlignment.CENTER);
    private static final MaterialLabel menuLabel_1 = new MaterialLabel("MENU", LabelStyle.SECONDARY);

    private static final Ikon[] ikons = {MaterialDesignC.COMPASS, FluentUiFilledMZ.MIC_ON_28, FluentUiFilledMZ.MUSIC_NOTES_24, FluentUiFilledAL.HEART_24, FluentUiFilledAL.FOLDER_48};


    private SideNavigationPanel() {
        super(new MigLayout("nogrid,gapy 0, flowy, insets 5, fillX"));
        setElevationDP(ComponentParameters.SIDE_PANEL_ELEVATION);
        setPreferredSize(new Dimension(ComponentParameters.SIDE_PANEL_PREF_WIDTH, -1));
        setMaximumSize(new Dimension(ComponentParameters.SIDE_PANEL_MAX_WIDTH, -1));
        setMinimumSize(new Dimension(ComponentParameters.SIDE_PANEL_MIN_WIDTH, -1));

        titleLabel.setFontSize(20);
        menuLabel_1.setFontSize(FONT_SIZE - 1);

        String titleLabelConstraint = "north, h " + ComponentParameters.SEARCH_HEADER_HEIGHT + "!, alignX left";
        if (OsUtils.isCustomWindowSupported())
            titleLabelConstraint += ", gapY " + DecorationParameters.getTitleBarHeight();
        add(titleLabel, titleLabelConstraint);
        add(menuLabel_1, "gapX " + NAV_BTN_PADDING.getLeft() + " , gapY 10");
        addButtonsToLayout();

        switchLink(StartupSettings.LAST_NAVIGATION_LINK);

        HorizontalComponentResizer componentResizer = new HorizontalComponentResizer();
        componentResizer.registerComponent(this);
        componentResizer.setDragInsets(new Insets(0,0,0,RESIZE_GRIP_SIZE));
        componentResizer.setMaximumSize(ComponentParameters.SIDE_PANEL_MAX_WIDTH);
        componentResizer.setMinimumSize(ComponentParameters.SIDE_PANEL_MIN_WIDTH);

    }

    @Override
    public void updateTheme() {
        super.updateTheme();
        if(ThemeManager.getInstance().getThemeType().equals(ThemeType.Light)) {
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorUtils.darken(ThemeColors.getColorByElevation(getElevationDP() != null ? getElevationDP() : Elevation._0), 10)));
        }
        else {
            setBorder(null);
        }
    }

    private void addButtonsToLayout() {
        NavigationLink[] values = NavigationLink.values();
        for (int i = 0; i < values.length; i++) {
            NavigationLink link = values[i];
            Ikon icon = ikons[i];

            MaterialNavButton btn = new MaterialNavButton(icon, StringUtils.toHeading(link.name()));

            if (btn.getFont() != null)
                btn.setFont(new Font(btn.getFont().getFontName(), Font.PLAIN, FONT_SIZE));

            btn.addLeftClickListener(e -> {
                switchLink(link);
            });
            btn.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (!getCursor().equals(Cursor.getDefaultCursor()))
                        setCursor(Cursor.getDefaultCursor());
                }
            });
            if (i == 0) {
                String constraint = ComponentParameters.NAV_BUTTON_CONSTRAINTS + ", gapY 10";
                add(btn, constraint);
            } else
                add(btn, ComponentParameters.NAV_BUTTON_CONSTRAINTS);

            LinkMap.put(link.name(), btn);
        }
    }

    public synchronized void switchLink(NavigationLink navigationLink) {
        String linkName = navigationLink.name();
        MaterialNavButton newActiveBtn = LinkMap.get(linkName);
        if (lastActiveBtn != newActiveBtn) {
            SwingUtilities.invokeLater(() -> {
                if (lastActiveBtn != null)
                    lastActiveBtn.setActive(false);
                newActiveBtn.setActive(true);
                lastActiveBtn = newActiveBtn;
            });
        }

        ViewManagerPanel viewer = ViewManagerPanel.getInstance();
        viewer.switchLink(navigationLink);
        viewer.repaint();
        viewer.revalidate();
    }

    public static SideNavigationPanel getInstance() {
        if (instance == null)
            instance = new SideNavigationPanel();
        return instance;
    }

    private boolean isCursorOnResizePadding(Point e) {
        return e.getX() >= getWidth() - RESIZE_GRIP_SIZE && e.getX() <= getWidth();
    }

    @Override
    public void setCursor(Cursor cursor) {
        JFrame jFrame = (JFrame) SwingUtilities.getRoot(this);
        if (jFrame != null)
            jFrame.setCursor(cursor);
        else
            super.setCursor(cursor);
    }
}
