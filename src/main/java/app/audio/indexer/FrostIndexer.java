package app.audio.indexer;

import app.audio.Artist;
import app.audio.Folder;
import app.audio.FrostAudio;
import app.audio.Playlist;
import app.audio.search.SystemSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.CompletableFuture;

public class FrostIndexer {
    private static final ArrayList<Runnable> indexUpdatedListeners = new ArrayList<>(10);
    private static final int InitialCapacity = 500;
    private static FrostIndexer instance;
    private final HashSet<String> audioFilePath = new HashSet<>(InitialCapacity);
    private final ArrayList<FrostAudio> allAudioFiles = new ArrayList<>(InitialCapacity);
    private final ArrayList<Artist> audioFilesByArtist = new ArrayList<>(InitialCapacity);
    private final ArrayList<Folder> audioFilesByFolder = new ArrayList<>(InitialCapacity);
    private final Hashtable<Playlist, ArrayList<FrostAudio>> audioFilesByPlaylist = new Hashtable<>(InitialCapacity);
    private final ArrayList<FrostAudio> favoriteFrostAudios = new ArrayList<>(InitialCapacity);
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

    public final ArrayList<FrostAudio> getAllAudioFiles() {
        return new ArrayList<>(allAudioFiles);
    }
    public final int getTotalAudioFiles(){
        return allAudioFiles.size();
    }

    public void createAndAddAudioFileAsync(@NotNull FrostAudio frostAudio) {
        CompletableFuture.runAsync(() -> addAudioFile(frostAudio));
    }

    public synchronized void addAudioFile(@NotNull FrostAudio frostAudio) {
        if(!allAudioFiles.contains(frostAudio)) {
            allAudioFiles.add(frostAudio);
            var playlists = frostAudio.getPlaylists();
            if (playlists != null) {
                for (Playlist playlist : playlists) {
                    if (!audioFilesByPlaylist.contains(playlist)) {
                        addPlaylist(playlist);
                        addAudioFileToPlaylist(playlist, frostAudio);
                    }
                }
            }
            if (frostAudio.isFavorite())
                addAudioFileToFavorites(frostAudio);
            audioFilePath.add(frostAudio.getFile().getAbsolutePath());
        }
    }
    /**********************************************
                        ARTISTS
     *********************************************/

    public void indexAudioFileByArtistsAsync() {
        CompletableFuture.runAsync(this::indexAudioTilesByArtists);
    }

    public synchronized void indexAudioTilesByArtists() {
        for (FrostAudio frostAudio : getAllAudioFiles()) {
            String[] artistNames = frostAudio.getArtists();
            for(String artistName : artistNames) {
                Artist artist = createOrGetArtist(artistName);
                artist.addFrostAudio(frostAudio);
            }
        }
    }

    private @NotNull Artist createOrGetArtist(String name) {
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
    public synchronized void addAudioFileToFavorites(@NotNull FrostAudio audio) {
        if (!favoriteFrostAudios.contains(audio))
            favoriteFrostAudios.add(audio);
    }

    public synchronized void removeAudioFileFromFavorites(@NotNull FrostAudio audio) {
        favoriteFrostAudios.remove(audio);
    }
    public ArrayList<FrostAudio> getAudioFilesByFavorites() {
        return favoriteFrostAudios;
    }
    /**********************************************
                        PLAYLISTS
     *********************************************/
    public synchronized void removeAudioFileFromPlaylists(FrostAudio frostAudio) {
        audioFilesByPlaylist.forEach((playlist, arr) -> {
            arr.removeIf(el -> el.equals(frostAudio));
        });
    }

    public synchronized void addAudioFileToPlaylist(Playlist playlist, FrostAudio frostAudio) {
        ArrayList<FrostAudio> arr = audioFilesByPlaylist.get(playlist);
        if (arr == null)
            arr = new ArrayList<>();
        arr.add(frostAudio);
        audioFilesByPlaylist.put(playlist, arr);
    }

    public synchronized void addPlaylist(Playlist playlist) {
        if (!audioFilesByPlaylist.contains(playlist))
            audioFilesByPlaylist.put(playlist, new ArrayList<>(InitialCapacity));
    }

    /**********************************************
                        FOLDERS
     *********************************************/
    public void indexAudioFileByFoldersAsync() {
        CompletableFuture.runAsync(this::indexAudioTilesByFolders);
    }

    public synchronized void indexAudioTilesByFolders() {
        for (FrostAudio frostAudio : getAllAudioFiles()) {
            String folderPath = frostAudio.getFolderPath();
            Folder folder = createOrGetFolder(folderPath);
            folder.addFrostAudio(frostAudio);
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
    public static FrostIndexer getInstance() {
        if (instance == null)
            instance = new FrostIndexer();
        return instance;
    }


    private FrostIndexer() {
    }



    public boolean isSorted() {
        return isSorted;
    }

    public void callIndexUpdated() {
        for (Runnable r : indexUpdatedListeners)
            r.run();
    }

    public void addIndexUpdatedListener(Runnable r) {
        if(!indexUpdatedListeners.contains(r))
            indexUpdatedListeners.add(r);
    }

    public void removeIndexUpdatedListener(Runnable r) {
        indexUpdatedListeners.remove(r);
    }

//    public void createNewQueue(AudioTile audioTile, NavigationLink link) {
//        switch (link) {
//            case EXPLORE -> {
//                FrostQueue.getInstance().newQueue(audioTile.getAudioFile(), getAllAudioFiles());
//            }
//        }
//    }


    public boolean isIndexed() {
        return isSorted && SystemSearch.getInstance().isSearchOnceComplete();
    }

    public void getAudioFilesByArtist() {
    }

    public boolean isFileLoaded(File file) {
        return audioFilePath.contains(file.getAbsolutePath());
    }
}
