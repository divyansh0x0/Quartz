package app.components.containers;

import material.component.MaterialComponent;
import material.containers.MaterialPanel;
import material.window.MaterialWindow;
import org.jetbrains.annotations.NotNull;
import material.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class SelectablePanel extends MaterialPanel{
    private final HashMap<MaterialComponent,SelectionHandler> ComponentSelectionHandlers = new HashMap<>();
    private final ArrayList<MaterialComponent> SelectedComponents = new ArrayList<>();
    private boolean isSelectionAllowed = true;
    private MaterialWindow window;
    boolean isKeyListenerAdded = false;

    public SelectablePanel(LayoutManager layout, boolean isDoubleBuffered, boolean isSelectionAllowed) {
        super(layout, isDoubleBuffered);
        this.isSelectionAllowed = isSelectionAllowed;
        if(!isKeyListenerAdded) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new SelectionKeyEventDispatcher(this));
            isKeyListenerAdded = true;
        }
    }

    public SelectablePanel(LayoutManager layout, boolean isDoubleBuffered) {
        this(layout, isDoubleBuffered, true);
    }

    public SelectablePanel(LayoutManager layout) {
        this(layout, true, true);
    }

    public SelectablePanel(boolean isDoubleBuffered) {
        this(null, isDoubleBuffered, true);
    }

    public SelectablePanel() {
        this(null, true, true);
    }


    public boolean isSelectionAllowed() {
        return isSelectionAllowed;
    }

    public void setSelectionAllowed(boolean selectionAllowed) {
        isSelectionAllowed = selectionAllowed;
        if (!selectionAllowed)
            removeAllSelection();
    }

    public void setComponentSelection(MaterialComponent component,boolean isSelected){
        ComponentSelectionHandlers.get(component).setSelected(isSelected);
    }

    public ArrayList<MaterialComponent> getComponentSelectionHandlers() {
        return new ArrayList<>(ComponentSelectionHandlers.keySet());
    }
    public void removeAllSelection() {
        for (SelectionHandler selectionHandler : ComponentSelectionHandlers.values()){
            selectionHandler.setSelected(false);
        }
    }
    private void selectAll() {
        for (SelectionHandler selectionHandler : ComponentSelectionHandlers.values()){
            selectionHandler.setSelected(true);
        }
    }
    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        CompletableFuture.runAsync(()->{
            if (comp instanceof MaterialComponent) {
                if(!ComponentSelectionHandlers.containsKey(comp))
                    ComponentSelectionHandlers.put((MaterialComponent) comp, new SelectionHandler((MaterialComponent) comp));
            }
        });
        super.addImpl(comp, constraints, index);
    }

    @Override
    public void removeAll() {
        super.removeAll();
        this.removeAllSelection();
    }

    @Override
    public void addNotify() {
        super.addNotify();

    }

    /**
     * KEY PRESS HANDLING
     */
    private void handleKeyEvent(KeyEvent e) {
        if(isShowing()) {
            switch (e.getID()) {
                case KeyEvent.KEY_PRESSED -> {
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A) {
                        Log.info("Selecting all components of visible selection pane");
                        selectAll();
                    }
                }
            }
        }
    }


    /**
     * SELECTION HANDLER
     */
    private class SelectionHandler extends MouseAdapter {
        private final MaterialComponent comp;
        private boolean isSelected = false;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
            comp.setSelected(selected);
            if (isSelected)
                SelectedComponents.add(comp);
            else
                SelectedComponents.remove(comp);

        }

        public SelectionHandler(MaterialComponent comp) {
            this.comp = comp;
            MouseListener[] removedListeners = this.comp.getMouseListeners();
            //Remove all mouse listeners so that the selection listeners runs first
            for(MouseListener listener : removedListeners){
                this.comp.removeMouseListener(listener);
            }
            //Add selection listener on top
            this.comp.addMouseListener(this);
            //Add all the listeners that were initially removed
            for(MouseListener listener : removedListeners){
                this.comp.addMouseListener(listener);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isControlDown() && SwingUtilities.isLeftMouseButton(e)) {
                handleSelection(e);
            }
            else if(SwingUtilities.isRightMouseButton(e)){
                Log.info("Selected Components: " + SelectedComponents);

            }
            else{
                removeAllSelection();
            }
        }


        private synchronized void handleSelection(MouseEvent e) {
            Log.info("Handling selection for " + e.getComponent());
            if (isSelectionAllowed()) {
                setSelected(!isSelected);
                e.consume();
            }
        }

    }

    /**
     * SELECTION KEY EVENT DISPATCHER
     */
    class SelectionKeyEventDispatcher implements KeyEventDispatcher {
        private SelectablePanel selectablePanel;
        public SelectionKeyEventDispatcher(SelectablePanel selectablePanel){
            this.selectablePanel = selectablePanel;
        }
        private void traverseKeyPressEvent(@NotNull Component c, @NotNull KeyEvent e) {
            synchronized (c.getTreeLock()) {
                if(c instanceof MaterialWindow) {
                    for (Component component : ((MaterialWindow) c).getComponents()) {
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
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            selectablePanel.handleKeyEvent(e);
            traverseKeyPressEvent(SwingUtilities.getRoot(e.getComponent()), e);
            return false;
        }
    }


}
