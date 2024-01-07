package app.components.containers.views;

import material.containers.MaterialScrollPane;
import net.miginfocom.swing.MigLayout;

public class NavigationPanel extends ViewPanel {
    private static final MigLayout layout = new MigLayout("fill, insets 0");
    public NavigationPanel() {
        super(layout);
    }

    @Override
    public void handleSearch(String query) {

    }

    @Override
    public void handleUndo() {

    }

    @Override
    public void handleRedo() {

    }

    @Override
    public MaterialScrollPane getScrollpane() {
        return null;
    }
}
