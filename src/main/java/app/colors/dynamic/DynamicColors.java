package app.colors.dynamic;

import material.theme.enums.ThemeType;
import org.jetbrains.annotations.NotNull;
import material.utils.ColorConverter;
import material.tools.ColorUtils;

import java.awt.*;

public class DynamicColors {
    private static Color colorDark = new Color(0x0);
    private static Color colorLight = new Color(0xffffff);
//    private static float[] hslDark = {0,1,5.6f};
//    private static float[] hslLight = {0,1,5.6f};

    /**
     * Find the closest color from predefined 6 colors based on active theme.
     *
     * @return Closest color from predefined 6 colors
     */
    public static Color getClosestColor(Color color, ThemeType themeType) {
        if(color != null) {
            HexColors colorable;
            if (themeType == ThemeType.Light)
                colorable = Light.getInstance();
            else
                colorable = Dark.getInstance();


            if (color.equals(Color.BLACK))
                color = ColorUtils.lighten(color, 2);
            float[] lab1 = getLab(color);
            double lastColorDiff = 2000D;
            Color closestColor = Color.BLACK;

            for (Color c : colorable.getColorArray()) {
                float[] lab2 = getLab(c);
                double diff = getLabColorDifference(lab1, lab2);
                if (diff < lastColorDiff) {
                    closestColor = c;
                    lastColorDiff = diff;
                }
            }
            return closestColor;
        }
        return null;
    }

    /**
     * Get CIELAB colorspace
     *
     * @return A float array containing L*, a* and b* values
     */
    private static float @NotNull [] getLab(@NotNull Color color) {
        float[] xyz = ColorConverter.CIE10_D65;
        return ColorConverter.RGBtoLAB(color.getRed(), color.getGreen(), color.getBlue(), xyz);
    }

    /**
     * Get color differnce between 2 CIELAB colorspace model
     *
     * @param a CIELAB color 1
     * @param b CIELAB color 2
     * @return floating point difference between a and b color from 0 to 128;
     */
    private static double getLabColorDifference(float @NotNull [] a, float @NotNull [] b) {
        float k_1 = 0.045f;//chroma
        //hue - weight controls the impact of hue (H) in the color difference calculation.
        // A higher value of k_2 will give more importance to hue differences,
        // while a lower value will diminish its effect. The typical range for k_2 is between 0.01 and 0.1, with 0.015 being a common value.
        float k_2 = 0.1f;
        float k_L = 1f;//luminescence


        float k_C = 1f,  k_H = 1;

        float l_1 = a[0], a_1 = a[1], b_1 = a[2];
        float l_2 = b[0], a_2 = b[1], b_2 = b[2];

        float delta_a = a_1 - a_2;
        float delta_b = b_1 - b_2;

        double c_1 = Math.sqrt((a_1 * a_1) + (b_1 * b_1));
        double c_2 = Math.sqrt((a_2 * a_2) + (b_2 * b_2));

        float delta_L = l_1-l_2;
        double delta_C = c_1 - c_2;

        double delta_H2 = (delta_a * delta_a)+(delta_b * delta_b)-(delta_C * delta_C);

        float s_L = 1;
        double s_C = 1 + k_1 * c_1;
        double s_H = 1 + k_2 * c_1;

        double composite_L = Math.pow ((delta_L / (k_L * s_L)),2);
        double composite_C = Math.pow((delta_C / (k_C * s_C)),2);
        double composite_H = Math.pow((delta_H2 / (k_H * s_H)),2);
        return Math.sqrt(composite_L + composite_C + composite_H);
    }
}
