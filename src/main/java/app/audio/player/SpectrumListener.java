package app.audio.player;

import app.components.spectrum.SharedSpectrumManager;

public class SpectrumListener implements FrostPlayerVisualizerListener {
    private static SpectrumListener instance;
    private SpectrumListener() {

    }

    public static SpectrumListener getInstance() {
        if(instance == null)
            instance = new SpectrumListener();
        return instance;
    }

    @Override
    public void visualizerDataUpdated(float[] magnitudes, int threshold) {

        SharedSpectrumManager.setMagnitudes(magnitudes);
    }
}
