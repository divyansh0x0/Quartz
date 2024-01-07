package app.components.buttons.control;

import app.components.containers.MainPanel;
import app.components.enums.ViewType;

import static app.components.Icons.VISUALIZER;

public class SpectrumButton extends ControlButton{
    public SpectrumButton() {
        super(VISUALIZER);
        setToolTipText("Show Visualizer");
    }

    @Override
    public void toggled(boolean isActive) {
        if(isActive && MainPanel.getInstance().getCurrentView() != ViewType.SpectrumView)
            MainPanel.getInstance().switchView(ViewType.SpectrumView);
        else if(MainPanel.getInstance().getCurrentView() != ViewType.DefaultView)
            MainPanel.getInstance().switchView(ViewType.DefaultView);
        setActive(isActive);
        updateTheme();
    }
}
