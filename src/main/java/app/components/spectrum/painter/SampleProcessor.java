package app.components.spectrum.painter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class SampleProcessor {
    /**
     * Magnitudes are mirrored like 1,2,3,3,2,1
     *
     * @param magnitudes  These magnitudes are mirrored
     */
    public static float @NotNull [] mirrorMagnitudes(float @NotNull [] magnitudes, float @Nullable [] arrayToFill, boolean isSorted){
        float[] copy = new float[magnitudes.length];
        System.arraycopy(magnitudes,0,copy,0,copy.length);
        if(arrayToFill == null){
            arrayToFill = new float[magnitudes.length * 2];
        }
        if(isSorted){
            Arrays.sort(copy);
        }
        System.arraycopy(copy,0,arrayToFill,copy.length,copy.length);
        reverse(copy);
        System.arraycopy(copy,0,arrayToFill,0,copy.length);


        return arrayToFill;
    }

    public static void calculateAvgMagnitudes(float[] oldMags, float[] newMags, float[] arrToFill) {
        if (newMags == null || newMags.length == 0)
            return;
        for (int i = 0; i < arrToFill.length; i++) {
            float magnitude1 = 0f;
            float magnitude2 = 0f;
            if (i < oldMags.length)
                magnitude1 = oldMags[i];
            if (i < newMags.length)
                magnitude2 = newMags[i];
            arrToFill[i] = (magnitude1 + magnitude2) * 0.5f;
        }
    }
    public static void  reverse(float[] magnitudes)
    {
        int n = magnitudes.length;
        float t;
        for (int i = 0; i < n / 2; i++) {
            t = magnitudes[i];
            magnitudes[i] = magnitudes[n - i - 1];
            magnitudes[n - i - 1] = t;
        }
    }

}
