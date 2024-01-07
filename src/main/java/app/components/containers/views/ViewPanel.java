package app.components.containers.views;

import app.components.containers.ViewManagerPanel;
import material.component.MaterialIconButton;
import material.containers.MaterialPanel;
import material.containers.MaterialScrollPane;
import material.utils.filters.FastGaussianBlur;

import java.awt.*;

public abstract class ViewPanel extends MaterialPanel {
    protected static FastGaussianBlur fastGaussianBlur = new FastGaussianBlur(30);


    public ViewPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        setElevationDP(null);
    }

    public ViewPanel(LayoutManager layout) {
        this(layout,true);
    }

    public ViewPanel(boolean isDoubleBuffered) {
        this(null,isDoubleBuffered);
    }

    public ViewPanel() {
        this(null, true);
    }
    public abstract void handleSearch(String query);
    public abstract void handleUndo();
    public abstract void handleRedo();
    public abstract MaterialScrollPane getScrollpane();
    protected MaterialIconButton getUndoButton(){
        return ViewManagerPanel.getInstance().getSearchHeader().getUndoButton();
    }

    protected MaterialIconButton getRedoButton(){
        return ViewManagerPanel.getInstance().getSearchHeader().getRedoButton();
    }
}
