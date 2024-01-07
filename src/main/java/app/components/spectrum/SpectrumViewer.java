package app.components.spectrum;

import app.settings.StartupSettings;
import app.settings.constraints.ComponentParameters;
import material.containers.MaterialPanel;
import net.miginfocom.swing.MigLayout;

public class SpectrumViewer extends MaterialPanel {
    private static SpectrumViewer instance;
    private static final Spectrum spectrum = SharedSpectrumManager.createSpectrum();
    private SpectrumViewer(){
        super();
        setLayout(new MigLayout("fill, inset 0"));
        setElevationDP(ComponentParameters.CONTROL_PANEL_ELEVATION_DP);

        add(spectrum,"alignX center, grow");
        spectrum.setSpectrumType(StartupSettings.SPECTRUM_TYPE);
    }
    public static SpectrumViewer getInstance() {
        if(instance == null)
            instance = new SpectrumViewer();
        return instance;
    }
    public Spectrum getSpectrum(){
        return spectrum;
    }
}
