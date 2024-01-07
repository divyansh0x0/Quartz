package app.components.audio;

import app.audio.FrostAudio;
import material.component.MaterialComponent;
import material.theme.ThemeColors;
import material.utils.GraphicsUtils;
import material.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class CompoundAudioImage extends MaterialComponent {
    private CompletableFuture<Void> imageWriterThread;
    private int cornerRadius = 10;
    private Image backgroundImage;
    private Image[] images;
    private boolean hasRendered = false;
    private final Object lock = new Object();


    public CompoundAudioImage(FrostAudio... frostAudios) {
        setAudioFiles(frostAudios);
    }

    @Override
    protected void animateMouseEnter() {

    }

    @Override
    protected void animateMouseExit() {

    }

    @Override
    public void updateTheme() {
        setForeground(ThemeColors.getAccent());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        float strokeWidth = 2f;
        Graphics2D g2d = (Graphics2D) g;

        RoundRectangle2D clipRect = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        RoundRectangle2D borderRect = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);


        g2d.setColor(ThemeColors.getBackground());
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.setClip(clipRect);
        g2d.fill(borderRect);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, null);
            g2d.setClip(null);
            g2d.draw(borderRect);
        }

        if(!hasRendered){
            hasRendered = true;
            rewriteImage(this.images);
        }

    }

    private void rewriteImage(Image[] images) {
        synchronized (lock) {
            this.images = images;
            if (imageWriterThread != null) {
                imageWriterThread.cancel(true);
                imageWriterThread = null;
            }
            imageWriterThread = CompletableFuture.runAsync(() -> {
                try {
                    if (images != null && images.length > 0 && hasRendered) {
                        int size = Math.max(Math.min(getWidth(), getHeight()), 1);
                        backgroundImage = GraphicsUtils.imageMerger(size, this.images);
                    } else backgroundImage = null;
                } catch (Exception e) {
                    Log.error(e.toString());
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(this::repaint);
            });
        }
    }

    public void setAudioFiles(FrostAudio[] frostAudios) {
        if (frostAudios.length > 0) {
            Image[] images = frostAudios.length > 3 ? new BufferedImage[4] : new BufferedImage[1];
            for (int i = 0; i < images.length; i++)
                images[i] = frostAudios[i].getArtwork();
            rewriteImage(images);
        }
    }

    public void setAudioFiles(ArrayList<FrostAudio> frostAudios) {
        if (frostAudios.size() > 0) {
            Image[] images = frostAudios.size() > 3 ? new Image[4] : new Image[1];
            for (int i = 0; i < images.length; i++)
                images[i] = frostAudios.get(i).getArtwork();
            rewriteImage(images);
        }
    }


    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public void setImages(BufferedImage[] images) {
        rewriteImage(images);
    }
}
