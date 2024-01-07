package app.audio.player;

import app.audio.FrostAudio;

import java.io.FileNotFoundException;
import java.time.Duration;

public interface FrostPlayerModel {
    void load(FrostAudio audio) throws FileNotFoundException;
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
