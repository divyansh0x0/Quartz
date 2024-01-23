package app.components.listeners;

import app.audio.AudioData;

import java.io.Serializable;

public interface AudioTileClickListener extends Serializable {
    void clicked(AudioData audioData);
}
