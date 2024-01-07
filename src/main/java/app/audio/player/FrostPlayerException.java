package app.audio.player;

public class FrostPlayerException extends Exception {
    private final int code;
    public FrostPlayerException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}