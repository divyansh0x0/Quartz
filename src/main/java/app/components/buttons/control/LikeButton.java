package app.components.buttons.control;

import material.theme.ThemeColors;

import static app.components.Icons.*;

public class LikeButton extends ControlButton {

    public LikeButton() {
        super(LIKE_OUTLINED);
        if (isActive)
            setIcon(LIKE_FILLED);
        else
            setIcon(LIKE_OUTLINED);
        setToolTipText("Add to Favorites");
    }

    @Override
    public void updateTheme() {
        if (isActive) {
            setIcon(LIKE_FILLED);
            animateFG(ThemeColors.getAccent());
        } else {
            setIcon(LIKE_OUTLINED);
            animateFG(ThemeColors.getTextSecondary());
        }
    }

    @Override
    public void toggled(boolean isActive) {
        if (isActive) {
            setIcon(LIKE_FILLED);
        } else {
            setIcon(LIKE_OUTLINED);
        }
        setActive(isActive);
    }

}
