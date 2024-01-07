package app.components.buttons.control;

import app.settings.constraints.ControlButtonConstraints;
import material.component.MaterialIconButton;
import material.listeners.MouseClickListener;
import material.theme.ThemeColors;
import org.kordamp.ikonli.Ikon;

import java.awt.event.InputEvent;

public abstract class ControlButton extends MaterialIconButton implements MouseClickListener {
    protected boolean isActive = false;

    public ControlButton(Ikon icon) {
        super(icon);
        setAllowMouseAnimation(false);
        setMaximumSize(ControlButtonConstraints.BUTTON_SIZE);
        setMinimumSize(ControlButtonConstraints.BUTTON_SIZE);
        setPreferredSize(ControlButtonConstraints.BUTTON_SIZE);
        setIconSizeRatio(0.9);
        setPadding(ControlButtonConstraints.PADDING);
        setCornerRadius(100);
        setTransparentBackground(true);
        addLeftClickListener(this);
    }

    @Override
    public void updateTheme() {
        if (!isActive)
            animateFG(ThemeColors.getTextSecondary());
        else
            animateFG(ThemeColors.getAccent());
    }

    @Override
    public void clicked(InputEvent e) {
        toggled(!isActive);
    }

    public boolean isActive() {
        return isActive;
    }

    public ControlButton setActive(boolean active) {
        isActive = active;
        updateTheme();
        return this;
    }

    protected abstract void toggled(boolean isActive);
}
