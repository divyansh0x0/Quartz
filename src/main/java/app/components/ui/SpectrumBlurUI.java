package app.components.ui;

import app.components.spectrum.Spectrum;
import com.jhlabs.image.GaussianFilter;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SpectrumBlurUI extends LayerUI<Spectrum> {

    //    private BufferedImage mOffscreenImage;
//    private final BufferedImageOp imgOperation;
    private static final int BLUR_RADIUS = 9;
    private static final GaussianFilter gaussianFilter = new GaussianFilter(BLUR_RADIUS);
    private static final Composite COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);

    public SpectrumBlurUI() {
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        int w = c.getWidth();
        int h = c.getHeight();

        if (w == 0 || h == 0) {
            return;
        }

        // Only create the offscreen image if the one we have
        // is the wrong size.
//        if (mOffscreenImage == null || mOffscreenImage.getWidth() != w
//                || mOffscreenImage.getHeight() != h) {
//        }
        var snapshot = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        var blurredImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = snapshot.createGraphics();
        ig2.setClip(g.getClip());
        super.paint(ig2, c);
        ig2.dispose();

        Graphics2D g2d  = (Graphics2D) g;
        g2d.setComposite(COMPOSITE);
        gaussianFilter.filter(snapshot, blurredImage);
        g2d.drawImage(blurredImage, null, 0, 0);
    }

}
