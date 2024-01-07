package app.components.audio;

import app.audio.Artist;
import app.audio.FrostAudio;
import app.audio.Playlist;
import app.audio.indexer.FrostIndexer;
import app.audio.player.FrostPlayerController;
import app.audio.player.FrostQueue;
import app.components.Icons;
import app.components.enums.NavigationLink;
import app.components.listeners.AudioTileClickListener;
import material.MaterialParameters;
import material.component.MaterialComponent;
import material.theme.ThemeColors;
import material.theme.enums.Elevation;
import material.tools.ColorUtils;
import material.utils.GraphicsUtils;
import material.utils.Log;
import material.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;

public class AudioTile extends MaterialComponent implements MouseInputListener, Serializable {
    private static final FontIcon BROKEN_AUDIO_ICON = new FontIcon();
    private static final Color BROKEN_AUDIO_COLOR = Color.RED;
    private static float BROKEN_AUDIO_COLOR_ALPHA = 0.1f;
    private final ArrayList<AudioTileClickListener> mouseClickListeners = new ArrayList<>();
    private static BufferedImage defaultArtwork;
    private Playlist playlist;
    private Elevation elevation = null;
    private BufferedImage artwork = null;
    private final @NotNull FrostAudio frostAudio;
    private int padding = 10;
    private Color audioNameColor = ThemeColors.getTextPrimary();
    private Color artistNameColor = ThemeColors.getTextSecondary();
    private boolean isActive = false;
    private final int ThumbSize = 50;
    private Color actualBackgroundColor;
    private Rectangle2D oldBounds;
    private boolean isHighlighting;
    private final NavigationLink LINK;
    private final AudioUtilityPopupMenu audioUtilityPopupMenu = new AudioUtilityPopupMenu(this);
    private static Color artworkBg = new Color(0x98000000, true);

    public AudioTile(@NotNull FrostAudio frostAudio, NavigationLink link) {
        super();
        this.frostAudio = frostAudio;
        LINK = link;
//        elevationDP = ComponentConstraints.VIEWER_ELEVATION_DP;
        setPreferredSize(new Dimension(ThumbSize, ThumbSize));
        setMinimumSize(new Dimension(ThumbSize, ThumbSize));
        addMouseListener(this);
        setFontSize(16);
        setOpaque(false);

        addLeftClickListener((audio) -> {
            play();
        });
        if (BROKEN_AUDIO_ICON.getIkon() == null)
            BROKEN_AUDIO_ICON.setIkon(Icons.BROKEN_AUDIO);
    }

    public void play() {
        FrostPlayerController.getInstance().load(frostAudio);
        FrostPlayerController.getInstance().play();

        switch (LINK) {
            case ARTIST -> {
                Artist artist = FrostIndexer.getInstance().getArtistByName(frostAudio.getArtistsConcatenated());
                if (artist != null) {
                    FrostQueue.getInstance().newQueue(frostAudio, artist.getFrostAudios());
//                Log.success("QUEUE: " + artist.getFrostAudios());
                }
            }
            case EXPLORE -> {
                FrostQueue.getInstance().newQueue(frostAudio, FrostIndexer.getInstance().getAllAudioFiles());
            }
        }
    }

    private static final Color mouseOverColorLightMode = new Color(0x5000000, true);
    private static final Color mouseOverColorDarkMode = new Color(0x5FFFFFF, true);
    @Override
    protected void animateMouseEnter() {
//        if(ThemeColors.TransparentColor.equals(actualBackgroundColor))
//            animateBG(ThemeManager.getInstance().getThemeType().equals(ThemeType.Dark) ? mouseOverColorDarkMode : mouseOverColorLightMode);
//        else
//            animateBG(actualBackgroundColor.brighter());
    }

    @Override
    protected void animateMouseExit() {
//        if(actualBackgroundColor != null)
//            animateBG(actualBackgroundColor);
    }

    //TODO fix bad transitions in active status
    @Override
    public void updateTheme() {
        Color newBackground;
        if (isActive) {
            newBackground = ColorUtils.RgbToArgb(ThemeColors.getActiveBackgroundColor(), 0.4f);
        } else {
//            newBackground = elevationDP == null ? ThemeColors.TransparentColor : ThemeColors.getColorByElevation(elevationDP);
            newBackground = ThemeColors.TransparentColor;
        }
        audioNameColor = ThemeColors.getTextPrimary();
        artistNameColor = ThemeColors.getTextSecondary();
        artworkBg = ColorUtils.darken(newBackground, 10);
        actualBackgroundColor = newBackground;
//        if(isActive)
//            setBackground(newBackground);
//        else
        animateBG(newBackground);

        //        BROKEN_AUDIO_COLOR = ColorUtils.RgbToArgb(ThemeColors.getBackgroundDanger(), BROKEN_AUDIO_COLOR_ALPHA);
    }

