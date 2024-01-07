package app.settings.constraints;

import app.dialogs.DialogRootPanel;
import material.window.DecorationParameters;
import material.theme.enums.Elevation;

public class ComponentParameters {
    public static final String DIALOG_CONTENT_CONSTRAINT = "alignX center,w (100% - "+(DialogRootPanel.INSETS * 2)+")!, h (100% - " + (DialogRootPanel.INSETS * 2 + DecorationParameters.getTitleBarHeight()) + ")!, gapY 0";
    public static String CAPTION_BAR_CONSTRAINT = "north, gapY 0, h " +(DecorationParameters.getTitleBarHeight())+ "!";
    public static final String CAPTION_BUTTONS_CONSTRAINT = "east, growy, w " + (DecorationParameters.getTitleBarHeight()) + "!";
    public static final String SEARCH_TEXT = "Search by artist, playlist, album, or songs!";
    public static final String NO_MUSIC_FOUND = "No MP3 files found on your PC";
    public static final String NO_FAVORITES_FOUND = "You don't have any favorites";
    public static final String NO_RESULT_FOUND_FOR_QUERY = "No media found for your query :(";
    public static final String SEARCH_ONGOING_TEXT = "Searching...";
    //Elevation
    public static final Elevation VIEWER_ELEVATION_DP = Elevation._1;
    public static final Elevation CONTROL_PANEL_ELEVATION_DP = Elevation._3;
    public static final Elevation SIDE_PANEL_ELEVATION = Elevation._0;
    public static final Elevation SEARCH_BOX_ELEVATION = Elevation._4;

    //WIDTHS AND HEIGHTS
    public static final int SIDE_PANEL_MIN_WIDTH = 140;
    public static final int SIDE_PANEL_MAX_WIDTH = 240;
    public static final int SEARCH_PANEL_HEIGHT = 40;
    public static final int SEARCH_HEADER_HEIGHT = ComponentParameters.SEARCH_PANEL_HEIGHT + 20;

    //Artists Panel
    public static final int AUDIO_HEADER_LIST_HEIGHT = 300;
    public static final int ARTIST_TILE_CONTAINER_HEIGHT = 250;

    //Side panel
    public static final int SIDE_PANEL_PREF_WIDTH = 140;

    //Constraints
    public static final String VerticalTileFlow = "alignY top, nogrid, flowy, fillX, insets 0";
    public static final String PaddedVerticalTileFlow = "alignY top, nogrid, flowy, fillX";
    public static final String NAV_BUTTON_CONSTRAINTS = "gapY 0,growX, h " + NavButtonConstraints.SIZE.getHeight() + "!";

    public static final String TILE_CONSTRAINT = "growx, h 100!, gapY 2";
}
