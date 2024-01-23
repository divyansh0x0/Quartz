package app.settings;

import app.components.enums.ViewType;
import app.components.enums.NavigationLink;
import app.components.spectrum.SpectrumType;
import material.theme.enums.ThemeType;

public class StartupSettings {
    public static final long MINIMUM_FILE_SIZE = 500_000;//500 kiloBytes
    public static final ThemeType DEFAULT_THEME_TYPE = ThemeType.Light;
    public static ViewType DEFAULT_MAIN_PANEL_ACTIVE_VIEW = ViewType.DefaultView;
    public static NavigationLink LAST_NAVIGATION_LINK = NavigationLink.EXPLORE;
    public static SpectrumType SPECTRUM_TYPE = SpectrumType.WAVY;

    public static int SPECTRUM_BANDS_NUM = 128;

    public static int SEARCH_DEPTH = 16;
    public static int SPECTRUM_THRESHOLD = -90;

    public static boolean DYNAMIC_THEMING_ENABLED = true;
    public static boolean DYNAMIC_BACKGROUND_THEMING_ENABLED = false;

}