package material.window;

import material.animation.MaterialFixedTimer;
import material.utils.OsInfo;

import javax.swing.*;
import java.awt.*;

public class MousePointer {
    private static final Point mouseLocation = new Point();
    static {
        new MaterialFixedTimer((float) 1000 /OsInfo.getRefreshRate()) {
            @Override
            public void tick(float deltaMillis) {
                mouseLocation.setLocation(MouseInfo.getPointerInfo().getLocation());
            }
        }.start();
    }
    public static Point getPointerLocation(){
        return mouseLocation;
    }
}
