package app.components.spectrum.painter;

import app.components.spectrum.Spectrum;
import app.settings.StartupSettings;
import com.jhlabs.image.GaussianFilter;
import material.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public abstract class SpectrumPainter {
    private final GaussianFilter gaussianFilter = new GaussianFilter(20);
    private final Spectrum spectrum;
    public final int MAX_DECIBELS = 110;
    protected boolean forceAmbientUpdate = false;
    private float[] processedMagnitudes;
    private final Object lock = new Object();
    private boolean isProcessing = false;
    private float[] magnitudes;
    private static Random random = new Random();
    private float timePassed = 0.0f;

    public Object getSpectrumLock() {
        return lock;
    }

    public SpectrumPainter(Spectrum spectrum) {
        this.spectrum = spectrum;
        SharedPainterTimer.getInstance().add(this);
        this.spectrum.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                clearAmbientBuffer();
                forceAmbientUpdate = true;
                spectrum.repaint();
            }
        });

    }

    protected abstract void clearAmbientBuffer();

    public abstract void paint(Graphics g);

    public synchronized void setMagnitudes(float[] magnitudes) {
        //            Arrays.sort(magnitudes);
        this.magnitudes = magnitudes;
        if (this.processedMagnitudes == null)
            this.processedMagnitudes = new float[magnitudes.length];


    }

    public final int getNumOfSpectrumBands() {
        return StartupSettings.SPECTRUM_BANDS_NUM;
    }

    void tick(float dt) {
        try {
//            processedMagnitudes = magnitudes;
            if (processedMagnitudes != null) {
                this.timePassed += dt;
                SampleProcessor.calculateAvgMagnitudes(processedMagnitudes, magnitudes, processedMagnitudes);
                SwingUtilities.invokeLater(spectrum::repaint);
            }
        } catch (Exception e) {
            Log.error(e);
        }
    }

    ;

    abstract void createAmbientImage();

    protected float[] getMagnitudes() {
        if (processedMagnitudes != null) {
            float[] magnitudesToReturn = new float[processedMagnitudes.length];
            System.arraycopy(processedMagnitudes, 0, magnitudesToReturn, 0, magnitudesToReturn.length);
            return magnitudesToReturn;
        }
        return null;
    }

    protected float[] getMirroredMagnitudes() {
        if (processedMagnitudes == null)
            return null;
        return SampleProcessor.mirrorMagnitudes(processedMagnitudes, null, false);
    }

    private final int maxCircleSize = 400;

    void setBackground(BufferedImage background, int height, int width) {

        float dx = (float) (Math.sin(2 * timePassed) + Math.sin(Math.PI * timePassed));
        float dy = (float) (Math.sin(3.312 * timePassed) + Math.sin(Math.PI * timePassed));
        int w = spectrum.getWidth(), h = spectrum.getHeight();
        if (w > 0 && h > 0) {
            if (background == null || background.getHeight() != height || background.getWidth() != width)
                background = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
            background.setAccelerationPriority(1f);
            Graphics2D g2d = (Graphics2D) background.getGraphics();
            g2d.setColor(spectrum.getBackground());
            g2d.fillRect(0, 0, w, h);
            g2d.setColor(spectrum.getForeground());
            g2d.dispose();
            gaussianFilter.filter(background, background);
        }
    }
}