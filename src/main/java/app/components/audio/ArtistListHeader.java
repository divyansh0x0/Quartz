package app.components.audio;

import app.audio.Artist;
import app.components.Icons;
import material.component.MaterialIconButton;
import material.component.MaterialLabel;
import material.component.enums.LabelStyle;
import material.containers.MaterialPanel;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import material.utils.Log;

import java.awt.image.BufferedImage;
import java.util.List;

import static java.awt.AWTEventMulticaster.add;

public class ArtistListHeader extends MaterialPanel {
    private List<Artist> artists;
    private boolean showBackButton;
    private CompoundAudioImage HeaderImage = new CompoundAudioImage();
    private final MaterialLabel Heading = new MaterialLabel("Listen to your favorite artist", LabelStyle.PRIMARY);
    private final MaterialLabel SubHeading = new MaterialLabel("or play a random artist", LabelStyle.SECONDARY);
    private final MaterialIconButton PlayRandomButton = new MaterialIconButton(Icons.SHUFFLE, "Play random").setCornerRadius(30).setIconSizeRatio(0.9);

    public ArtistListHeader(boolean showBackButton) {
        super(new MigLayout("nogrid, flowX, fill, insets 10"));

        MaterialPanel textContainer = new MaterialPanel(new MigLayout("alignY center, alignX Left, nogrid, flowy, fillX, insets 0"));

        add(HeaderImage, "h 200!, w 200!");
        add(textContainer, "growX");

        textContainer.add(Heading,"growX, gapX 10");
        textContainer.add(SubHeading, "growX, gapX 10");
        textContainer.add(PlayRandomButton, "gapX 10, gapY 20, w 150!, h 50!");

        textContainer.setElevationDP(null);
        setElevationDP(null);
        setShowBackButton(showBackButton);

        Heading.setFontSize(18);
        SubHeading.setFontSize(14);
    }

    public void setHeading(String text) {
        Heading.setText(text);
    }

    public void setSubHeading(String text) {
        SubHeading.setText(text);
    }

    public void setArtists(@NotNull List<Artist> artists) {
        Log.info("rewriting images");
        this.artists = artists;
        BufferedImage[] images = artists.size() > 3 ? new BufferedImage[4] : new BufferedImage[1];
        for (int i = 0; i < images.length; i++)
            images[i] = artists.get(i).getArtistImage();
        HeaderImage.setImages(images);
        HeaderImage.revalidate();
        repaint();
        revalidate();
    }

    public boolean isShowBackButton() {
        return showBackButton;
    }

    public void setShowBackButton(boolean showBackButton) {
        this.showBackButton = showBackButton;
    }
}
