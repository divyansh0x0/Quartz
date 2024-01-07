package app.components.spectrum.painter;

import app.components.spectrum.Spectrum;
import app.settings.StartupSettings;
import material.constants.Point2D;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.tools.ColorUtils;
import material.utils.ArrayUtils;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class WavySpectrum extends SpectrumPainter {
    private static final float BAR_HEIGHT_RATIO = 0.9f;
    private static final float MAX_AMBIENT_BLUR_TRANSPARENCY = 0.4f;
    private static final int AMBIENT_BLUR_RADIUS = 40;

    private static final float MINIMUM_HEIGHT = 5;
    public static final BasicStroke BORDER_STROKE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private final Spectrum spectrum;
//    private final LinkedList<BufferedImage> _ambientImageBackgroundBuffer = new LinkedList<>();
    private BufferedImage ambientImage;
    private Color oldAmbientFg;
    private BufferedImage background;
    private final static float[] dist = {0f, 1.0f};
    private boolean sizeChanged;

    public WavySpectrum(Spectrum spectrum) {
        super(spectrum);
        this.spectrum = spectrum;
        this.background = new BufferedImage(100,100,BufferedImage.TYPE_3BYTE_BGR);
        this.spectrum.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                sizeChanged= true;
                clearAmbientBuffer();
                forceAmbientUpdate = true;
                spectrum.repaint();
                sizeChanged= false;
            }
        });
        ThemeManager.getInstance().addThemeListener(this::clearAmbientBuffer);
    }


    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        drawAmbientBlur(g2d);
        drawWaves(g2d);
        g2d.dispose();
        Toolkit.getDefaultToolkit().sync();
    }


    private void drawAmbientBlur(Graphics2D g2d) {
//        if (!_ambientImageBackgroundBuffer.isEmpty()) {
//            BufferedImage temp = _ambientImageBackgroundBuffer.removeFirst();
//            if (temp != null)
//                bufferedImage = temp;
//        }
        createAmbientImage();
        if (ambientImage != null) {
            float ambientTransparency = MAX_AMBIENT_BLUR_TRANSPARENCY;
            if(getMagnitudes() != null)
                ambientTransparency = ((ArrayUtils.sumArray(getMagnitudes())/getMagnitudes().length)/MAX_DECIBELS) * MAX_AMBIENT_BLUR_TRANSPARENCY;

            final int y = (spectrum.getHeight() - ambientImage.getHeight()) / 2;
            final int x = (spectrum.getWidth() - ambientImage.getWidth()) / 2;//-1 * (getWidth() / 2)
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(Math.min(ambientTransparency,1f),0)));
            g2d.drawImage(ambientImage,x,y,null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }


    }
    private Point2D[] reflectionCoordinates = new Point2D[StartupSettings.SPECTRUM_BANDS_NUM * 2];

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
            Path2D path2D = new Path2D.Float();
            boolean isMoved = false;
            float cy = spectrum.getHeight() / 2F;
            float cx = 0;

            path2D.moveTo(cx, cy);
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
                path2D.lineTo(cx, y);
                i++;
            }
            i = 0;
            cx = spectrum.getWidth();
            cy = spectrum.getHeight() / 2F;
            path2D.lineTo(cx, cy);
            while (i < totalPoints) {
                Point2D point2D = reflectionCoordinates[i];
                if (point2D != null) {
                    path2D.lineTo(point2D.getX(), point2D.getY());
                }
                i++;
            }
            path2D.closePath();
            g2d.fill(path2D);

            Color borderColor = ColorUtils.darken(spectrum.getForeground(), 50);
            g2d.setColor(borderColor);
            g2d.setStroke(BORDER_STROKE);
            g2d.draw(path2D);
        }
    }

    protected void createAmbientImage() {
//        try {
        if (oldAmbientFg != null && oldAmbientFg.getRGB() == spectrum.getForeground().getRGB() && !forceAmbientUpdate) {
            return;
        }

        final int w = Math.max(1, spectrum.getWidth() * 2); // Gradient width
        final int h = Math.max(1, spectrum.getHeight()); // Gradient height
        final Color[] colors = {spectrum.getForeground(), ThemeColors.TransparentColor};
        final Rectangle2D ambientBounds = new Rectangle2D.Double(0, 0, w, h);
        BufferedImage tempBufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        RadialGradientPaint paint = new RadialGradientPaint(ambientBounds, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);

        Graphics2D g = (Graphics2D) tempBufferedImage.getGraphics();
        g.setPaint(paint);
        g.fillRect(0, 0, w, h);
        g.dispose();
        oldAmbientFg = spectrum.getForeground();
        forceAmbientUpdate = false;
        this.ambientImage = tempBufferedImage;

    }

    protected void clearAmbientBuffer() {
//        _ambientImageBackgroundBuffer.clear();
    }
}
