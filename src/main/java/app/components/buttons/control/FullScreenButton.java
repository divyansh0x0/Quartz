package app.components.buttons.control;

import app.components.Icons;
import app.main.Aphrodite;

public class FullScreenButton extends ControlButton{
    private static FullScreenButton instance;
    private FullScreenButton() {
        super(Icons.FULLSCREEN_OPEN);
    }

    @Override
    protected void toggled(boolean isActive) {
        if(isActive) {
            setIcon(Icons.FULLSCREEN_OPEN);
            Aphrodite.getInstance().switchToChillMode();
            setToolTipText("Turn on Fullscreen");
        }else{
            setIcon(Icons.FULLSCREEN_EXIT);
            setToolTipText("Turn off Fullscreen");
        }
    }

    public static FullScreenButton getInstance(){
        if(instance == null)
            instance = new FullScreenButton();
        return instance;
    }
}
