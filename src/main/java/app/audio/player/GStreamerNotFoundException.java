package app.audio.player;

public class GStreamerNotFoundException extends RuntimeException {
    public GStreamerNotFoundException() {
        super("GStreamer libraries not found or failed to initialize.");
    }

    public GStreamerNotFoundException(String message) {
        super(message);
    }
}
