package material.window.buttons;

import material.Padding;
import material.component.MaterialIconButton;
import material.listeners.MouseClickListener;

import java.awt.event.InputEvent;

public abstract class CaptionButton extends MaterialIconButton implements MouseClickListener {
    private static final Padding PADDING = new Padding(5);
    public CaptionButton() {
        super();
        setText(null);
        setPadding(PADDING);
        setIconSizeRatio(0.9);
        addLeftClickListener(this);
        setTransparentBackground(true);
        updateTheme();
    }

    @Override
    public abstract void clicked(InputEvent e);
}
