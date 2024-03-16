package app.components.containers.views;

import app.main.TileManager;
import app.audio.indexer.AudioDataIndexer;
import app.audio.search.SystemSearch;
import app.components.audio.AudioTile;
import app.components.enums.NavigationLink;
import app.components.misc.ViewerStatusLabel;
import material.containers.MaterialScrollPane;
import net.miginfocom.swing.MigLayout;

public class FavoritesContainer extends ViewPanel {
private static FavoritesContainer instance;
    private static final MigLayout containerLayout = new MigLayout("nogrid, flowy, fillX, insets 0,gap 0 0 0 0");

    private final ViewerStatusLabel viewerStatusLabel = new ViewerStatusLabel(0);
    private static final String AUDIO_TILE_CONSTRAINTS = "growx, h 100:100:100";

    private FavoritesContainer(){
        super(containerLayout);
        add(viewerStatusLabel,"center, grow");
        if(SystemSearch.getInstance().isSearchOnceComplete()){
            this.loadTiles();
        }
        else
            SystemSearch.getInstance().onSearchComplete(this::loadTiles);
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

    private void loadTiles(){
        AudioDataIndexer audioDataIndexer = AudioDataIndexer.getInstance();
        if(audioDataIndexer.getAudioFilesByFavorites().size() == 0){
            viewerStatusLabel.setLabelType(ViewerStatusLabel.NO_FAVORITES_FOUND);
        }
        else{
            audioDataIndexer.getAudioFilesByFavorites().forEach(audioFile->{
                AudioTile audioTile = TileManager.convertToTile(audioFile, NavigationLink.FAVORITES);
                this.add(audioTile, AUDIO_TILE_CONSTRAINTS);
            });
        }
    }

    public static FavoritesContainer getInstance() {
        if(instance == null)
            instance = new FavoritesContainer();
        return instance;
    }
}
