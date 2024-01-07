package app.components.buttons.playback;

import material.theme.ThemeColors;

import static app.components.Icons.PAUSE_AUDIO;
import static app.components.Icons.PLAY_AUDIO;

public class PlayButton extends PlaybackButton {

    public PlayButton() {
        super(PLAY_AUDIO);
        isActive = true;
        setIconSizeRatio(0.7);
        setToolTipText("Play");
        requestFocusInWindow();
    }

    @Override
    public void updateTheme() {
        animateBG(ThemeColors.getAccent());
        setForeground(ThemeColors.getColorOnAccent());

    }

    @Override
    public void changeUI(boolean isActive) {
        if(isActive) {
            setIcon(PLAY_AUDIO);
            setToolTipText("Play");
        }
        else {
            setIcon(PAUSE_AUDIO);
            setToolTipText("Pause");
        }
    }

    public boolean isPaused() {
        return isActive;
    }

    public void setPaused(boolean paused) {
        setActive(paused);
        repaint();
    }
}
