package app.audio.player;

import app.components.spectrum.SharedSpectrumManager;

public class Spectrum implements AudioVisualizerListener {
    private static Spectrum instance;
    private Spectrum() {

    }

    public static Spectrum getInstance() {
        if(instance == null)
            instance = new Spectrum();
        return instance;
    }

    @Override
    public void visualizerDataUpdated(float[] magnitudes, int threshold) {

        SharedSpectrumManager.setMagnitudes(magnitudes);
    }
}
