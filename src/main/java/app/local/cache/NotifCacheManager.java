package app.local.cache;

import app.audio.AudioData;
import material.utils.Log;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;

public class NotifCacheManager {
    private static NotifCacheManager instance;
    private static final String OUT_PATH = "out/";
    private static String NAME = "notif";
    private static final File CACHE_FILE = new File(OUT_PATH + "cache.txt");
    private static File NOTIF_IMAGE_FILe;

    private NotifCacheManager() {
        try {
            if (!NOTIF_IMAGE_FILe.exists()) {
                if (NOTIF_IMAGE_FILe.createNewFile()) {
                    Log.success("NOTIF_IMAGE file created successfully");
                }
            }
        } catch (Exception e) {
            Log.error("Exception thrown while creating cache files: " + e);
        }
    }

    public File writeNotifImageFile(AudioData audioData) {
        try {
            if (NOTIF_IMAGE_FILe != null && NOTIF_IMAGE_FILe.exists())
                NOTIF_IMAGE_FILe.delete();
            NAME = "notif - " + audioData.getName();
            NOTIF_IMAGE_FILe = File.createTempFile(NAME, ".jpg");
            NOTIF_IMAGE_FILe.deleteOnExit();
            RenderedImage img = audioData.getArtwork();
            ImageIO.write(img, "jpg", NOTIF_IMAGE_FILe);
        } catch (Exception e) {
            Log.error("Exception while writing notification image" + e);
            e.printStackTrace();
        }
        return getNotifImageFile();
    }

    public File getNotifImageFile() {
        return NOTIF_IMAGE_FILe;
    }

    public static NotifCacheManager getInstance() {
        if (instance == null)
            instance = new NotifCacheManager();
        return instance;
    }
}
