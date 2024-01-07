package app.components.buttons.playback;

import static app.components.Icons.*;

public class PrevButton extends PlaybackButton {
    public PrevButton() {
        super(PREVIOUS);
        setToolTipText("Previous");
    }

    @Override
    public void changeUI(boolean isActive) {

    }
}
