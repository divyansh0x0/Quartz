package app.audio.player;

import app.audio.AudioData;

import java.io.FileNotFoundException;
import java.time.Duration;

public interface AudioPlayerModel {
    void load(AudioData audio) throws FileNotFoundException;
     void play();
    void pause();
    void stop();
    void seek(Duration newDur);
    long getTotalTimeNanos();
    long getCurrentTimeNanos();

    double getVolume();
    void setVolume(double newVolume);

    void dispose();
}
