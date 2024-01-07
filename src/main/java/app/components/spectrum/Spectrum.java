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
import java.awt.image.BufferedImage;

public class Spectrum extends MaterialComponent {
    private SpectrumType spectrumType = StartupSettings.SPECTRUM_TYPE;
    private static final int INTERVAL = 1000;

    private BufferedImage imgForAvgColor;
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
//    @Override
//    public void animateFG(Color to) {
////        if (getForeground() != null && getForeground().getRGB() != to.getRGB()) {
////            if (fgAnimation == null || fgAnimation.isCompleted()) {
////                fgAnimation = new ColorAnimation(this, ColorAnimationType.FOREGROUND, getForeground(), to, INTERVAL);
////                fgAnimation.onCompleted(() -> {
////                    setBackground(to);
////                    repaint();
////                });
////                fgAnimation.start();
////            } else {
////                fgAnimation.stop();
////                fgAnimation = null;
////                animateFG(to);
////            }
////        } else
////            setForeground(to);
//        AnimationLibrary.animateForeground();
//    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        activePainter.paint(g);
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
