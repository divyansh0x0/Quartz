package app.components.audio;

import app.audio.Artist;
import app.audio.AudioData;
import app.audio.Playlist;
import app.audio.indexer.AudioDataIndexer;
import app.audio.player.AphroditeAudioController;
import app.audio.player.AudioQueue;
import app.components.enums.NavigationLink;
import app.components.listeners.AudioTileClickListener;
import material.MaterialParameters;
import material.component.MaterialComponent;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.Elevation;
import material.theme.enums.ThemeType;
import material.tools.ColorUtils;
import material.utils.GraphicsUtils;
import material.utils.Log;
import material.utils.StringUtils;
import material.window.MousePointer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

public class AudioTile extends MaterialComponent implements MouseInputListener, Serializable {
//    private static final FontIcon BROKEN_AUDIO_ICON = new FontIcon();
//    private static final Color BROKEN_AUDIO_COLOR = Color.RED;
//    private static float BROKEN_AUDIO_COLOR_ALPHA = 0.1f;
    private final ArrayList<AudioTileClickListener> mouseClickListeners = new ArrayList<>();
    private Playlist playlist;
    private Elevation elevation = null;
    private BufferedImage artwork = null;
    private final @NotNull AudioData audioData;
    private int padding = 10;
    private Color audioNameColor = ThemeColors.getTextPrimary();
    private Color artistNameColor = ThemeColors.getTextSecondary();
    private boolean isActive = false;
    private final int ThumbSize = 50;
    private Color actualBackgroundColor;
//    private Rectangle2D oldBounds;
//    private boolean isHighlighting;
    private final NavigationLink LINK;
    private static final AudioUtilityPopupMenu audioUtilityPopupMenu = new AudioUtilityPopupMenu();
    private static Color artworkBg = new Color(0x98000000, true);

    public AudioTile(@NotNull AudioData audioData, NavigationLink link) {
        super();
        this.audioData = audioData;
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
//        if (BROKEN_AUDIO_ICON.getIkon() == null)
//            BROKEN_AUDIO_ICON.setIkon(Icons.BROKEN_AUDIO);
    }

    public void play() {
        AphroditeAudioController.getInstance().load(audioData);
        AphroditeAudioController.getInstance().play();

        switch (LINK) {
            case ARTIST -> {
                Artist artist = AudioDataIndexer.getInstance().getArtistByName(audioData.getArtistsConcatenated());
                if (artist != null) {
                    AudioQueue.getInstance().newQueue(audioData, artist.getAudioDataList());
//                Log.success("QUEUE: " + artist.getAudioDatas());
                }
            }
            case EXPLORE -> {
                AudioQueue.getInstance().newQueue(audioData, AudioDataIndexer.getInstance().getAllAudioFiles());
            }
        }
    }

    private static final Color mouseOverColorLightMode = new Color(0x5000000, true);
    private static final Color mouseOverColorDarkMode = new Color(0x5FFFFFF, true);
    @Override
    protected void animateMouseEnter() {
        if(ThemeColors.TransparentColor.equals(actualBackgroundColor))
            animateBG(ThemeManager.getInstance().getThemeType().equals(ThemeType.Dark) ? mouseOverColorDarkMode : mouseOverColorLightMode);
        else
            animateBG(actualBackgroundColor.brighter());
    }

    @Override
    protected void animateMouseExit() {
        if(actualBackgroundColor != null)
            animateBG(actualBackgroundColor);
    }


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
        audioUtilityPopupMenu.show(MousePointer.getPointerLocation(),this);
    }
    private RoundRectangle2D artworkBounds = new RoundRectangle2D.Float(0,0,0,0,MaterialParameters.CORNER_RADIUS,MaterialParameters.CORNER_RADIUS);

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
        Image artwork = audioData.getArtwork();

        //Background
        artworkBounds.setFrame(iX,iY,iSize,iSize);
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
        g2d.setColor(ThemeColors.getBackground());
        g2d.draw(artworkBounds);

        //Drawing text
        String artistName = audioData.getArtistsConcatenated();
        String audioName = audioData.getName();
        String audioDuration = StringUtils.getFormattedTimeMs(audioData.getDurationInMs());

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
            drawLanguageCompatibleString(audioName, tX, tY, g2d, getFont());
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
            drawLanguageCompatibleString(artistName, tX, tY, g2d, artistFont);
        }
        //If audio is broken show an error visual for it
//        if (audioData.isBroken()) {
//            if (BROKEN_AUDIO_ICON.getIconSize() != this.getFontSize())
//                BROKEN_AUDIO_ICON.setIconSize((int) this.getFontSize());
//            ImageIcon icon = BROKEN_AUDIO_ICON.toImageIcon();
//            int dX = getWidth() - durationTextSize;
//            int dY = (getHeight() - BROKEN_AUDIO_ICON.getIconHeight()) / 2;
//            g2d.drawImage(icon.getImage(), dX, dY, null);
//            float ALPHA = 0.5f;
//            g2d.setColor(BROKEN_AUDIO_COLOR);
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ALPHA));
//            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
//        } else {
            //drawing duration
            g2d.setFont(getFont());
            fontMetrics = g2d.getFontMetrics();
            int dX = getWidth() - durationTextSize;
            int dY = (getHeight() + fontMetrics.getAscent()) / 2;
            g2d.drawString(audioDuration, dX, dY);
            g2d.setClip(null);
//        }
        Toolkit.getDefaultToolkit().sync();
    }




    private Dimension getRequiredDimensions() {
        if (getGraphics() != null) {
            var fontMetrics = getGraphics().getFontMetrics();

            int artistWidth = fontMetrics.stringWidth(audioData.getArtistsConcatenated());
            int nameWidth = fontMetrics.stringWidth(audioData.getName());
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
                leftClicked(e, audioData);
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


    private void leftClicked(MouseEvent e, AudioData audioData) {
        if (!e.isConsumed())
            for (AudioTileClickListener l : mouseClickListeners)
                l.clicked(audioData);

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
                """.formatted(audioData);
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

    public @NotNull AudioData getAudioData() {
        return audioData;
    }
}
