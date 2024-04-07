package material.theme;

import material.theme.colors.Dark;
import material.theme.colors.Light;
import material.theme.colors.Themeable;
import material.theme.enums.ThemeType;
import material.tools.ColorUtils;
import material.tools.ImageProcessing;
import material.utils.Log;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ThemeManager {
    private static @Nullable Color DarkerColorOfNoise;
    private static BufferedImage imageWithNoise;
    private static ThemeManager instance;
    private final ArrayList<ThemeListener> _themeListeners = new ArrayList<>();
    private Themeable themeable;
    private static final Themeable[] _themes = {new Dark(), new Light()};
    private ThemeType ACTIVE_THEME_TYPE;
    private boolean isBgTinted;
    Consumer<Boolean> systemThemeListener = this::setDarkMode;

    private ThemeManager() {
//        if (OsThemeDetector.isSupported()) {
//            OsThemeDetector.getDetector().registerListener(systemThemeListener);
            setDarkMode(true);
//        } else
//            setThemeType(ThemeType.Dark);
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            OsThemeDetector.getDetector().removeListener(systemThemeListener);
//        }));
    }

    public boolean isThemingSupported() {
        return false;
    }

    public void setThemeType(ThemeType themeType) {
        if (ACTIVE_THEME_TYPE != null) {
            if (themeType == ThemeType.Dark && ACTIVE_THEME_TYPE != ThemeType.Dark) {
                toDark();
            } else if (themeType == ThemeType.Light && ACTIVE_THEME_TYPE != ThemeType.Light) {
                toLight();
            }
        } else {
            if (themeType == ThemeType.Dark)
                toDark();
            else
                toLight();
        }
    }

    public ThemeType getThemeType() {
        return this.ACTIVE_THEME_TYPE;
    }

    private void toLight() {
        ACTIVE_THEME_TYPE = ThemeType.Light;
        themeable = _themes[1];
        Log.success("Switching to light theme");
        updateTheme();
    }

    private void toDark() {
        ACTIVE_THEME_TYPE = ThemeType.Dark;
        themeable = _themes[0];
        Log.success("Switching to dark theme");
        updateTheme();
    }

    private void updateTheme() {
        if (themeable != null) {
            ThemeColors.setBackground(themeable.getBackgroundColor());
            ThemeColors.setActiveBackgroundColor(themeable.getActiveBackgroundColor());
            ThemeColors.setBackgroundDanger(themeable.getBackgroundColorDanger());
            ThemeColors.setBackgroundSuccess(themeable.getBackgroundColorSuccess());
            ThemeColors.setBackgroundWarn(themeable.getBackgroundColorWarn());

            ThemeColors.setAccent(themeable.getAccentColor());
            ThemeColors.setColorOnAccent(themeable.getColorOnAccent());
            ThemeColors.setTextPrimary(themeable.getTextColorPrimary());
            ThemeColors.setTextSecondary(themeable.getTextColorSecondary());

            ThemeColors.setFocusedBorderColor(themeable.getFocusedBorderColor());

            ThemeColors.setIconButtonColors(themeable.getIconButtonColors());
            ThemeColors.setElevationColors(themeable.getElevationColors());
            ThemeColors.setSelectionColors(themeable.getSelectionColors());
            themeChangedCallback();
        }
    }

    private void setDarkMode(boolean isDark) {
        if (isDark)
            toDark();
        else toLight();
    }

    /**
     * Change the accent color of current theme.
     *
     * @param color New accent color of active theme. 'null' can be used to set the accent color to default accent color
     */
    public synchronized void setForcedAccentColor(@Nullable Color color) {
        themeable.setAccentColor(color, isBgTinted);
        updateTheme();
    }

    public void addThemeListener(ThemeListener listener) {
        _themeListeners.add(listener);
    }

    public void removeThemeListener(ThemeListener listener) {
        _themeListeners.remove(listener);
    }

    private void themeChangedCallback() {
        try {
            for (ThemeListener listener : _themeListeners) {
                listener.ThemeChanged();
            }
        } catch (Exception e) {
            Log.error(e);
        }
    }

    public static ThemeManager getInstance() {
        if (instance == null)
            instance = new ThemeManager();
        return instance;
    }

    /**
     * returns an argb image filled with noise. Dark color of noise is a 10% darker variant of theme's background color while the other color is transparent.
     * Must call this method everytime you need an themed image with noise.
     *
     * @param imageNoiseGenerationCompleted returns the image with noise after it has been generated.
     */
    public void getThemeBasedNoiseImage(ImageNoiseGenerationCompleted imageNoiseGenerationCompleted) {
        //Generating noise image once asynchronously
        if (themeable != null) {
            if (DarkerColorOfNoise != themeable.getBackgroundColor()) {
                Thread.startVirtualThread(() -> {
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    imageWithNoise = ImageProcessing.getNoise(ColorUtils.darken(ThemeColors.getBackground(), 3), ThemeColors.TransparentColor, screenSize.width, screenSize.height);
                    imageNoiseGenerationCompleted.handleNewImage(imageWithNoise);
                    DarkerColorOfNoise = themeable.getBackgroundColor();
                });
            } else {
                imageNoiseGenerationCompleted.handleNewImage(imageWithNoise);
            }
        } else {
            throw new NullPointerException("Theme is null");
        }
    }

    public void enableTintedBackground(boolean isTinted) {
        this.isBgTinted = isTinted;
        themeable.setAccentColor(themeable.getAccentColor(), isTinted);
    }


    public Themeable[] getThemes() {
        return new Themeable[]{_themes[0], _themes[1]};
    }

    public void setDarkModeTheme(Themeable darkTheme) {
        _themes[0] = darkTheme;
        updateTheme();
    }

    public void setLightModeTheme(Themeable lightTheme) {
        _themes[1] = lightTheme;
        updateTheme();
    }

    public interface ImageNoiseGenerationCompleted {
        void handleNewImage(BufferedImage imageWithNoise);
    }
}
