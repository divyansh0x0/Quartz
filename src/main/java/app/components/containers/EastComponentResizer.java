package app.components.containers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EastComponentResizer extends MouseAdapter {
    private int resizeGripSize = 5;
    //    private Insets edgeInsets = new Insets(0, 0, 0, 0);
    private boolean changeCursor = true;
    private boolean autoLayout = false;

    private Class destinationClass;
    private Component destinationComponent;
    private Component destination;
    private Component source;

    private Point pressed;
    private Dimension size;

    private Cursor originalCursor;
    private boolean autoscrolls;
    private boolean potentialDrag;


    /**
     * Constructor for moving individual components. The components must be
     * regisetered using the registerComponent() method.
     */
    public EastComponentResizer() {
    }
    /**
     * Remove listeners from the specified component
     *
     * @param components the component the listeners are removed from
     */
    public void deregisterComponent(Component... components) {
        for (Component component : components)
            component.removeMouseListener(this);
    }

    /**
     * Add the required listeners to the specified component
     *
     * @param components the component the listeners are added to
     */
    public void registerComponent(Component... components) {
        for (Component component : components) {
            component.addMouseListener(this);
            component.addMouseMotionListener(this);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(changeCursor && originalCursor != null && !originalCursor.equals(source.getCursor())) {
            source.setCursor(originalCursor);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        source = e.getComponent();
//        int x = source.getSize().width - resizeGripSize;
//        int y = source.getSize().height - resizeGripSize;
//        Rectangle r = new Rectangle(x, 0, resizeGripSize, source.getHeight());
//        Log.success(r);
//
//        if (r.contains(e.getPoint())) {
//            if (changeCursor)
//                source.setCursor(originalCursor);
//        } else {
//            if (changeCursor) {
//                originalCursor = source.getCursor();
//                source.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
//            }
//        }
        source = e.getComponent();
        int x = source.getSize().width - resizeGripSize;
        Rectangle r = new Rectangle(x, 0, resizeGripSize, source.getHeight());

        if (r.contains(e.getPoint())) {
            if (changeCursor) {
                if(!source.getCursor().getName().equalsIgnoreCase(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR).getName()))
                    originalCursor = source.getCursor();
                source.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            }
        }else{
            if(changeCursor && originalCursor != null && !originalCursor.equals(source.getCursor()))
                source.setCursor(originalCursor);
        }
    }

    /**
     * Get the snap size
     *
     * @return the snap size
     * <p>
     * <p>
     * /**
     * Setup the variables used to control the moving of the component:
     * <p>
     * source - the source component of the mouse event
     * destination - the component that will ultimately be moved
     * pressed - the Point where the mouse was pressed in the destination
     * component coordinates.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        source = e.getComponent();
        int x = source.getSize().width - resizeGripSize;
        Rectangle r = new Rectangle(x, 0, resizeGripSize, source.getHeight());

        if (r.contains(e.getPoint()))
            setupForDragging(e);
    }

    private void setupForDragging(MouseEvent e) {
        source.addMouseMotionListener(this);
        potentialDrag = true;

        //  Determine the component that will ultimately be moved

        if (destinationComponent != null) {
            destination = destinationComponent;
        } else if (destinationClass == null) {
            destination = source;
        } else  //  forward events to destination component
        {
            destination = SwingUtilities.getAncestorOfClass(destinationClass, source);
        }
        pressed = e.getLocationOnScreen();
        size = destination.getSize();

        if (changeCursor) {
            if(!source.getCursor().getName().equalsIgnoreCase(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR).getName()))
                originalCursor = source.getCursor();
            source.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        }

        //  Making sure autoscrolls is false will allow for smoother dragging of
        //  individual components

        if (destination instanceof JComponent) {
            JComponent jc = (JComponent) destination;
            autoscrolls = jc.getAutoscrolls();
            jc.setAutoscrolls(false);
        }
    }

    /**
     * Move the component to its new location. The dragged Point must be in
     * the destination coordinates.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (potentialDrag) {
            Point dragged = e.getLocationOnScreen();
            int dragX = getDragDistance(dragged.x, pressed.x);

            int sizeX = size.width + dragX;
            int sizeY = size.height;

            Dimension d = getBoundingSize(destination);

            while (sizeX + destination.getSize().width > d.width)
                sizeX -= 1;

            //  Adjustments are finished, move the component

            destination.setPreferredSize(new Dimension(sizeX, sizeY));
            SwingUtilities.invokeLater(destination::revalidate);
        }
    }

    /*
     *  Determine how far the mouse has moved from where dragging started
     *  (Assume drag direction is down and right for positive drag distance)
     */
    private int getDragDistance(int larger, int smaller) {
        int snapSize = resizeGripSize;
        int halfway = snapSize / 2;
        int drag = larger - smaller;
        drag += (drag < 0) ? -halfway : halfway;
        drag = (drag / snapSize) * snapSize;

        return drag;
    }

    /*
     *  Get the bounds of the parent of the dragged component.
     */
    private Dimension getBoundingSize(Component source) {
        if (source instanceof Window) {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle bounds = env.getMaximumWindowBounds();
            return new Dimension(bounds.width, bounds.height);
        } else {
            return source.getParent().getSize();
        }
    }

    /**
     * Restore the original state of the Component
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (!potentialDrag) return;

        source.removeMouseMotionListener(this);
        potentialDrag = false;

        if (changeCursor)
            source.setCursor(originalCursor);

        originalCursor = null;

        if (destination instanceof JComponent) {
            ((JComponent) destination).setAutoscrolls(autoscrolls);
        }

        //  Layout the components on the parent container

        if (autoLayout) {
            if (destination instanceof JComponent) {
                ((JComponent) destination).revalidate();
            } else {
                destination.validate();
            }
        }
    }
}
