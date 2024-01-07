package app.components.containers.views.home;

import app.TileManager;
import app.audio.FrostAudio;
import app.audio.indexer.FrostIndexer;
import app.audio.search.FrostSearch;
import app.components.audio.AudioTile;
import app.components.containers.SelectablePanel;
import app.components.containers.ViewManagerPanel;
import app.components.containers.views.ViewPanel;
import app.components.enums.NavigationLink;
import app.components.misc.ViewerStatusLabel;
import app.settings.constraints.ComponentParameters;
import material.containers.MaterialPanel;
import material.containers.MaterialScrollPane;
import material.utils.Log;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

public class ExploreContainer extends ViewPanel {
    private static ExploreContainer instance;
    private static final MaterialPanel explorerContainer = new MaterialPanel(new MigLayout(ComponentParameters.VerticalTileFlow)).setElevationDP(ComponentParameters.VIEWER_ELEVATION_DP);

    private static final SelectablePanel searchResultContainer = (SelectablePanel) new SelectablePanel(new MigLayout(ComponentParameters.VerticalTileFlow)).setElevationDP(null);

    private static final SelectablePanel defaultTileContainer = (SelectablePanel) new SelectablePanel(new MigLayout(ComponentParameters.VerticalTileFlow)).setElevationDP(null);
    private static final GreetingsHeader greetingsHeader = GreetingsHeader.getInstance();
    private static final ViewerStatusLabel viewerStatusLabel = new ViewerStatusLabel(ViewerStatusLabel.SEARCH_TEXT);
    private static boolean isTilesLoaded = false;
    private static final String CONTAINER_CONSTRAINT = "grow";
    private static final String GREETINGS_HEADER_CONSTRAINTS = "north, growX,h 100!, gapY 0";
    private static final String VIEWER_STATUS_CONSTRAINT = "north, growX,h 100!, gapY 0";
    private static final MaterialScrollPane ROOT_SCROLLPANE = new MaterialScrollPane(explorerContainer);
    private Color AVG_IMAGE_COLOR;
    private Image BACKGROUND_IMAGE;
    CompletableFuture<Void> searchTask;
    private final Object lock = new Object();
    private CompletableFuture<Void> loadTask;
    private BufferedImage backgroundImage;
    private ExploreContainer() {
        super(new MigLayout(ComponentParameters.VerticalTileFlow));
        add(ROOT_SCROLLPANE, "w 100%, h 100%");
        explorerContainer.add(greetingsHeader, GREETINGS_HEADER_CONSTRAINTS);
        explorerContainer.add(defaultTileContainer, CONTAINER_CONSTRAINT);
        getUndoButton().setEnabled(false);
        getRedoButton().setEnabled(false);
        loadTiles();


    }

    @Override
    public void updateTheme() {
        super.updateTheme();
//        explorerContainer.setBackgroundImage(getBufferedImage());
//        setBackground(Color.white);
    }

    @Override
    public void addNotify() {
        super.addNotify();
    }

    Thread thread;


    @Override
    public MaterialScrollPane getScrollpane() {
        return ROOT_SCROLLPANE;
    }

    private void loadTiles() {
        if (loadTask != null) {
            loadTask.cancel(true);
            loadTask = null;
        }
        loadTask = CompletableFuture.runAsync(() -> {
            if (!isTilesLoaded) {
                FrostIndexer indexer = FrostIndexer.getInstance();
                indexer.addIndexUpdatedListener(this::createAndAddAllTiles);
                if (indexer.isIndexed())
                    createAndAddAllTiles();
            }
        });
    }

