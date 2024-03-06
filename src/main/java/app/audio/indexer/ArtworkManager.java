package app.audio.indexer;

import app.audio.AudioData;
import app.comparators.ImageComparator;
import material.constants.Size;
import material.utils.GraphicsUtils;
import material.utils.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public class ArtworkManager {
    private static BufferedImage DEFAULT_ARTWORK;
    private static final Size ARTWORK_SIZE = new Size(200);
    private static final HashMap<Integer, BufferedImage> ARTWORKS = new HashMap<>();
    private static final TreeMap<AudioData, Integer> ARTWORKS_POINTER = new TreeMap<>();
    private static ArtworkManager instance;

    public static int DEFAULT_ARTWORK_POINTER;

    static {
        if (DEFAULT_ARTWORK == null) {
            try {
                BufferedImage artworkAltImage = ImageIO.read(Objects.requireNonNull(AudioData.class.getClassLoader().getResource("images/artwork.jpg"), "Artwork.jpg not found"));
                DEFAULT_ARTWORK = GraphicsUtils.resize(artworkAltImage, ARTWORK_SIZE.getWidthInt(), ARTWORK_SIZE.getHeightInt());
            } catch (Exception e) {
                Log.error(e.toString());
                DEFAULT_ARTWORK = new BufferedImage(ARTWORK_SIZE.getWidthInt(), ARTWORK_SIZE.getHeightInt(), BufferedImage.TYPE_INT_RGB);
            }
            DEFAULT_ARTWORK.setAccelerationPriority(1f);
            DEFAULT_ARTWORK_POINTER = ImageComparator.calculateMurmurHash(DEFAULT_ARTWORK);

            ARTWORKS.put(DEFAULT_ARTWORK_POINTER, DEFAULT_ARTWORK);
        }
    }


    public void registerThumbnail(@NotNull AudioData audio, byte @Nullable [] artworkData) {

            try {
                if (artworkData != null) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(artworkData);
                    BufferedImage artwork = ImageIO.read(bais);
                    int hash = ImageComparator.calculateMurmurHash(artwork);
                    if (!ARTWORKS.containsKey(hash)) {
                        int w = artwork.getWidth(null);
                        int h = artwork.getHeight(null);
                        int maxSize = Math.min(w, h);
                        int x = (w - maxSize) / 2;
                        int y = (h - maxSize) / 2;
                        artwork = GraphicsUtils.resize(artwork.getSubimage(x, y, maxSize, maxSize), ARTWORK_SIZE.getWidthInt(), ARTWORK_SIZE.getHeightInt());
                        artwork.setAccelerationPriority(1f);
                        ARTWORKS.put(hash, artwork);
                        bais.close();
                    } else
                        Log.warn("Discarding duplicate artwork for " + audio.getFile() + "\n for hash: " + hash);

                    ARTWORKS_POINTER.put(audio, hash);
                } else
                    ARTWORKS_POINTER.put(audio, DEFAULT_ARTWORK_POINTER);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }
    public void registerThumbnailAsync(@NotNull AudioData audio, byte @Nullable [] artworkData){
        Thread.startVirtualThread(() -> {
            registerThumbnail(audio,artworkData);
        });
    }

    public @NotNull BufferedImage getArtwork(AudioData audio) {
        return ARTWORKS.get(ARTWORKS_POINTER.get(audio));
    }

    public @NotNull Size getArtworkSize() {
        return ARTWORK_SIZE;
    }

    public static ArtworkManager getInstance() {
        if (instance == null)
            instance = new ArtworkManager();
        return instance;
    }

    private ArtworkManager() {

    }


}