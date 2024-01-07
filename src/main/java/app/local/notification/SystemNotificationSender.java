package app.local.notification;

public interface SystemNotificationSender {
    void sendNotification(String title, String subtitle, String iconPath);
    void updateMediaPlayer(String title,String subtitle, boolean isPaused);
    void addNextAudioRequestHandler(Runnable r);
    void addPrevAudioRequestHandler(Runnable r);
    void addPauseAudioRequestHandler(Runnable r);
    void addPlayAudioRequestHandler(Runnable r);
}
