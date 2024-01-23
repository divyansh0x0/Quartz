package app.audio.player;

public interface AudioVisualizerListener {
    void visualizerDataUpdated(float[] magnitudes, int threshold);
}
