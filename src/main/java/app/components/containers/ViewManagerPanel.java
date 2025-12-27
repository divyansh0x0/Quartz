package app.components.containers;

import app.components.containers.views.FavoritesContainer;
import app.components.containers.views.ViewPanel;
import app.components.containers.views.artist.ArtistsContainer;
import app.components.containers.views.folders.FoldersContainer;
import app.components.containers.views.home.ExploreContainer;
import app.components.enums.NavigationLink;
import app.components.search.SearchHeader;
import app.dialogs.DialogFactory;
import app.settings.StartupSettings;
import app.settings.constraints.ComponentParameters;
import material.containers.MaterialPanel;
import material.theme.enums.Elevation;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.util.Objects;

public class ViewManagerPanel extends MaterialPanel {
    private static ViewManagerPanel instance;
    private static final Elevation ELEVATION = ComponentParameters.VIEWER_ELEVATION_DP;
    private final SearchHeader SEARCH_HEADER = new SearchHeader();
    private ViewPanel activePanel;
    private final ScrollbarValueChangeListener scrollbarValueChangeListener = new ScrollbarValueChangeListener();
    private NavigationLink activeLink = StartupSettings.LAST_NAVIGATION_LINK;
    private static final String[] CachedSearchText = new String[NavigationLink.values().length];
    private static final byte[][] CachedUndoRedo = new byte[NavigationLink.values().length][2]; //{ index, {UNDO_FLAG, REDO_FLAG}}

    private ViewManagerPanel() {
        setLayout(new MigLayout("fill, nogrid, inset 0"));
        setElevationDP(ELEVATION);

        add(SEARCH_HEADER, "north, growX, h " + ComponentParameters.SEARCH_HEADER_HEIGHT + "!");

//        MainPanel.CAPTION_BAR.setBackgroundNoiseEnabled(true, 0.75f);

        SEARCH_HEADER.getUndoButton().addLeftClickListener(e -> {
            if (activePanel != null)
                activePanel.handleUndo();
        });
        SEARCH_HEADER.getRedoButton().addLeftClickListener(e -> {
            if (activePanel != null)
                activePanel.handleRedo();
        });

    }

    private void cacheAndResetUndoRedoButtonsState(){
        CachedUndoRedo[activeLink.ordinal()][0] = SEARCH_HEADER.getUndoButton().isEnabled() ? (byte) 1 : 0;
        CachedUndoRedo[activeLink.ordinal()][1] = SEARCH_HEADER.getRedoButton().isEnabled() ? (byte) 1 : 0;
        SEARCH_HEADER.getUndoButton().setEnabled(false);
        SEARCH_HEADER.getRedoButton().setEnabled(false);
    }
    private void loadCachedUndoRedoButtonsState(){
        byte[] status= CachedUndoRedo[activeLink.ordinal()];
        SEARCH_HEADER.getUndoButton().setEnabled(status[0] != 0);
        SEARCH_HEADER.getRedoButton().setEnabled(status[1] != 0);
    }

    private void cacheAndEmptySearchText() {
        String txt = SEARCH_HEADER.getSearchBar().getTextBox().getText();
        CachedSearchText[activeLink.ordinal()] = txt;
        SEARCH_HEADER.getSearchBar().getTextBox().setText("");
    }


    private void loadCachedText() {
        SEARCH_HEADER.getSearchBar().getTextBox().setText(Objects.requireNonNullElse(CachedSearchText[activeLink.ordinal()], ""));
    }

//    private void addScrollListener() {
//        for (Component component : getComponents()) {
//            if (component instanceof ViewPanel viewPanel) {
//                JScrollPane scrollPane = viewPanel.getScrollpane();
//                if (scrollPane != null) {
//                    JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
//                    scrollBar.addAdjustmentListener(scrollbarValueChangeListener);
//
//                    //Pre check scrollbar value
//                    if (scrollBar.getValue() > 10) {
//                        SEARCH_HEADER.setElevationDP(SEARCH_HEADER.getScrollingElevation());
//                    } else {
//                        SEARCH_HEADER.setElevationDP(SEARCH_HEADER.getDefaultElevation());
//                    }
//                }
//
////                SwingUtilities.invokeLater(()->{
////                    if(MainPanel.getInstance().getCurrentView() == ViewType.DefaultView)
////                        MainPanel.getInstance().getCaptionBar().setElevationDP(SEARCH_HEADER.getElevationDP());
////                });
//
//                return;
//            }
//        }
//    }

