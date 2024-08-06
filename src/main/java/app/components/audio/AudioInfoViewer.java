package app.components.audio;

import app.audio.AudioData;
import material.MaterialParameters;
import material.component.MaterialComponent;
import material.theme.ThemeColors;
import material.utils.GraphicsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AudioInfoViewer extends MaterialComponent {
    private Image artwork = null;
    private static BufferedImage defaultArtworkImage;
    private AudioData audioData;
    private Thread imageWriterThread;
    private static final int padding = 5;
    private static Color audioNameColor = ThemeColors.getTextPrimary();
    private Color artistNameColor = ThemeColors.getTextSecondary();
    private boolean isBold = true;
    private boolean isItalic;
    private boolean isLoaded = false;

    private final int ThumbSize = 50;

    public AudioInfoViewer() {
        super();
        setPreferredSize(new Dimension(ThumbSize, ThumbSize));
        setMinimumSize(new Dimension(ThumbSize, ThumbSize));
        setFontSize(14);
    }

    @Override
    protected void animateMouseEnter() {

    }

    @Override
    protected void animateMouseExit() {

    }

    private void rewriteImage() {
        if (!isLoaded)
            return;
        if (imageWriterThread != null) {
            imageWriterThread.interrupt();
            imageWriterThread = null;
        }
        if (audioData == null)
            return;
        imageWriterThread = Thread.startVirtualThread(() -> {
            final int iSize = Math.abs(getHeight() - padding * 2);
            try {
                // resize image
                this.artwork = audioData.getArtwork();
                SwingUtilities.invokeLater(this::repaint);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void updateTheme() {
        audioNameColor = ThemeColors.getTextPrimary();
        artistNameColor = ThemeColors.getTextSecondary();
    }

    private RoundRectangle2D imageBounds = new RoundRectangle2D.Float(0,0,0,0,MaterialParameters.CORNER_RADIUS,MaterialParameters.CORNER_RADIUS);
    @Override
    protected void paintComponent(Graphics g) {
        if (!isLoaded) {
            isLoaded = true;
            rewriteImage();
        }
        Graphics2D g2d = (Graphics2D) g;
        int iSize = getHeight() - padding * 2;
        int iX = padding;
        int iY = padding;
        int gap = 5;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);

        if (audioData != null && artwork != null) {
            imageBounds.setFrame(iX,iY,iSize,iSize);
            g2d.setComposite(AlphaComposite.SrcAtop);

            g2d.setClip(imageBounds);
            g2d.drawImage(artwork, iX, iY, iSize, iSize, null);
            g2d.setComposite(AlphaComposite.Src);
            g2d.setClip(null);

            g2d.setColor(ThemeColors.getBackground());
            g2d.draw(imageBounds);

            //Drawing font
            String artistName = audioData.getArtistsConcatenated();
            String audioName = audioData.getName();

            FontMetrics fontMetrics = g2d.getFontMetrics(this.getFont());
//            int artistStringWidth = fontMetrics.stringWidth(artistName);


            int availableWidth = getWidth() - iSize - padding - iX;
            int tX = iX + padding + iSize;
            int tY = (getHeight() - fontMetrics.getAscent()) / 2;

            //Name of audio
            g2d.setFont(getFont());
            g2d.setColor(audioNameColor);
            audioName = GraphicsUtils.clipString(g2d, audioName, availableWidth);

            if (getFont().canDisplayUpTo(audioName) == -1)
                g2d.drawString(audioName, tX, tY);
            else
                drawLanguageCompatibleString(audioName, tX, tY, g2d, getFont());

            //artist name
            int artistFontSize = (int) Math.round(getFontSize() * 0.8); //80% of font size
            Font artistFont = new Font(getFont().getName(), getFont().getStyle(), artistFontSize);
            g2d.setFont(artistFont);
            g2d.setColor(artistNameColor);
            fontMetrics = g2d.getFontMetrics();
            artistName = GraphicsUtils.clipString(g2d, artistName, availableWidth);

            tY = tY + gap + fontMetrics.getAscent();

            if (getFont().canDisplayUpTo(artistName) == -1)
                g2d.drawString(artistName, tX, tY);
            else
                drawLanguageCompatibleString(artistName, tX, tY, g2d, artistFont);

            g2d.setFont(getFont());

        }
        g2d.setClip(null);
        g2d.dispose();
    }


    public AudioInfoViewer setAudio(AudioData audioData) {
        this.audioData = audioData;
        setPreferredSize(getRequiredDimensions());
        rewriteImage();
        repaint();
        return this;
    }

    private Dimension getRequiredDimensions() {
        if (audioData != null && getGraphics() != null) {
            var fontMetrics = getGraphics().getFontMetrics();

            int artistWidth = fontMetrics.stringWidth(audioData.getArtistsConcatenated());
            int nameWidth = fontMetrics.stringWidth(audioData.getName());
            int maxTextWidth = Math.max(artistWidth, nameWidth);

            int imageWidth = getHeight() + padding * 2;

            int requiredComponentWidth = imageWidth + maxTextWidth;
            return new Dimension(requiredComponentWidth, getHeight());
        } else return getPreferredSize();
    }

    private boolean applyNewFont = false;
    private int fontSize;

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
        applyFontStyle();
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
        applyFontStyle();
    }

    public void applyFontStyle() {
        Font f = this.getFont();
        if (f != null) {

            if (isBold && isItalic) {
                Map<TextAttribute, Object> attributeMap = new HashMap<>();
                attributeMap.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
                attributeMap.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
                this.setFont(f.deriveFont(attributeMap));
            } else if (isBold) {
                this.setFont(f.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD)));
            } else if (isItalic) {
                this.setFont(f.deriveFont(Collections.singletonMap(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE)));
            }
            fontSize = getFont().getSize();
            this.repaint();
            this.revalidate();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (applyNewFont)
            setFontSize(fontSize);
        applyFontStyle();
    }

    public Image getArtwork() {
        return artwork;
    }
}
