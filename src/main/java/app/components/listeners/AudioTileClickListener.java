package app.components.listeners;

import app.audio.FrostAudio;
import java.io.Serializable;

public interface AudioTileClickListener extends Serializable {
    void clicked(FrostAudio frostAudio);
}
