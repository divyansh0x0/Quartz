package app.components.buttons.playback;

import static app.components.Icons.*;

public class NextButton extends PlaybackButton {

    public NextButton() {
        super(NEXT_AUDIO);
        setToolTipText("Next");
    }

    @Override
    public void changeUI(boolean isActive) {

    }
}
