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
    private final ArrayList<FrostAudio> frostAudios = new ArrayList<>();
    private final String name;
    private BufferedImage defaultArtistImage;
    private BufferedImage lastBufferedImage;
    private final int imgResolution = 524;


    public Artist(String name) {
        this.name = StringUtils.toHeading(name);
    }

    public ArrayList<FrostAudio> getFrostAudios() {
//        Log.info(name +":\n" + frostAudios);
        return new ArrayList<>(frostAudios);
    }

    public void setFrostAudios(ArrayList<FrostAudio> frostAudios) {
        this.frostAudios.clear();
        this.frostAudios.addAll(frostAudios);
    }

    public void addFrostAudio(FrostAudio frostAudio) {
        if(!frostAudios.contains(frostAudio))
            frostAudios.add(frostAudio);
    }

    public void removeFrostAudio(FrostAudio frostAudio) {
        frostAudios.remove(frostAudio);
    }

    public void removeAllFrostAudios() {
        frostAudios.clear();
    }

    public String getName() {
        return name;
    }

    public @Nullable BufferedImage getArtistImage() {
        try {
            if (defaultArtistImage == null) {
                if (lastBufferedImage == null) {
                    int numOfImages = frostAudios.size() >= 4 ? 4 : 1;
                    Image[] images = new Image[numOfImages];
                    for (int i = 0; i < images.length; i++) {
                        images[i] = frostAudios.get(i).getArtwork();
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
        int result = getFrostAudios().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Artist: " + name;
    }
}
