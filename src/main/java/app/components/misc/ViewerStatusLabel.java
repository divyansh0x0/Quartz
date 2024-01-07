package app.components.misc;

import app.settings.constraints.ComponentParameters;
import material.component.MaterialLabel;
import material.component.enums.LabelStyle;

import java.awt.*;

public class ViewerStatusLabel extends MaterialLabel {
    public static final int HIDDEN = 0;
    public static final int NO_MUSIC_FOUND = 1;
    public static final int NO_FAVORITES_FOUND = 2;
    public static final int NO_RESULT_FOUND = 3;
    public static final int SEARCH_TEXT = 4;
    public static final int SEARCHING = 5;
    private int labelType = 0;
    public ViewerStatusLabel(int type){
        super("", LabelStyle.PRIMARY, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        setLabelType(type);
        setFontSize(20);
    }

    private Rectangle oldBounds;
    public void setLabelType(int type) {
        labelType = type;
        switch (type){
            case HIDDEN -> {
                setText("");
            }
            case NO_MUSIC_FOUND -> setText(ComponentParameters.NO_MUSIC_FOUND);
            case NO_FAVORITES_FOUND -> setText(ComponentParameters.NO_FAVORITES_FOUND);
            case NO_RESULT_FOUND -> setText(ComponentParameters.NO_RESULT_FOUND_FOR_QUERY);
            case SEARCH_TEXT -> setText(ComponentParameters.SEARCH_TEXT);
            case SEARCHING -> setText(ComponentParameters.SEARCH_ONGOING_TEXT);
            default -> setText("Invalid type: " + type);
        }
        repaint();
        revalidate();
    }

    public int getLabelType() {
        return labelType;
    }
}
