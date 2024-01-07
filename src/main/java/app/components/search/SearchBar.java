package app.components.search;

import app.components.Icons;
import app.components.listeners.TextChangedListener;
import app.settings.constraints.ComponentParameters;
import material.MaterialParameters;
import material.component.MaterialIcon;
import material.component.MaterialTextBox;
import material.containers.MaterialPanel;
import material.theme.ThemeManager;
import material.theme.enums.Elevation;
import material.theme.enums.ThemeType;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import static app.settings.constraints.ComponentParameters.SEARCH_PANEL_HEIGHT;

public class SearchBar extends MaterialPanel {
    private static final ArrayList<TextChangedListener> listeners = new ArrayList<>();
    private final int CORNER_RADIUS = MaterialParameters.CORNER_RADIUS;
    private final MaterialTextBox textBox = new MaterialTextBox().setCornerRadius(0);
    private final Color darkThemeBorderColor = new Color(0xFFFFFFF,true);
    private final Color lightThemeBorderColor = new Color(0xF000000, true);
    private boolean isSilent = false;
    public SearchBar() {
        super(new MigLayout("insets 0, fill, nogrid"));
        MaterialIcon searchIcon = new MaterialIcon(Icons.SEARCH);
        searchIcon.setIconSizeRatio(0.8f);

        setMaximumSize(new Dimension(500,SEARCH_PANEL_HEIGHT));
        setElevationDP(ComponentParameters.SEARCH_BOX_ELEVATION);
        add(searchIcon, "w " + SEARCH_PANEL_HEIGHT + "!, grow, gap 0");
        add(textBox, "grow, gap 0");

        textBox.setHint("What would you like to listen today?");
        searchIcon.setPadding(5);
        setCornerRadius(20);

        textBox.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                callTextChanged(textBox.getText());

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                callTextChanged(textBox.getText());

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

    }

    @Override
    public MaterialPanel setElevationDP(@Nullable Elevation elevation) {
        textBox.setElevationDP(elevation);
        return super.setElevationDP(elevation);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        RoundRectangle2D clipRect = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
        RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
        g2d.setClip(clipRect);

        super.paint(g2d);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setClip(null);
        g2d.setColor(ThemeManager.getInstance().getThemeType().equals(ThemeType.Dark) ? darkThemeBorderColor : lightThemeBorderColor);
        g2d.draw(rect);
    }

    private void callTextChanged(String newText) {
        if(!isSilent) {
            for (TextChangedListener l : listeners) {
                l.textChanged(newText);
            }
        }
    }

    public void addTextChangedListener(TextChangedListener l) {
        if (!listeners.contains(l))
            listeners.add(l);
    }

    public void removeTextChangedListener(TextChangedListener l){
        listeners.remove(l);
    }
    public MaterialTextBox getTextBox() {
        return textBox;
    }

    public void setText(String text) {
        isSilent = true;
        textBox.setText(text);
        isSilent = false;
    }

    public String getText() {
        return textBox.getText();
    }
}
