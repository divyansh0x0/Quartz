package app.components.buttons.control;

import material.theme.ThemeColors;

import static app.components.Icons.QUEUE;

public class QueueButton extends ControlButton{
    public QueueButton() {
        super(QUEUE);
        setToolTipText("Show Queue");
    }

    @Override
    public void toggled(boolean isActive) {
        setActive(isActive);
        updateTheme();
    }
}
