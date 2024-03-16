package app.components.containers.views.artist;

import app.main.TileManager;
import app.audio.Artist;
import app.audio.indexer.AudioDataIndexer;
import app.components.AudioTilePanel;
import app.components.audio.ArtistListHeader;
import app.components.audio.ArtistTile;
import app.components.audio.AudioListHeader;
import app.components.audio.AudioTile;
import app.components.containers.SelectablePanel;
import app.components.containers.views.ViewPanel;
import app.components.enums.NavigationLink;
import app.components.enums.SortingPolicy;
import material.constants.Size;
import app.settings.constraints.ComponentParameters;
import material.component.MaterialMenuItem;
import material.containers.MaterialPanel;
import material.containers.MaterialScrollPane;
import net.miginfocom.swing.MigLayout;
import material.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ArtistsContainer extends ViewPanel {
    private static ArtistsContainer instance;
    //    private static final int GAP = 5;
//    private static final MigLayout artistsContainerLayout = new MigLayout();
    private static final MaterialPanel SEARCH_RESULT_CONTAINER = new MaterialPanel();
    private static final String tileConstraint = "growX, h 100!";

    private static final MaterialPanel ROOT = new MaterialPanel(new MigLayout(ComponentParameters.PaddedVerticalTileFlow)).setElevationDP(ComponentParameters.VIEWER_ELEVATION_DP);
    private static final SelectablePanel artistTileContainer = (SelectablePanel) new SelectablePanel(new MigLayout(ComponentParameters.PaddedVerticalTileFlow),true).setElevationDP(ComponentParameters.VIEWER_ELEVATION_DP);
    private static final AudioTilePanel audioTileContainer = (AudioTilePanel) new AudioTilePanel(new MigLayout(ComponentParameters.PaddedVerticalTileFlow)).setElevationDP(ComponentParameters.VIEWER_ELEVATION_DP);
    //    private static final MaterialScrollPane AUDIO_TILE_SCROLL_PANE = new MaterialScrollPane(AUDIO_TILE_CONTAINER);

    private static final AudioListHeader audioListHeader = new AudioListHeader(true);
    private static final ArtistListHeader artistListHeader = new ArtistListHeader(false);
    private static final MaterialScrollPane _rootScrollPane = new MaterialScrollPane(ROOT);
    private Point OLD_ARTIST_CONTAINER_VIEW_POS;
    private Artist activeArtist;
    private CompletableFuture<Void> LoadTask;
    private final Size tileSize = new Size(100, 200);

    private static final String CONTAINER_CONSTRAINT = "grow";
    private static CompletableFuture<Void> AsyncAudioTileMapper;

    private boolean IsAudioTilesContainerFilled = false;
    private ArtistsContainer() {
        setLayout(new MigLayout(ComponentParameters.VerticalTileFlow));
        add(_rootScrollPane, "w 100%, h 100%");
        audioListHeader.setBackButtonClickHandler(this::switchToArtistContainer);
        audioListHeader.setComboSelectionChangedListener(this::handleSortingPolicyChange);
        initListHeader();
        switchToArtistContainer();
        loadArtistsWhenReady();
    }

    private void handleSortingPolicyChange(MaterialMenuItem menuItem, String s) {
        SortingPolicy sortingPolicy = AudioListHeader.getSortingPolicy(s);
        audioTileContainer.setSortingPolicy(sortingPolicy);
    }

    @Override
    public void handleSearch(String query) {

    }

    @Override
    public void handleUndo() {
        getRedoButton().setEnabled(true);
        getUndoButton().setEnabled(false);
        switchToArtistContainer();
    }

    @Override
    public void handleRedo() {
        getRedoButton().setEnabled(false);
        getUndoButton().setEnabled(true);
        switchToAudioTileContainer();
    }

    @Override
    public MaterialScrollPane getScrollpane() {
        return _rootScrollPane;
    }

    private void loadArtistsWhenReady() {
        if(LoadTask != null){
            LoadTask.cancel(true);
            LoadTask = null;
        }
        LoadTask = CompletableFuture.runAsync(() -> {
            artistTileContainer.removeAll();
            artistTileContainer.add(artistListHeader, "growX, h " + ComponentParameters.ARTIST_TILE_CONTAINER_HEIGHT + "!");
            AudioDataIndexer indexer = AudioDataIndexer.getInstance();
            AudioDataIndexer.getInstance().addIndexUpdatedListener(this::createAndAddArtistsTiles);
            if (indexer.isIndexed())
                createAndAddArtistsTiles();
        });
    }

    private synchronized void createAndAddArtistsTiles() {
        List<Artist> artists = AudioDataIndexer.getInstance().getAllArtists();
        artistListHeader.setArtists(artists);
        for (Artist artist : artists) {
            ArtistTile artistTile = TileManager.convertToArtistTile(artist);
            artistTile.onClick(this::handleArtistTileClick);
            artistTileContainer.add(artistTile, tileConstraint);
        }
        SwingUtilities.invokeLater(() -> {
            artistTileContainer.repaint();
            artistTileContainer.revalidate();
            repaint();
            revalidate();
            validate();
        });
    }

    private void handleArtistTileClick(Artist artist) {
        activeArtist = artist;
        if (AsyncAudioTileMapper != null) {
            AsyncAudioTileMapper.cancel(true);
            AsyncAudioTileMapper = null;
        }
        audioTileContainer.removeAll();
        audioTileContainer.add(audioListHeader, "growX, h " + ComponentParameters.AUDIO_HEADER_LIST_HEIGHT + "!");

        switchToAudioTileContainer();

        if(LoadTask != null){
            LoadTask.cancel(true);
            LoadTask = null;
        }

        LoadTask = CompletableFuture.runAsync(() -> {
            for (AudioTile audioTile : TileManager.convertToAudioTiles(artist.getAudioDataList(), NavigationLink.ARTIST)) {
                audioTileContainer.add(audioTile, tileConstraint);
//                Log.info(audioTile.getAudioFile().toString());
                if (!IsAudioTilesContainerFilled)
                    IsAudioTilesContainerFilled = true;
                audioTile.revalidate();
            }
            getUndoButton().setEnabled(true);
            getRedoButton().setEnabled(false);
        });
    }

    private synchronized void switchToAudioTileContainer() {
        OLD_ARTIST_CONTAINER_VIEW_POS = _rootScrollPane.getViewport().getViewPosition();

        ROOT.removeAll();
        ROOT.add(audioListHeader, "north,growX, h " + ComponentParameters.AUDIO_HEADER_LIST_HEIGHT + "!");
        ROOT.add(audioTileContainer, CONTAINER_CONSTRAINT);

        artistTileContainer.removeAllSelection();


        initListHeader();
        SwingUtilities.invokeLater(() -> {
            _rootScrollPane.getViewport().setViewPosition(new Point(0, 0));
            audioTileContainer.repaint();
            audioTileContainer.revalidate();
            repaint();
            revalidate();
        });
    }

    private synchronized void switchToArtistContainer() {
        SwingUtilities.invokeLater(() -> {
            ROOT.removeAll();
            audioTileContainer.removeAllSelection();
            ROOT.add(artistTileContainer, CONTAINER_CONSTRAINT);

            if (OLD_ARTIST_CONTAINER_VIEW_POS != null) {
                _rootScrollPane.getViewport().setViewPosition(OLD_ARTIST_CONTAINER_VIEW_POS.getLocation());
                Log.info("value: " + OLD_ARTIST_CONTAINER_VIEW_POS);
            }
            repaint();
            revalidate();
        });

    }

    private void initListHeader() {
        if (activeArtist != null) {
            audioListHeader.setHeading(activeArtist.getName());
            audioListHeader.setSubHeading("ARTIST");
            audioListHeader.setAudioDatas(activeArtist.getAudioDataList());
        }
    }


    public static ArtistsContainer getInstance() {
        if (instance == null)
            instance = new ArtistsContainer();
        return instance;
    }
}
