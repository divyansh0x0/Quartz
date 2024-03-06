package app.components.containers.views.folders;

import app.TileManager;
import app.audio.Folder;
import app.audio.indexer.AudioDataIndexer;
import app.audio.search.SystemSearch;
import app.components.containers.SelectablePanel;
import app.components.containers.views.ViewPanel;
import app.components.misc.ViewerStatusLabel;
import app.settings.constraints.ComponentParameters;
import material.containers.MaterialPanel;
import material.containers.MaterialScrollPane;
import material.utils.Log;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class FoldersContainer extends ViewPanel {
    private static final String tileConstraint = "growX, h 100!";

    private static final MigLayout containerLayout = new MigLayout("nogrid, flowy, fillX, insets 0,gap 0 0 0 0");
    private static FoldersContainer instance;
    private final ViewerStatusLabel viewerStatusLabel = new ViewerStatusLabel(0);
    private static final MaterialPanel ROOT = new MaterialPanel(new MigLayout(ComponentParameters.PaddedVerticalTileFlow)).setElevationDP(ComponentParameters.VIEWER_ELEVATION_DP);
    private static final MaterialPanel folderTilesContainer = new SelectablePanel(new MigLayout(ComponentParameters.PaddedVerticalTileFlow),true).setElevationDP(ComponentParameters.VIEWER_ELEVATION_DP);
    private static final MaterialScrollPane _rootScrollPane = new MaterialScrollPane(ROOT);
    private static final String CONTAINER_CONSTRAINT = "grow";
    private Thread LoadTask;
    public FoldersContainer() {
        super(containerLayout);
        add(_rootScrollPane,"w 100%,h 100%");

        switchToFoldersContainer();

        if(SystemSearch.getInstance().isSearchOnceComplete()){
            this.loadAllFolders();
        }
        else
            SystemSearch.getInstance().onSearchComplete(this::loadAllFolders);
//        this.add(label,"h 100!,south,grow");
    }

    private void switchToFoldersContainer() {
        ROOT.add(folderTilesContainer, CONTAINER_CONSTRAINT);

    }

    private void loadAllFolders(){
        if(LoadTask != null){
            synchronized (LoadTask) {
                LoadTask.interrupt();
                LoadTask = null;
            }
        }
        LoadTask = Thread.startVirtualThread(() -> {
            folderTilesContainer.removeAll();
//            folderTilesContainer.add(artistListHeader, "growX, h " + ComponentConstraints.ARTIST_TILE_CONTAINER_HEIGHT + "!");
            AudioDataIndexer indexer = AudioDataIndexer.getInstance();
            AudioDataIndexer.getInstance().addIndexUpdatedListener(this::createAndAddArtistsTiles);
            if (indexer.isIndexed())
                createAndAddArtistsTiles();
        });
    }
    private synchronized void createAndAddArtistsTiles() {
        List<Folder> folders = AudioDataIndexer.getInstance().getAllFolders();
        for (Folder folder : folders) {

            Log.info("Adding folder tile: " + folder);
            folderTilesContainer.add(TileManager.convertToFolderTile(folder), tileConstraint);

        }
        SwingUtilities.invokeLater(() -> {
            folderTilesContainer.repaint();
            folderTilesContainer.revalidate();
            repaint();
        });
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

    public static FoldersContainer getInstance(){
        if(instance == null)
            instance = new FoldersContainer();
        return instance;
    }
}
