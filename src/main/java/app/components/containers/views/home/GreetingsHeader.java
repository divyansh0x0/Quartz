package app.components.containers.views.home;

import material.component.MaterialLabel;
import material.component.enums.LabelStyle;
import material.containers.MaterialPanel;
import net.miginfocom.swing.MigLayout;

import java.time.LocalDateTime;

public class GreetingsHeader extends MaterialPanel {
    private static GreetingsHeader instance;
    private static final int PRIMARY_LABEL_FONT_SIZE = 30;
    private static final int SECONDARY_LABEL_FONT_SIZE = 15;
    private static final MaterialLabel primaryLabel = new MaterialLabel().setLabelStyle(LabelStyle.PRIMARY);
    private static final MaterialLabel secondaryLabel = new MaterialLabel().setLabelStyle(LabelStyle.SECONDARY);
    private static final MaterialPanel greetingsContainer = new MaterialPanel(new MigLayout("nogrid, flowy, fill, insets 5 10"));
    private GreetingsHeader(){
        super(new MigLayout("fill"));
        setElevationDP(null);


        primaryLabel.setFontSize(PRIMARY_LABEL_FONT_SIZE);
        primaryLabel.setEnableAutoResize(true);
        secondaryLabel.setFontSize(SECONDARY_LABEL_FONT_SIZE);
        secondaryLabel.setEnableAutoResize(true);


        greetingsContainer.setElevationDP(getElevationDP());
        greetingsContainer.add(primaryLabel, "growX,gapY 0");
        greetingsContainer.add(secondaryLabel, "growX,gapY 0");

        add(greetingsContainer, "growX");

        initHeader();
    }

    private void initHeader() {
        String greeting;
        int hour = LocalDateTime.now().getHour();
        if (hour >= 17)
            greeting = "Good Evening!";
        else if (hour >=12)
            greeting = "Good Afternoon!";
        else
            greeting = "Good Morning!";

        String primaryStr  = greeting;
        String secondaryStr = "What would you like to listen today?";

        primaryLabel.setText(primaryStr);
        secondaryLabel.setText(secondaryStr);
    }

    public static GreetingsHeader getInstance(){
        if(instance == null)
            instance= new GreetingsHeader();
        return instance;
    }
}
