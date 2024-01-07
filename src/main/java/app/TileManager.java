package app;

import app.audio.Artist;
import app.audio.Folder;
import app.audio.FrostAudio;
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
    private static FrostAudio lastActiveAudioFile;
    private static final TreeMap<FrostAudio, List<AudioTile>> TilesMap = new TreeMap<>();
    private static final TreeMap<Playlist, List<AudioTile>> playlistMap = new TreeMap<>();
    private static final TreeMap<String, ArtistTile> artistMap = new TreeMap<>();
    private static final TreeMap<String, FolderTile> folderMap = new TreeMap<>();


    public static @NotNull AudioTile convertToTile(@NotNull FrostAudio frostAudio, NavigationLink link) {
        for (AudioTile tile : getMappedTiles(frostAudio)) {
            if (tile.getLink().equals(link)) {
                return tile;
            }
        }
        AudioTile audioTile = new AudioTile(frostAudio, link);
        if(frostAudio.equals(lastActiveAudioFile))
            audioTile.setActive(true);
        mapTile(audioTile);
        return audioTile;
    }

    public static AudioTile convertToPlaylistTile(@NotNull FrostAudio audio, @NotNull Playlist playlist) {
        for (Playlist playlistOfAudio : audio.getPlaylists()) {
            if (playlistMap.containsKey(playlistOfAudio)) {
                for (AudioTile mappedTile : playlistMap.get(playlistOfAudio)) {
                    if (mappedTile.getFrostAudio().equals(audio))
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

    public static @NotNull ArrayList<AudioTile> convertToAudioTiles(@NotNull List<FrostAudio> frostAudios, @NotNull NavigationLink link) {
        ArrayList<AudioTile> arr = new ArrayList<>(frostAudios.size());
        for (FrostAudio frostAudio : frostAudios) {
            boolean isTileAlreadyCreated = false;
            if (TilesMap.containsKey(frostAudio)) {
                for (AudioTile audioTile : getMappedTiles(frostAudio)) {
                    if (audioTile.getLink().equals(link)) {
                        isTileAlreadyCreated = true;
                        arr.add(audioTile);
                        break;
                    }
                }
            }
            if (!isTileAlreadyCreated) {
                AudioTile audioTile = new AudioTile(frostAudio, link);
                if(frostAudio.equals(lastActiveAudioFile))
                    audioTile.setActive(true);
                mapTile(audioTile);
                arr.add(audioTile);
            }
        }
        return arr;
    }

    public static @NotNull List<AudioTile> getMappedTiles(FrostAudio frostAudio) {
        if (TilesMap.containsKey(frostAudio))
            return TilesMap.get(frostAudio);
        else
            return EMPTY_ARRAY;
    }

    public static @NotNull List<AudioTile> getPlaylistAudioTiles(FrostAudio frostAudio) {
        return new ArrayList<>(0);
    }

    private static void mapTile(@NotNull AudioTile tile) {
        FrostAudio frostAudio = tile.getFrostAudio();
        if (TilesMap.containsKey(frostAudio)) {
            List<AudioTile> list = TilesMap.get(frostAudio);
            if (!list.contains(tile))
                list.add(tile);
        } else {
            TilesMap.put(frostAudio, Collections.synchronizedList(new ArrayList<>()));
            mapTile(tile);
        }

    }

    public static void removeAudioFile(FrostAudio frostAudio) {
        TilesMap.remove(frostAudio);
    }

    public static void removeAudioTileAsync(FrostAudio currentFrostAudio, boolean b) {
        CompletableFuture.runAsync(() -> {
            for (AudioTile audioTile : getMappedTiles(currentFrostAudio)) {
                Container container = audioTile.getParent();
                if (container != null)
                    container.remove(audioTile);
            }
            removeAudioFile(currentFrostAudio);
        });
    }

    public static @NotNull String getMapDetails() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<FrostAudio, List<AudioTile>> entry : TilesMap.entrySet()) {
            FrostAudio frostAudio = entry.getKey();
            List<AudioTile> audioTiles = entry.getValue();
//            String str = frostAudio.name().substring(0, 10) + StringUtils.ELLIPSIS;
            sb.append(frostAudio.getName()).append(":\n\t\t\t").append(audioTiles.size()).append('\n');
        }
        return sb.toString();
    }

    public static void setActiveAudioTiles(@Nullable FrostAudio frostAudio) {
        if(lastActiveAudioFile == null || !lastActiveAudioFile.equals(frostAudio)) {
            List<AudioTile> audioTiles = null;
            if (frostAudio == null) {
                if (lastActiveAudioFile != null) {
                    audioTiles = findAudioTilesByFrostAudio(lastActiveAudioFile);
                    for (AudioTile audioTile : audioTiles) {
                        audioTile.setActive(false);
                    }
                }

            } else {
                if (lastActiveAudioFile != null) {
                    audioTiles = findAudioTilesByFrostAudio(lastActiveAudioFile);
                    for (AudioTile audioTile : audioTiles) {
                        audioTile.setActive(false);
                    }
                    lastActiveAudioFile = null;
                }

                audioTiles = findAudioTilesByFrostAudio(frostAudio);
                for (AudioTile audioTile : audioTiles) {
                    audioTile.setActive(true);
                }
            }
            lastActiveAudioFile = frostAudio;
        }
    }

    public static List<AudioTile> findAudioTilesByFrostAudio(@NotNull FrostAudio frostAudio) {
//        Log.info(TilesMap.keySet().toString());
        var arr1 = getMappedTiles(frostAudio);
        var arr2 = getPlaylistAudioTiles(frostAudio);
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
