package app.components.buttons.control;

import static app.components.Icons.SHUFFLE;

public class ShuffleButton extends ControlButton{
    public ShuffleButton(){
        super(SHUFFLE);
        setToolTipText("Shuffle Queue");
    }
    @Override
    public void toggled(boolean isActive) {
        setActive(isActive);
        updateTheme();
    }
}
