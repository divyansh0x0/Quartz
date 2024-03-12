package app.components.spectrum.painter;

import app.components.spectrum.Spectrum;
import app.settings.StartupSettings;
import material.constants.Point2D;
import material.tools.ColorUtils;

import java.awt.*;
import java.awt.geom.Path2D;

public class WavySpectrum extends SpectrumPainter {
    private static final float BAR_HEIGHT_RATIO = 0.9f;
    private static final float MAX_AMBIENT_BLUR_TRANSPARENCY = 0.4f;
    private static final int AMBIENT_BLUR_RADIUS = 40;

    private static final float MINIMUM_HEIGHT = 5;
    public static final BasicStroke BORDER_STROKE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private final Spectrum spectrum;
//    private final LinkedList<BufferedImage> _ambientImageBackgroundBuffer = new LinkedList<>();

    private Point2D[] reflectionCoordinates = new Point2D[StartupSettings.SPECTRUM_BANDS_NUM * 2];
    private final Path2D wavePath2D = new Path2D.Float();

    public WavySpectrum(Spectrum spectrum) {
        super(spectrum);
        this.spectrum = spectrum;
//        this.spectrum.addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                sizeChanged= true;
//                clearAmbientBuffer();
//                forceAmbientUpdate = true;
//                spectrum.repaint();
//                sizeChanged= false;
//            }
//        });
//        ThemeManager.getInstance().addThemeListener(this::clearAmbientBuffer);
    }


    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
//        drawAmbientBlur(g2d);
        drawWaves(g2d);
        g2d.dispose();
        Toolkit.getDefaultToolkit().sync();
    }


//    private void drawAmbientBlur(Graphics2D g2d) {
//        if (!_ambientImageBackgroundBuffer.isEmpty()) {
//            BufferedImage temp = _ambientImageBackgroundBuffer.removeFirst();
//            if (temp != null)
//                bufferedImage = temp;
//        }
//        createAmbientImage();
//        if (ambientImage != null) {
//            float ambientTransparency = MAX_AMBIENT_BLUR_TRANSPARENCY;
//            if(getMagnitudes() != null)
//                ambientTransparency = ((ArrayUtils.sumArray(getMagnitudes())/getMagnitudes().length)/MAX_DECIBELS) * MAX_AMBIENT_BLUR_TRANSPARENCY;
//
//            final int y = (spectrum.getHeight() - ambientImage.getHeight()) / 2;
//            final int x = (spectrum.getWidth() - ambientImage.getWidth()) / 2;//-1 * (getWidth() / 2)
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(Math.min(ambientTransparency,1f),0)));
//            g2d.drawImage(ambientImage,x,y,null);
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//        }
//    }
    private void drawWaves(Graphics2D g2d) {
        float[] magnitudes = getMirroredMagnitudes();
        if (magnitudes != null) {
            if(reflectionCoordinates.length != magnitudes.length)
                reflectionCoordinates = new Point2D[magnitudes.length];
            final float width = spectrum.getWidth();
            final float maxSize = (int) (spectrum.getHeight() * BAR_HEIGHT_RATIO);
            final int totalPoints = magnitudes.length;
            final float gap = width / (totalPoints);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(spectrum.getForeground());

            int i = 1; // Index of bars
            float cy = spectrum.getHeight() / 2F;
            float cx = 0;

            wavePath2D.moveTo(cx, cy);
            while (i < totalPoints) {
                float ratio = magnitudes[i] / MAX_DECIBELS;
                float height = Math.max((ratio * maxSize), MINIMUM_HEIGHT) * 0.5F;
                cx = i * gap;
                float y = cy - height;
                int index = totalPoints - i - 1;
                if(reflectionCoordinates[index] == null)
                    reflectionCoordinates[index] = new Point2D(cx, y + height * 2);
                else
                    reflectionCoordinates[index].setCoordinates(cx,y + height * 2);
                wavePath2D.lineTo(cx, y);
                i++;
            }
            i = 0;
            cx = spectrum.getWidth();
            cy = spectrum.getHeight() / 2F;
            wavePath2D.lineTo(cx, cy);
            while (i < totalPoints) {
                Point2D point2D = reflectionCoordinates[i];
                if (point2D != null) {
                    wavePath2D.lineTo(point2D.getX(), point2D.getY());
                }
                i++;
            }
            wavePath2D.closePath();
            g2d.fill(wavePath2D);
            wavePath2D.reset();
            Color borderColor = ColorUtils.darken(spectrum.getForeground(), 50);
            g2d.setColor(borderColor);
            g2d.setStroke(BORDER_STROKE);
            g2d.draw(wavePath2D);
        }
    }
    private final static float[] radialPaintFractions = {0f, 1.0f};


//    protected void clearAmbientBuffer() {
//        _ambientImageBackgroundBuffer.clear();
//    }
}