    public void switchLink(NavigationLink navigationLink) {
        if (navigationLink != null) {
            switch (navigationLink) {
                case ARTIST -> switchToArtistView();
                case PLAYLISTS -> switchToPlaylistView();
                case FAVORITES -> switchToFavoritesView();
                case EXPLORE -> switchToExplorerView();
                case FOLDERS -> switchToFoldersView();
            }
        }
    }

    public synchronized void switchToExplorerView() {
        activeLink = NavigationLink.EXPLORE;
        activePanel = ExploreContainer.getInstance();
        viewChangeCallback();
    }

    public synchronized void switchToFavoritesView() {
        activeLink = NavigationLink.FAVORITES;
        activePanel = FavoritesContainer.getInstance();
        viewChangeCallback();

    }

    public synchronized void switchToArtistView() {
        activeLink = NavigationLink.ARTIST;
        activePanel = ArtistsContainer.getInstance();
        viewChangeCallback();

    }
    public synchronized void switchToFoldersView() {
        activeLink = NavigationLink.FOLDERS;
        activePanel = FoldersContainer.getInstance();
        viewChangeCallback();

    }

    public synchronized void switchToPlaylistView() {
        activeLink = NavigationLink.PLAYLISTS;
        DialogFactory.showErrorDialog("Playlists do not work");
//        activePanel = Playl.getInstance();
//        viewChangeCallback();
    }

    public SearchHeader getSearchHeader() {
        return SEARCH_HEADER;
    }

    public static ViewManagerPanel getInstance() {
        if (instance == null)
            instance = new ViewManagerPanel();
        return instance;
    }

    private void viewChangeCallback() {
        clear();

        add(activePanel, "grow");
        loadCachedText();
        loadCachedUndoRedoButtonsState();
        SEARCH_HEADER.setSearchListener(activePanel::handleSearch);
        SEARCH_HEADER.getSearchBar().getTextBox().setHint(getAppropriateHint(activeLink));
        SEARCH_HEADER.setVisible(activeLink.equals(NavigationLink.EXPLORE));
        SEARCH_HEADER.getSearchBar().getTextBox().setHint(getAppropriateHint(activeLink));
    }
    public synchronized void clear() {
        cacheAndEmptySearchText();
        cacheAndResetUndoRedoButtonsState();
        SEARCH_HEADER.setSearchListener(null);
        removeAll();
        if(activeLink.equals(NavigationLink.EXPLORE))
            add(SEARCH_HEADER, "north, growX, h " + ComponentParameters.SEARCH_HEADER_HEIGHT + "!");
    }
    private String getAppropriateHint(NavigationLink activeLink) {
        switch (activeLink){
            case EXPLORE -> {
                return "What would you like to listen today?";
            }
            case ARTIST -> {
                return "Search for the artist whom you are looking for!";
            }
            case FAVORITES -> {
                return "Search among your favorites!";
            }
            case PLAYLISTS -> {
                return "Search for the playlist that you are looking for!";
            }
        }

        return "";
    }




    public NavigationLink getActiveLink() {
        return activeLink;
    }


    @Override
    public void addNotify() {
        SideNavigationPanel.getInstance().switchLink(activeLink);
        super.addNotify();
    }



    private class ScrollbarValueChangeListener implements java.awt.event.AdjustmentListener {
        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (e.getValue() > 10) {
                SEARCH_HEADER.setElevationDP(SEARCH_HEADER.getScrollingElevation());
            } else {
                SEARCH_HEADER.setElevationDP(SEARCH_HEADER.getDefaultElevation());
            }
        }
    }
}
