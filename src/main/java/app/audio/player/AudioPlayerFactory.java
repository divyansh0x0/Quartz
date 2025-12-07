package app.audio.player;

import app.dialogs.DialogFactory;
import material.utils.Log;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.GstException;

import java.util.logging.Logger;

public class AudioPlayerFactory {
    public static AudioPlayerModel createPlayer() {
        try {
            return new AudioPlayer(); // real player
        } catch (GstException e) {
            Log.error("GSTREAMER ERROR"+e.getLocalizedMessage());
            DialogFactory.showErrorDialog("[GSTREAMER ERROR]"+e.toString());
            return new AudioPlayerFallback(); // fallback
        }
    }
}
