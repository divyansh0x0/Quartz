package app.colors.dynamic;

import java.awt.*;

class Dark implements HexColors {
    private static Dark instance;
    protected final Color color1 = new Color(0xE35C5C); //red
    protected final Color color2 = new Color(0x33BFF5); //cyan
    protected final Color color3 = new Color(0x9775c7); //purple
    protected final Color color4 = new Color(0x61E754); //green
    protected final Color color5 = new Color(0xFFCF4E); //yellow
    protected final Color color6 = new Color(0x4D78E5); //blue

    protected final Color orangeColor = new Color(0xC25026); //orange

    public static Dark getInstance() {
        if (instance == null)
            instance = new Dark();
        return instance;
    }


    @Override
    public Color[] getColorArray() {
        return new Color[]{color1, color2, color3, color4, color5, color6,orangeColor};
    }
    @Override
    public Color getYellowAlternative(){
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
     * @return A variant of yello
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
