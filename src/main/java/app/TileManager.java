package app;

import app.audio.Artist;
import app.audio.AudioData;
import app.audio.Folder;
import app.audio.Playlist;
import app.components.audio.ArtistTile;
import app.components.audio.AudioTile;
import app.components.audio.FolderTile;
import app.components.enums.NavigationLink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TileManager {
    private static final List<AudioTile> EMPTY_ARRAY = new ArrayList<>(0);
    private static AudioData lastActiveAudioFile;
    private static final TreeMap<AudioData, List<AudioTile>> TilesMap = new TreeMap<>();
    private static final TreeMap<Playlist, List<AudioTile>> playlistMap = new TreeMap<>();
    private static final TreeMap<String, ArtistTile> artistMap = new TreeMap<>();
    private static final TreeMap<String, FolderTile> folderMap = new TreeMap<>();


    public static @NotNull AudioTile convertToTile(@NotNull AudioData audioData, NavigationLink link) {
        for (AudioTile tile : getMappedTiles(audioData)) {
            if (tile.getLink().equals(link)) {
                return tile;
            }
        }
        AudioTile audioTile = new AudioTile(audioData, link);
        if(audioData.equals(lastActiveAudioFile))
            audioTile.setActive(true);
        mapTile(audioTile);
        return audioTile;
    }

    public static AudioTile convertToPlaylistTile(@NotNull AudioData audio, @NotNull Playlist playlist) {
        for (Playlist playlistOfAudio : audio.getPlaylists()) {
            if (playlistMap.containsKey(playlistOfAudio)) {
                for (AudioTile mappedTile : playlistMap.get(playlistOfAudio)) {
                    if (mappedTile.getAudioData().equals(audio))
                        return mappedTile;
                }
            }
        }
        AudioTile audioTile = new AudioTile(audio, NavigationLink.PLAYLISTS);
        if(audio.equals(lastActiveAudioFile))
            audioTile.setActive(true);

        audioTile.setPlaylist(playlist);
        mapTileInPlaylist(audioTile);

        return audioTile;
    }

    private static void mapTileInPlaylist(@NotNull AudioTile audioTile) {
        if (playlistMap.containsKey(audioTile.getPlaylist())) {
            List<AudioTile> list = playlistMap.get(audioTile.getPlaylist());
            if (!list.contains(audioTile))
                list.add(audioTile);
        } else {
            playlistMap.put(audioTile.getPlaylist(), Collections.synchronizedList(new ArrayList<>()));
            mapTileInPlaylist(audioTile);
        }
    }

    public static @NotNull ArrayList<AudioTile> convertToAudioTiles(@NotNull List<AudioData> audioDataList, @NotNull NavigationLink link) {
        ArrayList<AudioTile> arr = new ArrayList<>(audioDataList.size());
        for (AudioData audioData : audioDataList) {
            boolean isTileAlreadyCreated = false;
            if (TilesMap.containsKey(audioData)) {
                for (AudioTile audioTile : getMappedTiles(audioData)) {
                    if (audioTile.getLink().equals(link)) {
                        isTileAlreadyCreated = true;
                        arr.add(audioTile);
                        break;
                    }
                }
            }
            if (!isTileAlreadyCreated) {
                AudioTile audioTile = new AudioTile(audioData, link);
                if(audioData.equals(lastActiveAudioFile))
                    audioTile.setActive(true);
                mapTile(audioTile);
                arr.add(audioTile);
            }
        }
        return arr;
    }

    public static @NotNull List<AudioTile> getMappedTiles(AudioData audioData) {
        return TilesMap.getOrDefault(audioData, EMPTY_ARRAY);
    }

    public static @NotNull List<AudioTile> getPlaylistAudioTiles(AudioData audioData) {
        return new ArrayList<>(0);
    }

    private static void mapTile(@NotNull AudioTile tile) {
        AudioData audioData = tile.getAudioData();
        if (TilesMap.containsKey(audioData)) {
            List<AudioTile> list = TilesMap.get(audioData);
            if (!list.contains(tile))
                list.add(tile);
        } else {
            TilesMap.put(audioData, Collections.synchronizedList(new ArrayList<>()));
            mapTile(tile);
        }

    }

    public static void removeAudioFile(AudioData audioData) {
        TilesMap.remove(audioData);
    }

    public static void removeAudioTileAsync(AudioData currentAudioData, boolean b) {
        Thread.startVirtualThread(() -> {
            for (AudioTile audioTile : getMappedTiles(currentAudioData)) {
                Container container = audioTile.getParent();
                if (container != null)
                    container.remove(audioTile);
            }
            removeAudioFile(currentAudioData);
        });
    }

    public static @NotNull String getMapDetails() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<AudioData, List<AudioTile>> entry : TilesMap.entrySet()) {
            AudioData audioData = entry.getKey();
            List<AudioTile> audioTiles = entry.getValue();
//            String str = audioData.name().substring(0, 10) + StringUtils.ELLIPSIS;
            sb.append(audioData.getName()).append(":\n\t\t\t").append(audioTiles.size()).append('\n');
        }
        return sb.toString();
    }

    public static void setActiveAudioTiles(@Nullable AudioData audioData) {
        if(lastActiveAudioFile == null || !lastActiveAudioFile.equals(audioData)) {
            List<AudioTile> audioTiles = null;
            if (audioData == null) {
                if (lastActiveAudioFile != null) {
                    audioTiles = findAudioTilesByAudioData(lastActiveAudioFile);
                    for (AudioTile audioTile : audioTiles) {
                        audioTile.setActive(false);
                    }
                }

            } else {
                if (lastActiveAudioFile != null) {
                    audioTiles = findAudioTilesByAudioData(lastActiveAudioFile);
                    for (AudioTile audioTile : audioTiles) {
                        audioTile.setActive(false);
                    }
                    lastActiveAudioFile = null;
                }

                audioTiles = findAudioTilesByAudioData(audioData);
                for (AudioTile audioTile : audioTiles) {
                    audioTile.setActive(true);
                }
            }
            lastActiveAudioFile = audioData;
        }
    }

    public static List<AudioTile> findAudioTilesByAudioData(@NotNull AudioData audioData) {
//        Log.info(TilesMap.keySet().toString());
        var arr1 = getMappedTiles(audioData);
        var arr2 = getPlaylistAudioTiles(audioData);
        ArrayList<AudioTile> arr = new ArrayList<>(arr1.size() + arr2.size());
        arr.addAll(arr1);
        arr.addAll(arr2);
        return arr;
    }

    public static ArtistTile convertToArtistTile(Artist artist) {
         artistMap.putIfAbsent(artist.getName(), new ArtistTile(artist));
         return artistMap.get(artist.getName());
    }
    public static FolderTile convertToFolderTile(Folder folder) {
        folderMap.putIfAbsent(folder.getName(), new FolderTile(folder));
        return folderMap.get(folder.getName());
    }
}
