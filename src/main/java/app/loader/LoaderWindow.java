package app.loader;

import material.constants.Size;
import material.component.MaterialLabel;
import material.component.enums.LabelStyle;
import material.window.MaterialWindow;
import material.utils.ComponentMover;
import material.utils.OsUtils;

import javax.swing.*;

public class LoaderWindow extends MaterialWindow {
    private static final ComponentMover componentMover = new ComponentMover();
    private static final String NAME = "LOADING FROST";
    private static final Size MIN_SIZE = Size.getGoldenSize(300).swapValues();

    private static final MaterialLabel LABEL = new MaterialLabel("Loading Frost", LabelStyle.PRIMARY, MaterialLabel.HorizontalAlignment.CENTER, MaterialLabel.VerticalAlignment.CENTER);

    public LoaderWindow() {
        super(NAME, MIN_SIZE,false,true);

        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setResizable(false);
        if (!OsUtils.isCustomWindowSupported()) {
            makeWindowUndecorated();
        } else
            setGripSize(MIN_SIZE.getHeightInt());

        LABEL.setFontSize(20);
        getRootPanel().add(LABEL, "grow");
    }

    private void makeWindowUndecorated() {
        this.setUndecorated(true);
        componentMover.registerComponent(this);
    }
}
