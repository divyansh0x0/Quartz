package app.dialogs;

import material.borders.RoundedBorder;
import material.containers.MaterialPanel;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.ThemeType;
import material.tools.ColorUtils;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class DialogRootPanel extends MaterialPanel {
    private static final int LIGHTEN_DARKEN_PERCENTAGE = 10;
    public static final int INSETS = 5;
    private @NotNull DialogType dialogType;
    public DialogRootPanel(@NotNull DialogType type){
        super(new MigLayout("nogrid, flowy, fill, inset "+INSETS+", gap 0"));
        dialogType = type;
        updateTheme();
    }

    public DialogRootPanel(){
        this(DialogType.NORMAL);
    }
    public void updateTheme() {
        ThemeType themeType = ThemeManager.getInstance().getThemeType();
        Color bg = ThemeColors.getBackground();
        switch (dialogType){
            case WARN -> bg = ThemeColors.getBackgroundWarn();
            case ERROR -> bg = ThemeColors.getBackgroundDanger();
            case SUCCESS -> bg = ThemeColors.getBackgroundSuccess();
            case NORMAL -> bg = ThemeColors.getBackground();
        }

        Color borderColor;
        if(themeType == ThemeType.Dark)
            borderColor = ColorUtils.lighten(bg,LIGHTEN_DARKEN_PERCENTAGE);
        else
            borderColor =  ColorUtils.darken(bg,LIGHTEN_DARKEN_PERCENTAGE);

        setBorder(new RoundedBorder(borderColor));
        setBackground(bg);
        revalidate();
        invalidate();
        repaint();
    }

    public DialogType getDialogType() {
        return dialogType;
    }

    public DialogRootPanel setDialogType(DialogType dialogType) {
        this.dialogType = dialogType;
        updateTheme();
        return this;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        repaint();
        validate();
        invalidate();
    }
}
