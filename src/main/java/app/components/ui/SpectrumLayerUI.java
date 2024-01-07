package app.components.ui;

import app.components.audio.AudioInfoViewer;
import app.components.spectrum.SharedSpectrumManager;
import app.components.spectrum.Spectrum;
import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.*;

public class SpectrumLayerUI extends LayerUI<AudioInfoViewer> {
    private float ALPHA = 0.4f;

    public SpectrumLayerUI() {
        this(0.4f);
    }
    private SpectrumLayerUI(float alpha){
        this.ALPHA = alpha;
    }
    Spectrum spectrum;

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        if(spectrum == null)
            spectrum = SharedSpectrumManager.createSpectrum();
        int w = c.getWidth();
        int h = c.getHeight();
        Graphics2D sg2d = (Graphics2D) g.create();
        sg2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ALPHA));
        spectrum.setBounds(0,0,w,h);
        spectrum.paint(sg2d);
        g.dispose();
    }
}
