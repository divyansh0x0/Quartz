package app.components.buttons.control;

import static app.components.Icons.REPEAT;

public class RepeatButton extends ControlButton{
    public RepeatButton() {
        super(REPEAT);
        setToolTipText("Repeat");
    }

    @Override
    public void toggled(boolean isActive) {
        setActive(isActive);
        updateTheme();
    }
}
