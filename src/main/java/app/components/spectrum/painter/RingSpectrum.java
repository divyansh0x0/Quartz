package app.components.spectrum.painter;

import app.components.spectrum.Spectrum;
import material.theme.ThemeColors;
import material.utils.Log;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RingSpectrum extends SpectrumPainter {
    private static final double BAR_HEIGHT_RATIO = 0.9d;
    private static final float AMBIENT_BLUR_TRANSPARENCY = 0.1f;
    private static final int AMBIENT_BLUR_RADIUS = 50;
    private static final int MINIMUM_HEIGHT = 2;
    private final Spectrum spectrum;

//    private final LinkedList<BufferedImage> _ambientImageBackgroundBuffer = new LinkedList<>();
    private BufferedImage bufferedImage;
    private Color oldAmbientFg;

    public RingSpectrum(Spectrum spectrum) {
        super(spectrum);
        this.spectrum = spectrum;

//        ThemeManager.getInstance().addThemeListener((ThemeListener) this::clearAmbientBuffer);
    }


    public void paint(Graphics g) {
        try {
            Graphics2D g2d = (Graphics2D) g.create();
//            drawAmbientBlur((Graphics2D) g2d.create());

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            //Drawing visualizer
            if(getMagnitudes() != null)
                drawCircularVisualizer(g2d, super.getMagnitudes());

            //Releasing graphic resources
            Toolkit.getDefaultToolkit().sync();
            g2d.dispose();
        } catch (Exception e) {
            Log.error("Error while painting spectrum: " + e);
            e.printStackTrace();
        }
    }


//    private void drawAmbientBlur(Graphics2D g2d) {
//        if (!_ambientImageBackgroundBuffer.isEmpty()) {
//            BufferedImage temp = _ambientImageBackgroundBuffer.removeFirst();
//            if (temp != null)
//                bufferedImage = temp;
//        }
//        if (bufferedImage != null) {
//            final int y = (spectrum.getHeight() - bufferedImage.getHeight()) / 2;
//            final int x = (spectrum.getWidth() - bufferedImage.getWidth()) / 2;//-1 * (getWidth() / 2)
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, AMBIENT_BLUR_TRANSPARENCY));
//            g2d.drawImage(bufferedImage, null, x, y);
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
//        }
//        g2d.dispose();
//        Toolkit.getDefaultToolkit().sync();
//    }


    private void drawCircularVisualizer(Graphics2D g2d, float[] magnitudes) {
        // Define visualizer properties
        int centerX = spectrum.getWidth() / 2;    // X-coordinate of the center of the visualizer
        int centerY = spectrum.getHeight()/ 2;   // Y-coordinate of the center of the visualizer
        int maxRadius = (int) (Math.min(centerX, centerY) * 0.8);  // Maximum radius based on available space
        int numBars = magnitudes.length;   // Number of bars
        int barWidth = (int) Math.ceil((2 * Math.PI * maxRadius) / numBars); // Dynamic bar width

        // Set the rendering properties
        g2d.setColor(ThemeColors.getAccent());
        g2d.setStroke(new BasicStroke(barWidth));

        // Calculate the angle increment for each bar
        double angleIncrement = (2 * Math.PI) / numBars;

        // Iterate through the magnitudes and draw the circular visualizer
        for (int i = 0; i < numBars; i++) {
            // Calculate the angle for the current bar
            double angle = i * angleIncrement;

            // Calculate the magnitude-based height for the current bar
            float magnitude = magnitudes[i];
            int height = (int) (magnitude * maxRadius / 150.0); // Adjust the denominator based on the maximum magnitude

            // Calculate the starting and ending coordinates for the current bar
            double x1 = centerX + (maxRadius - height) * Math.cos(angle - Math.PI / 2);
            double y1 = centerY + (maxRadius - height) * Math.sin(angle - Math.PI / 2);
            double x2 = centerX + maxRadius * Math.cos(angle - Math.PI / 2);
            double y2 = centerY + maxRadius * Math.sin(angle - Math.PI / 2);

            // Draw the bar
            g2d.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }
    }




//
//    protected void createAmbientImage() {
//        try {
//            if (oldAmbientFg != null && oldAmbientFg.getRGB() == spectrum.getForeground().getRGB() && !forceAmbientUpdate) {
//                return;
//            }
//            final int w = Math.max(1, (int) (spectrum.getWidth() * 2)); //Gradient width
//            final int h = Math.max(1, spectrum.getHeight());//Gradient height
//            final Color[] colors = {spectrum.getForeground(), ThemeColors.TransparentColor};
//            final float[] dist = {0f, 1.0f};
//            final FastGaussianBlur fastGaussianBlur = new FastGaussianBlur(AMBIENT_BLUR_RADIUS);
//            final Rectangle2D ambientBounds = new Rectangle2D.Double(0, 0, w, h);
//            BufferedImage tempBufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//
//            RadialGradientPaint paint = new RadialGradientPaint(ambientBounds, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
//
//            Graphics2D g = (Graphics2D) tempBufferedImage.getGraphics();
//            g.setPaint(paint);
//            g.fillRect(0, 0, w, h);
//            g.dispose();
//
//            _ambientImageBackgroundBuffer.addLast(fastGaussianBlur.filter(tempBufferedImage, tempBufferedImage));
//            oldAmbientFg = spectrum.getForeground();
//            forceAmbientUpdate = false;
//        } catch (Exception e) {
//            Log.error(e.toString());
//            Log.warn("skipping");
//        }
//    }

//    protected void clearAmbientBuffer() {
//        _ambientImageBackgroundBuffer.clear();
//    }
}
