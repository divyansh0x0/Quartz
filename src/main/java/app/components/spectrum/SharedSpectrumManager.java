package app.components.spectrum;

import java.util.ArrayList;

public class SharedSpectrumManager {
    public static ArrayList<Spectrum> spectrumList = new ArrayList<>();

    public static synchronized Spectrum createSpectrum(){
        Spectrum spectrum = new Spectrum();
        spectrumList.add(spectrum);
        return spectrum;
    }

    public static void setMagnitudes(float[] magnitudes){
        for(Spectrum spectrum : spectrumList){
            spectrum.setMagnitudes(magnitudes);
        }
    }
    public static synchronized void removeSpectrum(Spectrum spectrum){
        spectrumList.remove(spectrum);
    }
    public static boolean isMagnitudesRequired(){
        for(Spectrum spectrum : spectrumList)
            if(spectrum.isShowing())
                return true;
        return false;
    }
}
