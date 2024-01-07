package app.components.audio;

import app.audio.Artist;
import app.settings.constraints.ComponentParameters;
import material.MaterialParameters;
import material.Padding;
import material.component.MaterialComponent;
import material.theme.ThemeColors;
import material.theme.enums.Elevation;
import material.tools.ColorUtils;
import material.utils.GraphicsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class ArtistTile extends MaterialComponent {
    private final ArrayList<ArtistTileClickListener> clickListeners = new ArrayList<>();
    private static final float FONT_SIZE = 18f;
    private final Artist ARTIST;
    private final Padding padding = new Padding(10);
    private final int CORNER_RADIUS = MaterialParameters.CORNER_RADIUS;
    private boolean isLoaded = false;


    public ArtistTile(Artist artist) {
        this.ARTIST = artist;

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && !e.isConsumed()) {
                    for (ArtistTileClickListener listener : clickListeners) {
                        listener.clicked(artist);
                    }
                }
            }
        });


    }


    public void onClick(ArtistTileClickListener listener) {
        if (!clickListeners.contains(listener))
            clickListeners.add(listener);
    }

    public ArrayList<ArtistTileClickListener> getClickListeners() {
        return clickListeners;
    }
    @Override
    protected void animateMouseEnter() {

    }

    @Override
    protected void animateMouseExit() {

    }
    @Override
    public void updateTheme() {
        final Elevation elevation = Elevation.get(Math.min(ComponentParameters.VIEWER_ELEVATION_DP.ordinal() + 1, Elevation._24.ordinal()));
        animateFG(ThemeColors.getTextPrimary());
        animateBG(ThemeColors.getColorByElevation(elevation));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        final int StrokeWidth = 1;
        final int GAP = 8;
        float compositingAlpha = 0.8f;
        Image thumbnailImage = ARTIST.getArtistImage();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(StrokeWidth));
        g2d.setFont(g2d.getFont().deriveFont(FONT_SIZE));

        //background
        if(isSelected())
            g2d.setColor(ThemeColors.getSelectionColors().getBackground());
        else
            g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

        Shape defaultClip = g2d.getClip();

        //Drawing thumbnail
        int x = padding.getLeft();
        int y = padding.getTop();
        int iSize = getHeight() - padding.getVertical();
        RoundRectangle2D thumbRect = new RoundRectangle2D.Double(x, y, iSize, iSize, iSize, iSize);
        if (thumbnailImage != null) {
            g2d.setClip(thumbRect);
            g2d.drawImage(thumbnailImage, x, y, iSize,iSize, null);
            g2d.setClip(defaultClip);
        }
        //Thumbnail border
        g2d.draw(thumbRect);
        //Artist name
        FontMetrics fm = g2d.getFontMetrics();
        int tY = (getHeight() - fm.getAscent()) / 2;
        int tX = (int) (thumbRect.getWidth() + GAP + padding.getLeft());
        int tWidth = getWidth() - tX - padding.getRight();

        String artistName = GraphicsUtils.clipString(g2d, ARTIST.getName(), tWidth);

        g2d.setColor(getForeground());
        if(g2d.getFont().canDisplayUpTo(artistName) == -1) {
            g2d.drawString(artistName, tX, tY);
        }else{
            drawCompatibleString(artistName,tX,tY,g2d,g2d.getFont());
        }

        //Number of songs

        //Artist pill
        String pilLText = "Artist";
        g2d.setFont(g2d.getFont().deriveFont(FONT_SIZE * 0.7f));
        Padding pPad = new Padding(2, 5);
        FontMetrics pFm = g2d.getFontMetrics();
        Rectangle pRect = pFm.getStringBounds(pilLText, g2d).getBounds();
        int pWidth = pPad.getHorizontal() + pRect.width;
        int pHeight = pPad.getVertical() + pRect.height;
        int pY = tY + pFm.getAscent() + fm.getDescent() + GAP;
        int pillBgY = tY + GAP + fm.getDescent() / 2;
        int pX = tX + pPad.getLeft();
        g2d.setColor(ColorUtils.darken(getBackground(), 5D, true));
        g2d.fillRoundRect(tX, pillBgY, pWidth, pHeight, CORNER_RADIUS, CORNER_RADIUS);
        g2d.setColor(ColorUtils.RgbToArgb(getForeground(), compositingAlpha));
        g2d.drawString(pilLText, pX, pY);

        //Songs pill
        pilLText = ARTIST.getFrostAudios().size() + " songs";
        tX += pWidth + GAP;
        pX = tX + pPad.getLeft();
        pWidth = pPad.getHorizontal() + pFm.stringWidth(pilLText);

        g2d.setColor(ColorUtils.darken(getBackground(), 5D, true));
        g2d.fillRoundRect(tX, pillBgY, pWidth, pHeight, CORNER_RADIUS, CORNER_RADIUS);
        g2d.setColor(ColorUtils.RgbToArgb(getForeground(), compositingAlpha));
        g2d.drawString(pilLText, pX, pY);

        if (!isLoaded) {
            isLoaded = true;
        }
    }

    public Artist getArtist() {
        return ARTIST;
    }

    public interface ArtistTileClickListener {
        void clicked(Artist artist);
    }


}
