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
}
