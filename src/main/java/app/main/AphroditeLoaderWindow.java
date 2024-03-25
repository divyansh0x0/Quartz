package app.main;

import material.component.MaterialLabel;
import material.component.enums.LabelStyle;
import material.constants.Size;
import material.utils.ComponentMover;
import material.utils.OsInfo;
import material.window.MaterialDialogWindow;
import material.window.MaterialWindowGrip;

public class AphroditeLoaderWindow extends MaterialDialogWindow {
        private static final ComponentMover componentMover = new ComponentMover();
        private static final String NAME = "LOADING Aphrodite";
        private static final Size WINDOW_SIZE = Size.getGoldenSize(300).swapValues();

        private static final MaterialLabel LABEL = new MaterialLabel("Loading Aphrodite", LabelStyle.PRIMARY, MaterialLabel.HorizontalAlignment.CENTER, MaterialLabel.VerticalAlignment.CENTER);

        public AphroditeLoaderWindow(Aphrodite parent) {
            super(parent.getWindow(),NAME, WINDOW_SIZE, false, true);

            this.setResizable(false);
            if (!OsInfo.isCustomWindowSupported()) {
                makeWindowUndecorated();
            } else
                setGrip(MaterialWindowGrip.IGNORE_CAPTION_BAR.setGripHeight(WINDOW_SIZE.getHeightInt()).setGripWidth(WINDOW_SIZE.getWidthInt()));


            LABEL.setFontSize(20);
            getRootPanel().add(LABEL, "grow");
        }

        private void makeWindowUndecorated() {
            this.setUndecorated(true);
            componentMover.registerComponent(this);
        }



}
