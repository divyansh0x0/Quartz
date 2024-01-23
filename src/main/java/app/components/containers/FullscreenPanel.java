package app.components.containers;

import app.audio.AudioData;
import app.components.spectrum.SharedSpectrumManager;
import app.components.spectrum.Spectrum;
import app.components.spectrum.SpectrumType;
import material.containers.MaterialPanel;
import material.theme.ThemeColors;
import net.miginfocom.swing.MigLayout;

import java.awt.*;

public class FullscreenPanel extends MaterialPanel {
    private static FullscreenPanel instance;
    private static final MigLayout LAYOUT = new MigLayout("nogrid,insets 0, fill, flowX");
//    private static BufferedImage BG_IMAGE = FullscreenMode.getRandomImage();
    private static final Spectrum SPECTRUM = SharedSpectrumManager.createSpectrum();
    private static final float AMBIENT_TRANSPARENCY = 0.6f;
    private AudioData currentAudioData;
    private FullscreenPanel() {
        super(LAYOUT);
        setBackground(ThemeColors.TransparentColor);
        SPECTRUM.setSpectrumType(SpectrumType.CIRCULAR);
        SPECTRUM.setAmbientBlurTransparency(AMBIENT_TRANSPARENCY);

        add(SPECTRUM, "alignX center, grow");
    }

    @Override
    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(),getHeight());
    }
    public void setAudio(AudioData currentAudioData) {
        this.currentAudioData = currentAudioData;
    }
    public static FullscreenPanel getInstance() {
        if (instance == null)
            instance = new FullscreenPanel();
        return instance;
    }
}
