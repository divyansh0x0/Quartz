package app.audio.player;

import app.audio.AudioData;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

public class AudioPlayerFallback implements AudioPlayerModel {
    private static void throwIfUsed() {
        throw new GStreamerNotFoundException();
    }

    @Override
    public void load(AudioData audio) throws FileNotFoundException {
        throwIfUsed();
    }

    @Override
    public void play() {
        throwIfUsed();
    }

    @Override
    public synchronized void pause() {
        throwIfUsed();
    }

    @Override
    public synchronized void stop() {
        throwIfUsed();
    }

    @Override
    public synchronized void seek(long newPosMs) {
        throwIfUsed();
    }

    @Override
    public synchronized long getTotalTimeMs() {
        return 0;
    }

    @Override
    public long getCurrentTimeMs() {
        return 0;
    }

    @Override
    public double getVolume() {
        return 0;
    }

    @Override
    public void setVolume(double newVolume) {
    }

    @Override
    public void dispose() {
        // do nothing
    }

    @Override
    public void addVisualizerDataListener(AudioVisualizerListener instance) {
    }

    @Override
    public void addMediaEndedListener(Runnable mediaEnded) {
    }

    @Override
    public void setThreshold(int spectrumThreshold) {
    }

    @Override
    public void enableVisualizerSampling(boolean b) {
    }

    @Override
    public void addExceptionListener(AudioPlayerExceptionListener handleError) {
        throwIfUsed();
    }

    @Override
    public void setSpectrumBands(int spectrumBandsNum) {
    }

    @Override
    public boolean isDisposed() {
        return true;
    }
}
