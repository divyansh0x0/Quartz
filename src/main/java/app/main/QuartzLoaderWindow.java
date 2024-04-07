package app.main;

import material.component.MaterialLabel;
import material.component.enums.LabelStyle;
import material.constants.Size;
import material.utils.OsInfo;
import material.window.MaterialDialogWindow;
import material.window.MaterialWindowGrip;

public class QuartzLoaderWindow extends MaterialDialogWindow {
        private static final String NAME = "LOADING Quartz";
        private static final Size WINDOW_SIZE = Size.getGoldenSize(300).swapValues();

        private static final MaterialLabel LABEL = new MaterialLabel("Loading Quartz", LabelStyle.PRIMARY, MaterialLabel.HorizontalAlignment.CENTER, MaterialLabel.VerticalAlignment.CENTER);

        public QuartzLoaderWindow(Quartz parent) {
            super(parent.getWindow(),NAME, WINDOW_SIZE, false, true);

            this.setResizable(false);
            if (!OsInfo.isCustomWindowSupported()) {
                makeWindowUndecorated();
            } else
                setGrip(MaterialWindowGrip.CUSTOM.setGripHeight(WINDOW_SIZE.getHeightInt()).setGripWidth(WINDOW_SIZE.getWidthInt()));


            LABEL.setFontSize(20);
            getRootPanel().add(LABEL, "grow");
        }

        private void makeWindowUndecorated() {
            this.setUndecorated(true);
            componentMover.registerComponent(this);
        }



}
