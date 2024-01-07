package app.main;

import app.audio.player.FrostPlayerController;
import material.component.MaterialComponent;
import material.window.MaterialWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class FrostKeyEventDispatcher implements KeyEventDispatcher {
    private final MaterialWindow materialWindow;
    public FrostKeyEventDispatcher(MaterialWindow materialWindow) {
        this.materialWindow = materialWindow;
    }
    private void traverseKeyPressEvent(@NotNull JComponent c, @NotNull KeyEvent e) {
        synchronized (c.getTreeLock()) {
            for (Component component : c.getComponents()) {
                if(e.isConsumed())
                    break;
                if (component instanceof JComponent) {
                    if (component instanceof MaterialComponent)
                        ((MaterialComponent) component).handleKeyPressEvent(e);
                    if (((JComponent) component).getComponents().length > 0) {
                        traverseKeyPressEvent((JComponent) component, e);
                    }
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        Frost.getInstance().handleKeyEvent(e);
        traverseKeyPressEvent(materialWindow.getRootPanel(),e);
        return false;
    }
}