    private void showPopup() {
        Log.info("Showing popup");
//        audioTile.setSelected(true);
        audioUtilityPopupMenu.show(MouseInfo.getPointerInfo().getLocation());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        float dividerWidthPercentage = 1f;
        Graphics2D g2d = (Graphics2D) g.create();
//        int highlightPercentage = 2;
//        int activePercentage = 10;
//        int dividerHeight = 1;
        int cornerRadius = MaterialParameters.CORNER_RADIUS;
        int iSize = getHeight() - padding * 2;
        int iX = padding;
        int iY = padding;
        int gap = 5;
        Image artwork = frostAudio.getArtwork();

        //Background
        RoundRectangle2D artworkBounds = new RoundRectangle2D.Float(iX, iY, iSize, iSize, cornerRadius, cornerRadius);
        Color tileBgColor = isSelected() ? ThemeColors.getSelectionColors().getBackground() : getBackground(); //If selected then change color to selection color else set it to default
        g2d.setColor(tileBgColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);


        //Artwork background
        g2d.setColor(artworkBg);
        g2d.fill(artworkBounds);

        g2d.setClip(artworkBounds);
        g2d.drawImage(artwork, iX, iY, iSize, iSize, null);
        g2d.setClip(null);


        //border of artwork
        g2d.setColor(ColorUtils.darken(ThemeColors.getBackground(), 10));
        g2d.draw(artworkBounds);

        //Drawing text
        Duration duration = Duration.ofMillis((long) frostAudio.getDurationInSeconds() * 1000);
        String artistName = frostAudio.getArtistsConcatenated();
        String audioName = frostAudio.getName();
        String audioDuration = StringUtils.formatTime(duration);

        g2d.setFont(getFont());

        //artist and duration text metrics are same
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int durationTextSize = (int) fontMetrics.getStringBounds(audioDuration, g2d).getWidth() + padding * 2;


        int availableWidth = getWidth() - iSize - padding - iX - durationTextSize;
        int tX = iX + padding + iSize;
        int tY = (getHeight() - fontMetrics.getAscent()) / 2;

        //Audio name
        g2d.setFont(getFont());
        g2d.setColor(audioNameColor);
        audioName = GraphicsUtils.clipString(g2d, audioName, availableWidth);

        if (getFont().canDisplayUpTo(audioName) == -1)
            g2d.drawString(audioName, tX, tY);
        else { //If there are unsupported characters then render them using default font
            drawCompatibleString(audioName, tX, tY, g2d, getFont());
        }

        //artist name
        int artistFontSize = (int) Math.round(getFontSize() * 0.8); //80% of font size
        Font artistFont = new Font(getFont().getName(), getFont().getStyle(), artistFontSize);
        g2d.setFont(artistFont);
        g2d.setColor(artistNameColor);
        fontMetrics = g2d.getFontMetrics();
        artistName = GraphicsUtils.clipString(g2d, artistName, availableWidth);
        tY = tY + gap + fontMetrics.getAscent();
        if (getFont().canDisplayUpTo(audioName) == -1)
            g2d.drawString(artistName, tX, tY);
        else { //If there are unsupported characters then render them using default font
            drawCompatibleString(artistName, tX, tY, g2d, artistFont);
        }
        //If audio is broken show an error visual for it
        if (frostAudio.isBroken()) {
            if (BROKEN_AUDIO_ICON.getIconSize() != this.getFontSize())
                BROKEN_AUDIO_ICON.setIconSize((int) this.getFontSize());
            ImageIcon icon = BROKEN_AUDIO_ICON.toImageIcon();
            int dX = getWidth() - durationTextSize;
            int dY = (getHeight() - BROKEN_AUDIO_ICON.getIconHeight()) / 2;
            g2d.drawImage(icon.getImage(), dX, dY, null);
            float ALPHA = 0.5f;
            g2d.setColor(BROKEN_AUDIO_COLOR);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ALPHA));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        } else {
            //drawing duration
            g2d.setFont(getFont());
            fontMetrics = g2d.getFontMetrics();
            int dX = getWidth() - durationTextSize;
            int dY = (getHeight() + fontMetrics.getAscent()) / 2;
            g2d.drawString(audioDuration, dX, dY);
            g2d.setClip(null);
        }
        Toolkit.getDefaultToolkit().sync();
    }


    private boolean isBoundsChanged() {
        if (!getBounds().equals(oldBounds)) {
            oldBounds = getBounds();
            return true;
        }
        return false;
    }

    private Dimension getRequiredDimensions() {
        if (getGraphics() != null) {
            var fontMetrics = getGraphics().getFontMetrics();

            int artistWidth = fontMetrics.stringWidth(frostAudio.getArtistsConcatenated());
            int nameWidth = fontMetrics.stringWidth(frostAudio.getName());
            int maxTextWidth = Math.max(artistWidth, nameWidth);

            int imageWidth = getHeight() + padding * 2;

            int requiredComponentWidth = imageWidth + maxTextWidth;
            return new Dimension(requiredComponentWidth, getHeight());
        } else return getPreferredSize();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        updateTheme();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!e.isConsumed()) {
            if (SwingUtilities.isLeftMouseButton(e))
                leftClicked(e, frostAudio);
            else if (SwingUtilities.isRightMouseButton(e)) {
                showPopup();
            }
        }
    }


    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }


    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void addLeftClickListener(AudioTileClickListener l) {
        if (!mouseClickListeners.contains(l)) {
            mouseClickListeners.add(l);
        }
    }


    private void leftClicked(MouseEvent e, FrostAudio frostAudio) {
        if (!e.isConsumed())
            for (AudioTileClickListener l : mouseClickListeners)
                l.clicked(frostAudio);

    }


    public Elevation getElevationDP() {
        return elevation;
    }

    public int getPadding() {
        return padding;
    }

    public AudioTile setPadding(int padding) {
        this.padding = padding;
        repaint();
        return this;
    }

    public NavigationLink getLink() {
        return LINK;
    }

    @Override
    public String toString() {
        return """
                    --------------
                    Audio File: %s
                    --------------
                """.formatted(frostAudio);
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public BufferedImage getArtwork() {
        return artwork;
    }

    public @NotNull FrostAudio getFrostAudio() {
        return frostAudio;
    }
}
