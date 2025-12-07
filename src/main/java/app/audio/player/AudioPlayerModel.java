package app.audio.player;

import app.audio.AudioData;

import java.io.FileNotFoundException;

public interface AudioPlayerModel {
    void load(AudioData audio) throws FileNotFoundException;
     void play();
    void pause();
    void stop();
    void seek(long newDurMs);
    long getTotalTimeMs();
    long getCurrentTimeMs();

    double getVolume();
    void setVolume(double newVolume);

    void dispose();

    void addVisualizerDataListener(AudioVisualizerListener instance);

    void addMediaEndedListener(Runnable mediaEnded);

    void setThreshold(int spectrumThreshold);

    void enableVisualizerSampling(boolean b);

    void addExceptionListener(AudioPlayerExceptionListener handleError);

    void setSpectrumBands(int spectrumBandsNum);

    boolean isDisposed();
}
