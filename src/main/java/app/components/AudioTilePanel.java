package app.components;

import app.comparators.AudioTileComparator;
import app.components.containers.SelectablePanel;
import app.components.enums.SortingPolicy;
import app.settings.constraints.ComponentParameters;

import java.awt.*;
import java.util.Arrays;

public class AudioTilePanel extends SelectablePanel {
    private SortingPolicy sortingPolicy;

    public AudioTilePanel(LayoutManager layout, boolean isDoubleBuffered, boolean isSelectionAllowed) {
        super(layout, isDoubleBuffered, isSelectionAllowed);
    }

    public AudioTilePanel(LayoutManager layout, boolean isDoubleBuffered) {
        this(layout, isDoubleBuffered,true);
    }

    public AudioTilePanel(LayoutManager layout) {
        this(layout,true);
    }

    public AudioTilePanel(boolean isDoubleBuffered) {
        this(null, isDoubleBuffered);
    }

    public void setSortingPolicy(SortingPolicy sortingPolicy){
        this.sortingPolicy = sortingPolicy;
        sortTiles();
    }

    private void sortTiles() {
        synchronized (getTreeLock()) {
            final Component[] components = getComponents();
            switch (sortingPolicy) {
                case TITLE -> {
                    Arrays.sort(components, AudioTileComparator.TITLE_COMPARATOR);
                }
                case ARTIST -> Arrays.sort(components, AudioTileComparator.ARTIST_COMPARATOR);
                case DURATION -> Arrays.sort(components, AudioTileComparator.DURATION_COMPARATOR);
                case DATE_ADDED -> Arrays.sort(components, AudioTileComparator.DATE_ADDED);
            }
            // Remove existing components from the panel
            removeAll();

            // Re-add the components in the sorted order
            for (Component component : components) {
                add(component, ComponentParameters.TILE_CONSTRAINT);
            }

            // Repaint and revalidate the panel to reflect the changes
            repaint();
            revalidate();
        }
    }
}
