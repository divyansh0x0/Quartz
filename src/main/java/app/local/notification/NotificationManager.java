package app.local.notification;

import app.audio.FrostAudio;
import app.local.cache.NotifCacheManager;
import material.utils.OsUtils;

import java.util.concurrent.CompletableFuture;

public class NotificationManager {
    private static NotificationManager instance;
    private CompletableFuture<Void> AsyncTask;

    private NotificationManager() {

    }

    public static NotificationManager getInstance() {
        if (instance == null)
            instance = new NotificationManager();
        return instance;
    }

    public void notifyNewPlayback(FrostAudio frostAudio) {
        if(AsyncTask != null){
            AsyncTask.cancel(true);
            AsyncTask = null;
        }
        AsyncTask = CompletableFuture.runAsync(() -> {
            if(OsUtils.isUnix())
                GnomeNotify.getInstance().sendNotification(frostAudio.getName(),frostAudio.getArtistsConcatenated(), NotifCacheManager.getInstance().writeNotifImageFile(frostAudio).getAbsolutePath());
        });
    }
}
