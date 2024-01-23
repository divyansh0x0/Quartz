package app.audio.player;

public class AudioControllerException extends Exception {
    private final int code;
    public AudioControllerException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}