    private void createAndAddAllTiles() {
        defaultTileContainer.removeAll();
        ArrayList<FrostAudio> audios = FrostIndexer.getInstance().getAllAudioFiles();
        if (audios.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                showStatus(ViewerStatusLabel.NO_MUSIC_FOUND, false);
            });
        } else {
            showStatus(ViewerStatusLabel.HIDDEN, true);
            for (FrostAudio audio : audios) {
                AudioTile audioTile = TileManager.convertToTile(audio, NavigationLink.EXPLORE);
                SwingUtilities.invokeLater(() -> {
                    defaultTileContainer.add(audioTile, ComponentParameters.TILE_CONSTRAINT);
                });
            }
        }
        SwingUtilities.invokeLater(() -> {
            defaultTileContainer.repaint();
            defaultTileContainer.revalidate();
            ViewManagerPanel.getInstance().repaint();
        });
        isTilesLoaded = true;
    }

    private Stack<String> searchHistory = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private boolean isSilentSearch = false;

    public void handleSearch(String query) {
        synchronized (lock) {
            if (!isSilentSearch) {
                cancelSearchTask();
                searchTask = CompletableFuture.runAsync(() -> {
                    //Some verification to avoid duplicates
                    String text = ViewManagerPanel.getInstance().getSearchHeader().getSearchBar().getText().strip();
                    if (!searchHistory.isEmpty()) {
                        String lastSearch = searchHistory.get(searchHistory.size() - 1);

                        if (!lastSearch.equals(text))
                            searchHistory.push(text); // Add the search query to the history
                    } else searchHistory.push(text);

                    redoStack.clear();

                    if (text.length() > 0) {
                        // Perform the search and load the results
                        loadSearchResults(TileManager.convertToAudioTiles(FrostSearch.getInstance().searchString(query), NavigationLink.EXPLORE));
                    } else {
                        loadSearchResults(null);
                    }
                    getUndoButton().setEnabled(canUndo());
                    getRedoButton().setEnabled(canRedo());
                });
            }

        }
    }

    private void cancelSearchTask() {
        if (searchTask != null) {
            searchTask.cancel(true);
            searchTask = null;
        }
    }

    public void handleUndo() {
        if (!searchHistory.isEmpty()) {
            String undoneQuery = searchHistory.pop(); // Pop the most recent search query

            redoStack.push(undoneQuery); // Add the undone query to the redo stack

            if (!searchHistory.isEmpty()) {
                // Perform undo operation by loading the previous search results
                String previousQuery = searchHistory.peek();
                performUndoSearch(previousQuery);
            } else {
                performUndoSearch(null); // If there are no previous queries, clear the results
            }
        }
        getUndoButton().setEnabled(canUndo());
        getRedoButton().setEnabled(canRedo());
    }

    public void handleRedo() {
        if (!redoStack.isEmpty()) {
            String redoQuery = redoStack.pop(); // Pop the most recent undone query

            searchHistory.push(redoQuery); // Add the redo query back to the search history

            performRedoSearch(redoQuery);
        }

        getUndoButton().setEnabled(canUndo());
        getRedoButton().setEnabled(canRedo());
    }

    private void performUndoSearch(String query) {
        cancelSearchTask();
        CompletableFuture.runAsync(() -> {
            isSilentSearch = true;
            ViewManagerPanel.getInstance().getSearchHeader().getSearchBar().setText(query);
            if (query != null && !query.equals("")) {
                loadSearchResults(TileManager.convertToAudioTiles(FrostSearch.getInstance().searchString(query), NavigationLink.EXPLORE));
            } else {
                loadSearchResults(null);
            }
            isSilentSearch = false;
        });
    }

    private void performRedoSearch(String query) {
        cancelSearchTask();
        CompletableFuture.runAsync(() -> {
            isSilentSearch = true;
            ViewManagerPanel.getInstance().getSearchHeader().getSearchBar().setText(query);
            if (query != null && !query.equals("")) {
                loadSearchResults(TileManager.convertToAudioTiles(FrostSearch.getInstance().searchString(query), NavigationLink.EXPLORE));
            } else {
                loadSearchResults(null);
            }
            isSilentSearch = false;
        });
    }


    public void loadSearchResults(@Nullable ArrayList<AudioTile> arr) {
        removeSearchResultTiles();
        if (arr == null) {
            loadTilesContainer();
        } else {
            // load search result container
            loadSearchResultContainer();
            addResultToSearchContainer(arr);
            SwingUtilities.invokeLater(searchResultContainer::revalidate);
        }
        boolean isSearching = false;
    }

    private void addResultToSearchContainer(@NotNull ArrayList<AudioTile> arr) {
        if (!arr.isEmpty()) {
            showStatus(ViewerStatusLabel.HIDDEN, true);


            for (AudioTile audioTile : arr) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        searchResultContainer.add(audioTile, ComponentParameters.TILE_CONSTRAINT);
                    } catch (Exception e) {
                        Log.error(e);
                    }
                });
            }
        } else {
            Log.error("No result found!");
            showStatus(ViewerStatusLabel.NO_RESULT_FOUND, false);
        }
    }

    public boolean canUndo() {
        return !searchHistory.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    private void loadTilesContainer() {
        SwingUtilities.invokeLater(() -> {
            searchResultContainer.removeAllSelection();
            explorerContainer.remove(searchResultContainer);

            if (defaultTileContainer.getComponents().length < FrostIndexer.getInstance().getTotalAudioFiles()) {
                ArrayList<AudioTile> arr = TileManager.convertToAudioTiles(FrostIndexer.getInstance().getAllAudioFiles(), NavigationLink.EXPLORE);
                for (AudioTile audioTile : arr) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            defaultTileContainer.add(audioTile, ComponentParameters.TILE_CONSTRAINT);
                        } catch (Exception e) {
                            Log.error(e);
                        }
                    });
                }
            }
            explorerContainer.add(greetingsHeader, GREETINGS_HEADER_CONSTRAINTS);
            explorerContainer.add(defaultTileContainer, CONTAINER_CONSTRAINT);
            explorerContainer.repaint();

            defaultTileContainer.revalidate();
        });
    }

    private void loadSearchResultContainer() {
        SwingUtilities.invokeLater(() -> {
            explorerContainer.remove(greetingsHeader);
            explorerContainer.remove(defaultTileContainer);
            explorerContainer.repaint();
            explorerContainer.revalidate();

            explorerContainer.add(searchResultContainer, CONTAINER_CONSTRAINT);
        });

    }

    public void removeSearchResultTiles() {
        //Remove theme listeners from old results
        showStatus(ViewerStatusLabel.HIDDEN, true);
        SwingUtilities.invokeLater(searchResultContainer::removeAll);
    }

    private static void showStatus(int type, boolean isHidden) {
        viewerStatusLabel.setLabelType(type);
        if (isHidden)
            explorerContainer.remove(viewerStatusLabel);
        else
            explorerContainer.add(viewerStatusLabel, VIEWER_STATUS_CONSTRAINT);
    }

    public MaterialPanel getExplorerTileContainer() {
        return defaultTileContainer;
    }

    public static ExploreContainer getInstance() {
        if (instance == null) instance = new ExploreContainer();
        return instance;
    }



    private static class SearchHistory {
        private static SearchHistory instance;
        private final Stack<String> searchHistory;
        private final Stack<String> redoStack;

        private SearchHistory() {
            searchHistory = new Stack<>();
            redoStack = new Stack<>();
        }

        public void saveHistory(String query) {

            // Add query to search history
            searchHistory.push(query);

            // Clear redo stack after a new search
            redoStack.clear();
        }

        public @Nullable String undoSearch() {
            if (!searchHistory.empty()) {
                // Remove the last search query from history
                String lastQuery = searchHistory.pop();

                // Push the query to redo stack
                redoStack.push(lastQuery);
                return lastQuery;
            }
            return null;
        }

        public String redoSearch() {
            if (!redoStack.empty()) {
                // Pop the last undone query from redo stack
                String redoQuery = redoStack.pop();

                // Perform the redo search
                saveHistory(redoQuery);
                return redoQuery;
            }
            return null;
        }


        public static SearchHistory getInstance() {
            if (instance == null)
                instance = new SearchHistory();
            return instance;
        }
    }
}






