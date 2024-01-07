package app.audio.player;

public interface FrostPlayerVisualizerListener {
    void visualizerDataUpdated(float[] magnitudes, int threshold);
}
