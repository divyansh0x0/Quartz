package material.component;

import material.utils.Log;
import org.kordamp.ikonli.Ikon;

import java.awt.*;

public class MaterialMenuItem extends MaterialIconButton{
    public static final int HEIGHT = 48;
    public static final String CONSTRAINTS = "gapY 0,growX, h " + HEIGHT + "!";
    private Color oldBg;
    public MaterialMenuItem(Ikon icon, String text) {
        super(icon, text);
        setTransparentBackground(true);
        setIconSizeRatio(0.7);
    }

    @Override
    protected void animateMouseExit() {
        super.animateMouseExit();
    }

    @Override
    protected void animateMouseEnter() {
        super.animateMouseEnter();
    }

    public MaterialMenuItem(String str) {
        this(null, str);
    }
    public void resetColor(){
        if(oldBg != null)
            setBackground(oldBg);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
