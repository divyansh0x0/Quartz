package app.local.notification;
import org.jetbrains.annotations.NotNull;
import material.utils.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GnomeNotify implements SystemNotificationSender {
    private static GnomeNotify instance;
    private static final ProcessBuilder processBuilder = new ProcessBuilder();

    private static int NID = -1;
    private static final String PROGRAM_NAME = "/usr/bin/notify-send";

    public static GnomeNotify getInstance() {
        if (instance == null)
            instance = new GnomeNotify();
        return instance;
    }

    @Override
    public void sendNotification(String title, String subtitle, String iconPath) {
        try {
            processBuilder.command(getNotifyCommand(title, subtitle, iconPath));
            Log.info("command: " + processBuilder.command());
            BufferedReader stdError = new BufferedReader(new InputStreamReader(processBuilder.start().getErrorStream()));
            String s = "";
            while ((s = stdError.readLine()) != null) {
                Log.error("Error occurred while sending notification using NOTIFY-SEND: " + s);
                NID = Integer.parseInt(s);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateMediaPlayer(String title, String subtitle, boolean isPaused) {

    }

    @Override
    public void addNextAudioRequestHandler(Runnable r) {

    }

    @Override
    public void addPrevAudioRequestHandler(Runnable r) {

    }

    @Override
    public void addPauseAudioRequestHandler(Runnable r) {

    }

    @Override
    public void addPlayAudioRequestHandler(Runnable r) {

    }

    private void sendMediaPlayer(){
//        try(DBusConnection dBusConnection = DBusConnection.newConnection(DBusConnection.DBusBusType.SESSION)){
//            MediaPlayer2 mediaPlayer2 = dBusConnection.getRemoteObject("org.mpris.MediaPlayer2.frost","/org/mpris/MediaPlayer2", MediaPlayer2.class);
//            Log.info("can raise: " + mediaPlayer2.CanRaise());
//            Properties properties = dBusConnection.getRemoteObject("org.freedesktop.DBus.Properties","");
//        } catch (DBusException | IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static @NotNull List<String> getNotifyCommand(String title, String subtitle, String iconPath) {
        ArrayList<String> list = new ArrayList<>();
        list.add(PROGRAM_NAME);
        list.add(title);
        list.add(subtitle);
//        if(NID != -1 ) {
//            list.add("-r");
//            list.add(String.valueOf(NID));
//        }

        if (iconPath != null) {
            list.add("-i");
            list.add(iconPath);
        }

        list.add("-h");
        list.add("int:transient:1");
//        list.add("-p");


        return list;
    }
}
