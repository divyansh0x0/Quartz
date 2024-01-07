package app.colors.dynamic;

import java.awt.*;

interface HexColors {
    Color[] getColorArray();

    Color getYellowAlternative();

    /**
     * @return A variant of red
     */
    Color getRed();

    /**
     * @return A variant of cyan
     */
    Color getCyan();

    /**
     * @return A variant of purple
     */
    Color getPurple();

    /**
     * @return A variant of green
     */
    Color getGreen();

    /**
     * @return A variant of yello
     */
    Color getYellow();

    /**
     * @return A variant of blue
     */
     Color getBlue();
}
