package app.audio.indexer;

import app.audio.Artist;
import app.audio.AudioData;
import app.audio.Folder;
import app.audio.Playlist;
import app.audio.search.SystemSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

public class AudioDataIndexer {
    private static ArrayList<Runnable> indexUpdatedListeners = new ArrayList<>();
    private static final int InitialCapacity = 50;
    private static AudioDataIndexer instance;
    private final ArrayList<String> audioFilePath = new ArrayList<>(InitialCapacity);
    private final ArrayList<AudioData> allAudioFiles = new ArrayList<>(InitialCapacity);
    private final ArrayList<Artist> audioFilesByArtist = new ArrayList<>(InitialCapacity);
    private final ArrayList<Folder> audioFilesByFolder = new ArrayList<>(InitialCapacity);
    private final ArrayList<Playlist> playlists = new ArrayList<>(5);
    private final ArrayList<AudioData> favoriteAudioData = new ArrayList<>(5);
    private static IndexerSortingPolicy indexerSortingPolicy = IndexerSortingPolicy.ASCENDING;
    private boolean isSorted = false;

    /**********************************************
                     ALL AUDIO FILES
     *********************************************/
    public synchronized void indexAndSortAudioFiles() {
        var temp = allAudioFiles.stream().sorted(AudioFileComparators.getAscendingComparator()).toList();
        allAudioFiles.clear();
        allAudioFiles.addAll(temp);
        indexAudioTilesByArtists();
        indexAudioTilesByFolders();
        isSorted = true;
    }

    public final ArrayList<AudioData> getAllAudioFiles() {
        return new ArrayList<>(allAudioFiles);
    }
    public final int getTotalAudioFiles(){
        return allAudioFiles.size();
    }

    public void createAndAddAudioFileAsync(@NotNull AudioData audioData) {
        Thread.startVirtualThread(() -> addAudioFile(audioData));
    }

    public synchronized void addAudioFile(@NotNull AudioData audioData) {
        if(!allAudioFiles.contains(audioData)) {
            allAudioFiles.add(audioData);
            var playlists = audioData.getPlaylists();
            if (playlists != null) {
                for (Playlist playlist : playlists) {
                    if (!this.playlists.contains(playlist)) {
                        addPlaylist(playlist);
                        playlist.add(audioData);
                    }
                }
            }
            if (audioData.isFavorite())
                addAudioFileToFavorites(audioData);
            audioFilePath.add(audioData.getFile().getAbsolutePath());
        }
    }
    /**********************************************
                        ARTISTS
     *********************************************/

    public void indexAudioFileByArtistsAsync() {
        Thread.startVirtualThread(this::indexAudioTilesByArtists);
    }

    public synchronized void indexAudioTilesByArtists() {
        for (AudioData audioData : getAllAudioFiles()) {
            String[] artistNames = audioData.getArtists();
            for(String artistName : artistNames) {
                Artist artist = createOrGetArtist(artistName);
                artist.addAudioData(audioData);
            }
        }
    }

    private @NotNull synchronized Artist createOrGetArtist(String name) {
        name = name.strip();
        for(Artist artist : audioFilesByArtist){
            if(artist.getName().equalsIgnoreCase(name)){
                return artist;
            }
        }
        //If it's a new artist then add it to the array
        audioFilesByArtist.add(new Artist(name));
        return createOrGetArtist(name);
    }
    public @Nullable Artist getArtistByName(String name){
        if(name == null)
            return null;
        name = name.strip();
        for(Artist artist : audioFilesByArtist){
            if(artist.getName().equalsIgnoreCase(name)){
                return artist;
            }
        }
        return null;
    }
    public final ArrayList<Artist> getAllArtists() {
        return audioFilesByArtist;
    }
    /**********************************************
                     FAVORITES
     *********************************************/
    public synchronized void addAudioFileToFavorites(@NotNull AudioData audio) {
        if (!favoriteAudioData.contains(audio))
            favoriteAudioData.add(audio);
    }

    public synchronized void removeAudioFileFromFavorites(@NotNull AudioData audio) {
        favoriteAudioData.remove(audio);
    }
    public ArrayList<AudioData> getAudioFilesByFavorites() {
        return favoriteAudioData;
    }
    /**********************************************
                        PLAYLISTS
     *********************************************/
    public synchronized void removeAudioFileFromPlaylists(AudioData audioData) {
        for (int i = 0; i < playlists.size(); i++) {
            playlists.get(i).remove(audioData);
        }
    }


    public synchronized void addPlaylist(Playlist playlist) {
        if (!playlists.contains(playlist))
            playlists.add(playlist);
    }

    /**********************************************
                        FOLDERS
     *********************************************/
    public void indexAudioFileByFoldersAsync() {
        Thread.startVirtualThread(this::indexAudioTilesByFolders);
    }

    public synchronized void indexAudioTilesByFolders() {
        for (AudioData audioData : getAllAudioFiles()) {
            String folderPath = audioData.getFolderPath();
            Folder folder = createOrGetFolder(folderPath);
            folder.addAudioData(audioData);
        }
    }

    /**
     * Saves the audio to  in memory if that folder already exists otherwise create  a new one
     */
    private synchronized Folder createOrGetFolder(String folderPath){
        String path = folderPath.strip();
        for(Folder folder : audioFilesByFolder){
            if(folder.getPath().equalsIgnoreCase(path)){
                return folder;
            }
        }
        //If it's a new folder then add it to the array
        audioFilesByFolder.add(new Folder(folderPath));
        return createOrGetFolder(folderPath);
    }
    public final ArrayList<Folder> getAllFolders(){
        return audioFilesByFolder;
    }

    /**********************************************
                        UTILITY FUNCTIONS
     *********************************************/
    public static AudioDataIndexer getInstance() {
        if (instance == null)
            instance = new AudioDataIndexer();
        return instance;
    }


    private AudioDataIndexer() {
    }



    public boolean isSorted() {
        return isSorted;
    }

    public synchronized void callIndexUpdated() {
        for (int i = 0; i < indexUpdatedListeners.size(); i++) {
            Runnable r = indexUpdatedListeners.get(i);
            r.run();
        }
    }

    public synchronized void addIndexUpdatedListener(Runnable r) {
            indexUpdatedListeners.add(r);
    }

    public synchronized void removeIndexUpdatedListener(Runnable r) {
        indexUpdatedListeners.remove(r);
        if(indexUpdatedListeners.isEmpty())
            indexUpdatedListeners= new ArrayList<>(0);
    }

//    public void createNewQueue(AudioTile audioTile, NavigationLink link) {
//        switch (link) {
//            case EXPLORE -> {
//                AudioQueue.getInstance().newQueue(audioTile.getAudioFile(), getAllAudioFiles());
//            }
//        }
//    }


    public boolean isIndexed() {
        return isSorted && SystemSearch.getInstance().isSearchOnceComplete();
    }

    public void getAudioFilesByArtist() {
    }

    public boolean isFileLoaded(File file) {
        return isFileLoaded(file.getAbsolutePath());
    }
    public boolean isFileLoaded(String path) {
        return audioFilePath.contains(path);
    }
}
