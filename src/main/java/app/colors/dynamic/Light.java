package app.colors.dynamic;

import java.awt.*;

class Light implements HexColors {
    private static Light instance;
    protected final Color color1 = new Color(0x960E20); //red
    protected final Color color2 = new Color(0x15627E); //cyan
    protected final Color color3 = new Color(0x54129a); //purple
    protected final Color color4 = new Color(0x296021); //green
    protected final Color color5 = new Color(0x88660F); //yellow
    protected final Color color6 = new Color(0x133394); //blue
    protected final Color orangeColor = new Color(0x7C2C00); /// Use orange instead of yellow

    public static Light getInstance() {
        if (instance == null)
            instance = new Light();
        return instance;
    }

    @Override
    public Color[] getColorArray() {
        return new Color[]{color1, color2, color3, color4, color5, color6,orangeColor};
    }

    @Override
    public Color getYellowAlternative() {
        return orangeColor;
    }


//    ______________________________________________________________________________________________________
//
//                                              GETTERS
//
//    ______________________________________________________________________________________________________
    /**
     * @return A variant of red
     */
    public Color getRed() {
        return color1;
    }
    /**
     * @return A variant of cyan
     */
    public Color getCyan() {
        return color2;
    }
    /**
     * @return A variant of purple
     */
    public Color getPurple() {
        return color3;
    }
    /**
     * @return A variant of green
     */
    public Color getGreen() {
        return color4;
    }
    /**
     * @return A variant of yellow
     */
    public Color getYellow() {
        return color5;
    }

    /**
     * @return A variant of blue
     */
    public Color getBlue() {
        return color6;
    }
}