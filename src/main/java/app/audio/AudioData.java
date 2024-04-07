package app.audio;

import app.audio.indexer.ArtworkManager;
import app.settings.StartupSettings;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jetbrains.annotations.NotNull;
import material.utils.Log;
import material.utils.StringUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * AudioData class used for storing and getting necessary data used by app.main.Quartz Project
 */


public final class AudioData implements Serializable, Comparable<AudioData> {
    private @NotNull File file;
    private @NotNull String name;
    private @NotNull String[] artists;
    private @NotNull String artistsConcatenated;
    private @NotNull String album;
    private @NotNull String folderPath;
    private long durationInMs;
    private boolean isFavorite;
    private boolean isBroken = false;
    private ArrayList<Playlist> playlists;

    public AudioData(@NotNull File file) {
        this.file = file;
        this.folderPath = file.getParentFile().getAbsolutePath();

        try {
            AudioHeader audioHeader = MP3Tools.getAudioHeader(file);
            if (audioHeader != null) {
                double durationInSecs;
                if (audioHeader instanceof MP3AudioHeader) {
                    durationInSecs = ((MP3AudioHeader) audioHeader).getPreciseTrackLength();
                } else {
                    durationInSecs = audioHeader.getTrackLength();
                }
                durationInMs = (long) Math.floor(durationInSecs * 1000);
            }

            Tag tag = MP3Tools.getTag(file);
            if (tag != null) {
                name = formatStringData(tag.getFirst(FieldKey.TITLE));
                artistsConcatenated = formatStringData(tag.getFirst(FieldKey.ARTIST));
                artists = artistsConcatenated.split("/");

                album = formatStringData(tag.getFirst(FieldKey.ALBUM));
                Artwork artwork = tag.getFirstArtwork();
                if (artwork != null)
                    ArtworkManager.getInstance().registerThumbnail(this, artwork.getBinaryData());
                else
                    ArtworkManager.getInstance().registerThumbnail(this, null);

            } else {
                applyDefaultTags();
            }
        } catch (Exception e) {
            //Set everything to defaults if any exception occurs
            applyDefaultTags();
            Log.error(e);
        }

    }

    private void applyDefaultTags() {
        name = StringUtils.removeFileExtension(file.getName(), true);
        artists = new String[]{"Unknown"};
        artistsConcatenated = album = "Unknown";
        ArtworkManager.getInstance().registerThumbnail(this, null);
    }

    private static String formatStringData(String s) {
        if (s == null || s.isEmpty())
            return "Unknown";
        else
            return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioData that = (AudioData) o;
        return file.getPath().equals(that.file.getPath()) || (Double.compare(that.durationInMs, durationInMs) == 0 && name.equals(that.name) && artists.equals(that.artists));
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, name, artistsConcatenated, durationInMs);
    }

    public boolean containsString(String s) {
        return name.toLowerCase(Locale.ROOT).contains(s) || artistsConcatenated.toLowerCase(Locale.ROOT).contains(s);
    }

    @Override
    public String toString() {
        return ("""
                --------------------------------------------------------------------------------------------------
                Title: %s
                Duration: %ss
                Artist: %s
                Path: %s
                ---------------------------------------------------------------------------------------------------
                """).formatted(name, durationInMs, artists, getFile().getPath());
    }

    public static boolean isValidAudio(Path path) {
        boolean isMP3 = path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".mp3");
        boolean isValidSize = false;
        if (isMP3) {
            try {
                isValidSize = Files.size(path) > StartupSettings.MINIMUM_FILE_SIZE;
            } catch (Exception e) {
                Log.error(e);
                e.printStackTrace();
            }
        }
        return isMP3 && isValidSize;
    }

    //
    //SETTERS
    //
    public void setDurationInMs(double durationInMs) {
        setDurationInMs(Math.floor(durationInMs));
    }

    public void setDurationInMs(long durationInMs) {
        this.durationInMs = (long) durationInMs;
    }

    public void setFile(@NotNull File file) {
        this.file = file;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setArtists(@NotNull String[] artists) {
        this.artists = artists;
    }

    public void setAlbum(@NotNull String album) {
        this.album = album;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    //
    // GETTERS
    //
    public @NotNull BufferedImage getArtwork() {
//        Tag tag = MP3Tools.getTag(file);
//        return tag != null ? tag.getFirstArtwork() != null ? tag.getFirstArtwork().getBinaryData() : null : null;
        return ArtworkManager.getInstance().getArtwork(this);
    }

    public @NotNull File getFile() {
        return file;
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String[] getArtists() {
        return artists;
    }

    public @NotNull String getArtistsConcatenated() {
        return artistsConcatenated;
    }

    public @NotNull String getAlbum() {
        return album;
    }

    public @NotNull long getDurationInMs() {
        return durationInMs;
    }


    public boolean isFavorite() {
        return isFavorite;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }
//
//    public static @NotNull BufferedImage getDefaultArtwork() {
//        if (artworkAltImageData == null) {
//            try {
//                BufferedImage artworkAltImage = ImageIO.read(Objects.requireNonNull(AudioData.class.getClassLoader().getResource("images/artwork.jpg"), "Artwork.jpg not found"));
//                artworkAltImageData = GraphicsUtils.resize(artworkAltImage, artworkSize,artworkSize);
//            } catch (Exception e) {
//                Log.error(e.toString());
//                artworkAltImageData = new BufferedImage(artworkSize,artworkSize,BufferedImage.TYPE_INT_RGB);
//            }
//        }
//        return artworkAltImageData;
//    }

    @Override
    public int compareTo(@NotNull AudioData o) {
        return file.getPath().compareTo(o.getFile().getPath());
    }

    public void setBroken(boolean broken) {
        isBroken = broken;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public boolean isBroken() {
        return isBroken;
    }
}