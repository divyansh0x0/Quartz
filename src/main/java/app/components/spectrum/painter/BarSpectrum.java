package app.components.spectrum.painter;

import app.components.spectrum.Spectrum;
import material.theme.ThemeColors;
import material.theme.ThemeListener;
import material.theme.ThemeManager;
import material.utils.Log;
import material.utils.filters.FastGaussianBlur;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class BarSpectrum extends SpectrumPainter {
    private static final double BAR_HEIGHT_RATIO = 0.9d;
    private static final float AMBIENT_BLUR_TRANSPARENCY = 0.1f;
    private static final int AMBIENT_BLUR_RADIUS = 50;
    private static final int MINIMUM_HEIGHT = 2;
    private final Spectrum spectrum;

    private final LinkedList<BufferedImage> _ambientImageBackgroundBuffer = new LinkedList<>();
    private BufferedImage bufferedImage;
    private Color oldAmbientFg;
    private float[] reversedMag;

    public BarSpectrum(Spectrum spectrum) {
        super(spectrum);
        this.spectrum = spectrum;

        ThemeManager.getInstance().addThemeListener((ThemeListener) this::clearAmbientBuffer);
    }


    public void paint(Graphics g) {
        try {
            Graphics2D g2d = (Graphics2D) g.create();
            drawAmbientBlur((Graphics2D) g2d.create());


            //Drawing visualizer
            if (getMagnitudes() != null)
//                drawBars(g2d, getMirroredMagnitudes());

            //Releasing graphic resources
            Toolkit.getDefaultToolkit().sync();
            g2d.dispose();
        } catch (Exception e) {
            Log.error("Error while painting spectrum: " + e);
            e.printStackTrace();
        }
    }


    private void drawAmbientBlur(Graphics2D g2d) {
        if (!_ambientImageBackgroundBuffer.isEmpty()) {
            BufferedImage temp = _ambientImageBackgroundBuffer.removeFirst();
            if (temp != null)
                bufferedImage = temp;
        }
        if (bufferedImage != null) {
            final int y = (spectrum.getHeight() - bufferedImage.getHeight()) / 2;
            final int x = (spectrum.getWidth() - bufferedImage.getWidth()) / 2;//-1 * (getWidth() / 2)
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, AMBIENT_BLUR_TRANSPARENCY));
            g2d.drawImage(bufferedImage, null, x, y);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
        g2d.dispose();
        Toolkit.getDefaultToolkit().sync();
    }


    private void drawBars(Graphics2D g2d, float[] magnitudes) {
        double x, y;
        final double barNumber = magnitudes.length;
        final double width = spectrum.getWidth();
        final double maxSize = (int) (spectrum.getHeight() * BAR_HEIGHT_RATIO);
        final double barWidth = width / barNumber;
        //Horizontal bar in the center
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int i = 0; // Index of bars
        g2d.setColor(spectrum.getForeground());
        g2d.setStroke(new BasicStroke(1f));

        g2d.fillRect(0, (spectrum.getHeight() - MINIMUM_HEIGHT) / 2, spectrum.getWidth(), MINIMUM_HEIGHT);
        while (i < barNumber) {
            double ratio = magnitudes[i] / MAX_DECIBELS;
            double height = (ratio * maxSize);
            x = ((int) (i * barWidth));
            y = ((spectrum.getHeight() - height) / 2D);
//                k += Math.ceil(magnitudes.length / (barNumber));
            Rectangle2D bar = new Rectangle2D.Double(Math.ceil(x), Math.ceil(y), Math.ceil(barWidth), height);
            g2d.fill(bar);
            i++;

        }
    }

    protected void createAmbientImage() {
        try {
            if (oldAmbientFg != null && oldAmbientFg.getRGB() == spectrum.getForeground().getRGB() && !forceAmbientUpdate) {
                return;
            }
            final int w = Math.max(1, (int) (spectrum.getWidth() * 2)); //Gradient width
            final int h = Math.max(1, spectrum.getHeight());//Gradient height
            final Color[] colors = {spectrum.getForeground(), ThemeColors.TransparentColor};
            final float[] dist = {0f, 1.0f};
            final FastGaussianBlur fastGaussianBlur = new FastGaussianBlur(AMBIENT_BLUR_RADIUS);
            final Rectangle2D ambientBounds = new Rectangle2D.Double(0, 0, w, h);
            BufferedImage tempBufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            RadialGradientPaint paint = new RadialGradientPaint(ambientBounds, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);

            Graphics2D g = (Graphics2D) tempBufferedImage.getGraphics();
            g.setPaint(paint);
            g.fillRect(0, 0, w, h);
            g.dispose();

            _ambientImageBackgroundBuffer.addLast(fastGaussianBlur.filter(tempBufferedImage, tempBufferedImage));
            oldAmbientFg = spectrum.getForeground();
            forceAmbientUpdate = false;
        } catch (Exception e) {
            Log.error(e.toString());
            Log.warn("skipping");
        }
    }

    protected void clearAmbientBuffer() {
        _ambientImageBackgroundBuffer.clear();
    }
}
