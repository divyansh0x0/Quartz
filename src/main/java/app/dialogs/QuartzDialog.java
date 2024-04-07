package app.dialogs;

import app.main.Quartz;
import material.Padding;
import material.component.MaterialLabel;
import material.component.enums.LabelStyle;
import material.constants.Size;
import material.containers.MaterialPanel;
import material.theme.ThemeColors;
import material.utils.OsInfo;
import material.utils.StringUtils;
import material.window.MaterialDialogWindow;
import material.window.MaterialWindow;
import material.window.MaterialWindowGrip;
import material.window.buttons.CloseButton;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class QuartzDialog extends MaterialDialogWindow {
    public static final Size MAX_SIZE = new Size(640, 640 / 12 * 9);
    //    private static CustomWindow instance;
//    private final MaterialPanel container = new MaterialPanel().setElevationDP(null);
    private static final MaterialWindow window = Quartz.getInstance().getWindow();
    private final @NotNull DialogType dialogType;

    public QuartzDialog(String txt, Size minimumSize, DialogType dialogType) {
        super(window, StringUtils.titleCase(dialogType.name()),minimumSize, false, true);
        this.dialogType = dialogType;
        CloseButton _close_btn = new CloseButton();
        _close_btn.setIconSizeRatio(1);
        _close_btn.setPadding(Padding.ONE);
        MaterialLabel titleLabel = new MaterialLabel(StringUtils.titleCase(txt)).setLabelStyle(LabelStyle.PRIMARY);
        MaterialPanel captionBar = new MaterialPanel(new MigLayout("fill, insets 0")).setElevationDP(null);
        MaterialPanel titleBar = new MaterialPanel(new MigLayout("fill, insets 0")).setElevationDP(null);
        this.setResizable(false);
        if (!OsInfo.isCustomWindowSupported()) {
            makeWindowUndecorated();
        } else
            setGrip(MaterialWindowGrip.EXCLUDE_CAPTION_BAR_WIDTH);


        captionBar.add(_close_btn,"east, h 25!,w 25!");
        titleBar.add(titleLabel,"west,pad 0 5 0 5 , grow");
        titleBar.add(captionBar,"east");

        getRootPanel().add(titleBar,"north, h 25!");
        setCustomCaptionBar(captionBar);
    }



    private void makeWindowUndecorated() {
        this.setUndecorated(true);
        componentMover.registerComponent(this);
    }

    @Override
    protected void updateTheme() {
        Color newBg;
        switch (dialogType){
            case WARN -> newBg = ThemeColors.getBackgroundWarn();
            case ERROR -> newBg = ThemeColors.getBackgroundDanger();
            case SUCCESS -> newBg = ThemeColors.getBackgroundSuccess();
            default -> newBg = ThemeColors.getBackground();
        }

        getRootPanel().animateBG(newBg);
    }
}