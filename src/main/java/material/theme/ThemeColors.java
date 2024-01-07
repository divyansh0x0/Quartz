package material.theme;

import material.theme.colors.*;
import material.theme.enums.Elevation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ThemeColors {
    public static final Color TransparentColor = new Color(0x0, true);
    private static Color Background;
    private static Color Accent;
    private static Color ColorOnAccent;

    private static Color TextPrimary;
    private static Color TextSecondary;


    private static Color BackgroundDanger;
    private static Color BackgroundSuccess;
    public static Color BackgroundWarn;
    private static final Themeable defaultTheme = new Dark();
    private static @NotNull material.theme.colors.ElevationColors ElevationColors = defaultTheme.getElevationColors();
    private static @NotNull ButtonColors IconButtonColors = defaultTheme.getIconButtonColors();
    public static Color FocusedBorderColor = defaultTheme.getFocusedBorderColor();
    private static Color ActiveBackgroundColor = defaultTheme.getActiveBackgroundColor();

    private static @NotNull ThemeSelectionColors SelectionColors = defaultTheme.getSelectionColors();
    private ThemeColors() {
        Background = defaultTheme.getBackgroundColor();
        Accent = defaultTheme.getAccentColor();
        ColorOnAccent = defaultTheme.getColorOnAccent();
        TextPrimary = defaultTheme.getTextColorPrimary();
        TextSecondary = defaultTheme.getTextColorSecondary();
        BackgroundDanger = defaultTheme.getBackgroundColorDanger();
        BackgroundSuccess = defaultTheme.getBackgroundColorSuccess();
        BackgroundWarn = defaultTheme.getBackgroundColorWarn();
    }

    @NotNull
    public static Color getBackground() {
        return Background;
    }

    protected static void setBackground(@NotNull Color background) {
        Background = background;
    }

    @NotNull
    public static Color getAccent() {
        return Accent;
    }

    protected static void setAccent(@NotNull Color accent) {
        Accent = accent;
    }

    @NotNull
    public static Color getColorOnAccent() {
        return ColorOnAccent;
    }

    protected static void setColorOnAccent(@NotNull Color colorOnAccent) {
        ColorOnAccent = colorOnAccent;
    }

    @NotNull
    public static Color getTextPrimary() {
        return TextPrimary;
    }

    protected static void setTextPrimary(@NotNull Color textPrimary) {
        TextPrimary = textPrimary;
    }

    @NotNull
    public static Color getTextSecondary() {
        return TextSecondary;
    }

    protected static void setTextSecondary(@NotNull Color textSecondary) {
        TextSecondary = textSecondary;
    }
    public static @NotNull Color getActiveBackgroundColor() {
        return ActiveBackgroundColor;
    }

    protected static void setActiveBackgroundColor(@NotNull Color activeBackgroundColor) {
        ActiveBackgroundColor = activeBackgroundColor;
    }
    @NotNull
    public static Color getBackgroundDanger() {
        return BackgroundDanger;
    }

    protected static void setBackgroundDanger(@NotNull Color backgroundDanger) {
        BackgroundDanger = backgroundDanger;
    }

    @NotNull
    public static Color getBackgroundSuccess() {
        return BackgroundSuccess;
    }

    protected static void setBackgroundSuccess(@NotNull Color backgroundSuccess) {
        BackgroundSuccess = backgroundSuccess;
    }

    @NotNull
    public static Color getBackgroundWarn() {
        return BackgroundWarn;
    }

    protected static void setBackgroundWarn(@NotNull Color backgroundWarn) {
        BackgroundWarn = backgroundWarn;
    }

    @NotNull
    public static Color getFocusedBorderColor() {
        return FocusedBorderColor;
    }

    protected static void setFocusedBorderColor(@NotNull Color focusedBorderColor) {
        FocusedBorderColor = focusedBorderColor;
    }

    @NotNull
    private static material.theme.colors.ElevationColors getElevationColors() {
        return ElevationColors;
    }

    protected static void setElevationColors(@NotNull material.theme.colors.ElevationColors elevationColors) {
        ElevationColors = elevationColors;
    }

    @NotNull
    public static Color getColorByElevation(@NotNull Elevation elevation) {
        material.theme.colors.ElevationColors el = getElevationColors();
        switch (elevation) {
            case _0 -> {
                return el.getDP_0();
            }
            case _1 -> {
                return el.getDP_1();
            }
            case _2 -> {
                return el.getDP_2();
            }
            case _3 -> {
                return el.getDP_3();
            }
            case _4 -> {
                return el.getDP_4();
            }
            case _6 -> {
                return el.getDP_6();
            }
            case _8 -> {
                return el.getDP_8();
            }
            case _12 -> {
                return el.getDP_12();
            }
            case _16 -> {
                return el.getDP_16();
            }
            case _24 -> {
                return el.getDP_24();
            }
        }
        return el.getDP_24();
    }

    public static @NotNull ButtonColors getIconButtonColors() {
        return IconButtonColors;
    }

    protected static void setIconButtonColors(@NotNull ButtonColors iconButtonColors) {
        IconButtonColors = iconButtonColors;
    }

    public static @NotNull ThemeSelectionColors getSelectionColors() {
        return SelectionColors;
    }

    protected static void setSelectionColors(@NotNull ThemeSelectionColors selectionColors) {
        SelectionColors = selectionColors;
    }

}
