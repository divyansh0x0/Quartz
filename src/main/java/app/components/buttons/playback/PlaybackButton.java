package app.components.buttons.playback;

import app.settings.constraints.ControlButtonConstraints;
import material.component.MaterialIconButton;
import material.listeners.MouseClickListener;
import material.theme.ThemeColors;
import org.kordamp.ikonli.Ikon;

import java.awt.event.InputEvent;

public abstract class PlaybackButton extends MaterialIconButton implements MouseClickListener {
    protected boolean isActive = false;

    public PlaybackButton(Ikon icon) {
        super(icon);
        setAllowMouseAnimation(false);
        setCornerRadius(100);
        setIconSizeRatio(0.8);
        setPadding(ControlButtonConstraints.PADDING);
        setTransparentBackground(true);
        setPreferredSize(ControlButtonConstraints.BUTTON_SIZE);
        addLeftClickListener(this);
    }

    @Override
    public void updateTheme() {
        animateFG(ThemeColors.getTextSecondary());
    }


    @Override
    public void clicked(InputEvent e) {
        isActive = !isActive;
        changeUI(isActive);
        updateTheme();
    }

    public boolean isActive() {
        return isActive;
    }

    public PlaybackButton setActive(boolean active) {
        if (isActive == active)
            return this;
        isActive = active;
        changeUI(isActive);
        updateTheme();
        
        return this;
    }

    public abstract void changeUI(boolean isActive);
}
