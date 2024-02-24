package material.component;

import material.MaterialParameters;
import material.Padding;
import material.theme.ThemeColors;
import material.theme.enums.Elevation;
import org.kordamp.ikonli.Ikon;

import java.awt.*;

public class MaterialNavButton extends MaterialIconButton {

    private boolean isActive = false;
    private Color MouseOverColor;

    private static final Padding DefaultPadding = new Padding(10, 20);
    Color focusedBorderColor;


    private Elevation elevation = null;

    public MaterialNavButton(Ikon ikon, String text) {
        super(ikon, text);
        setCornerRadius(MaterialParameters.CORNER_RADIUS);
        setPadding(DefaultPadding);
        setIconSizeRatio(0.7);
        setBold(true);
        applyFontStyle();
        setTransparentBackground(true);
        updateTheme();
    }

    public MaterialNavButton(Ikon ikon) {
        this(ikon, "");
    }

    public MaterialNavButton() {
        this(null, "");
    }

    @Override
    public void updateTheme() {
        if (isActive)
            animateFG(ThemeColors.getAccent());
        else {
            animateFG(ThemeColors.getTextPrimary());
            animateBG(getRequiredBackground());
        }
    }

    private Color getRequiredBackground() {
        if (elevation == null) {
            return isTransparentBackground() ? ThemeColors.TransparentColor : ThemeColors.getIconButtonColors().getBackgroundColor();
        } else {
            return ThemeColors.getColorByElevation(elevation);
        }
    }


    public boolean isActive() {
        return isActive;
    }

    public MaterialNavButton setActive(boolean active) {
        isActive = active;
        updateTheme();
        return this;
    }

    public Elevation getElevation() {
        return elevation;
    }

    public MaterialNavButton setElevation(Elevation elevation) {
        this.elevation = elevation;
        repaint();
        return this;
    }


}
