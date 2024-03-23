package app.components.spectrum;

import app.components.spectrum.painter.BarSpectrum;
import app.components.spectrum.painter.RingSpectrum;
import app.components.spectrum.painter.SpectrumPainter;
import app.components.spectrum.painter.WavySpectrum;
import app.settings.StartupSettings;
import material.component.MaterialComponent;
import material.theme.ThemeColors;
import org.jetbrains.annotations.Range;

import java.awt.*;

public class Spectrum extends MaterialComponent {
    private SpectrumType spectrumType = StartupSettings.SPECTRUM_TYPE;
    private final BarSpectrum barSpectrum = new BarSpectrum(this);
    private final WavySpectrum wavySpectrum = new WavySpectrum(this);
    private final RingSpectrum ringSpectrum = new RingSpectrum(this);
    private SpectrumPainter activePainter;

    protected Spectrum(SpectrumType type) {
        setOpaque(false);
        setSpectrumType(type);
    }

    protected Spectrum() {
        this(StartupSettings.SPECTRUM_TYPE);
    }

    @Override
    public void updateTheme() {
        animateFG(ThemeColors.getAccent());
        repaint();
    }
    @Override
    protected void animateMouseEnter() {

    }

    @Override
    protected void animateMouseExit() {

    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        activePainter.paintInVolatileImage(g);
    }


    public void setMagnitudes(float[] magnitudes) {
        if(this.isVisible()) {
            activePainter.setMagnitudes(magnitudes);
            repaint();
        }
    }

    public void setSpectrumType(SpectrumType spectrumType) {
        this.spectrumType = spectrumType;
        switch (spectrumType) {
            case BAR -> activePainter = barSpectrum;
            case WAVY -> activePainter = wavySpectrum;
            case CIRCULAR -> activePainter = ringSpectrum;
        }
        repaint();
    }

    public SpectrumType getSpectrumType() {
        return spectrumType;
    }

    private Color avgColor;
    public void setAmbientBlurTransparency(@Range(from = 0, to = 1) float percentage){

    }
}
