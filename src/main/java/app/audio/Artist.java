package app.audio;

import app.audio.indexer.ArtworkManager;
import org.jetbrains.annotations.Nullable;
import material.utils.GraphicsUtils;
import material.utils.Log;
import material.utils.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Artist {
    private final ArrayList<AudioData> audioData = new ArrayList<>();
    private final String name;
    private BufferedImage defaultArtistImage;
    private BufferedImage lastBufferedImage;
    private final int imgResolution = 524;


    public Artist(String name) {
        this.name = StringUtils.toHeading(name);
    }

    public ArrayList<AudioData> getAudioDataList() {
//        Log.info(name +":\n" + audioData);
        return new ArrayList<>(audioData);
    }

    public void addAudioDataList(ArrayList<AudioData> audioData) {
        this.audioData.clear();
        this.audioData.addAll(audioData);
    }

    public void addAudioData(AudioData audioData) {
        if(!this.audioData.contains(audioData))
            this.audioData.add(audioData);
    }

    public void removeAudioData(AudioData audioData) {
        this.audioData.remove(audioData);
    }

    public void removeAllAudioDatas() {
        audioData.clear();
    }

    public String getName() {
        return name;
    }

    public @Nullable BufferedImage getArtistImage() {
        try {
            if (defaultArtistImage == null) {
                if (lastBufferedImage == null) {
                    int numOfImages = audioData.size() >= 4 ? 4 : 1;
                    Image[] images = new Image[numOfImages];
                    for (int i = 0; i < images.length; i++) {
                        images[i] = audioData.get(i).getArtwork();
                    }

                    lastBufferedImage = GraphicsUtils.imageMerger(ArtworkManager.getInstance().getArtworkSize().getWidthInt(),images);
                }
                return lastBufferedImage;
            }
            return defaultArtistImage;
        } catch (Exception e) {
            Log.error("Error in artwork of artist (" + name + "):" + e);
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist artist)) return false;
        return this.getName().equalsIgnoreCase(artist.getName());
    }

    @Override
    public int hashCode() {
        int result = getAudioDataList().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Artist: " + name;
    }
}
