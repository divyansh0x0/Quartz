package app.components.audio;

import app.audio.Folder;
import app.settings.constraints.ComponentParameters;
import material.MaterialParameters;
import material.Padding;
import material.component.MaterialComponent;
import material.theme.ThemeColors;
import material.theme.enums.Elevation;
import material.tools.ColorUtils;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.swing.FontIcon;
import material.utils.GraphicsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class FolderTile extends MaterialComponent {
    private final ArrayList<FolderTile.FolderTileClickListener> clickListeners = new ArrayList<>();
    private final int CORNER_RADIUS = MaterialParameters.CORNER_RADIUS;
    private final Padding padding = new Padding(10);
    private static final float FONT_SIZE = 18f;
    private boolean isLoaded = false;
    private @NotNull Folder FOLDER;
    private final FontIcon FOLDER_ICON = new FontIcon();
    @Override
    protected void animateMouseEnter() {

    }

    @Override
    protected void animateMouseExit() {

    }
    public FolderTile(@NotNull Folder FOLDER) throws HeadlessException {
        this.FOLDER = FOLDER;

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    for (FolderTileClickListener listener : clickListeners) {
                        listener.clicked(FOLDER);
                    }
                }
            }
        });


        FOLDER_ICON.setIkon(BootstrapIcons.FOLDER2_OPEN);
        updateTheme();
    }

    @Override
    public void updateTheme() {
        final Elevation elevation = Elevation.get(Math.min(ComponentParameters.VIEWER_ELEVATION_DP.ordinal() + 1, Elevation._24.ordinal()));
        animateFG(ThemeColors.getTextPrimary());
        animateBG(ThemeColors.getColorByElevation(elevation));
        if(FOLDER_ICON != null)
            FOLDER_ICON.setIconColor(ColorUtils.AlphaCompositedRGB(getForeground(),getBackground(),0.5f));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        final int StrokeWidth = 1;
        final int GAP = 5;
        float compositingAlpha = 0.8f;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(StrokeWidth));
        g2d.setFont(g2d.getFont().deriveFont(FONT_SIZE));

        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

        //Drawing Icon
        int x = padding.getLeft();
        int y = padding.getTop();
        int iSize = (int)((getHeight() - padding.getVertical()) * 0.9);//90% size

        if(FOLDER_ICON.getIconSize() != iSize)
            FOLDER_ICON.setIconSize(iSize);
        RoundRectangle2D iconRect = new RoundRectangle2D.Double(x, y, iSize + padding.getHorizontal(), iSize + padding.getVertical(), CORNER_RADIUS, CORNER_RADIUS);
        g2d.drawImage(FOLDER_ICON.toImageIcon().getImage(),x,y,null);

        //Folder name
        FontMetrics fm = g2d.getFontMetrics();
        int tY = (getHeight() - fm.getAscent()) / 2;
        int tX = (int) (iconRect.getWidth() + GAP + padding.getLeft());
        int tWidth = getWidth() - tX - padding.getRight();

        String artistName = GraphicsUtils.clipString(g2d, FOLDER.getName(), tWidth);

        g2d.setColor(getForeground());
        if(g2d.getFont().canDisplayUpTo(artistName) == -1) {
            g2d.drawString(artistName, tX, tY);
        }else{
            drawCompatibleString(artistName,tX,tY,g2d,g2d.getFont());
        }

        //Number of songs

        //Artist pill
        String pilLText = "Folder: " + FOLDER.getPath();
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
        pilLText = FOLDER.getAudioDatas().size() + " songs";
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

    public Folder getFolder() {
        return FOLDER;
    }

    public ArrayList<FolderTileClickListener> getClickListeners() {
        return clickListeners;
    }
    public void setClickListeners(FolderTileClickListener clickListener){
        if (!clickListeners.contains(clickListener)){
            clickListeners.add(clickListener);
        }
    }

    public interface FolderTileClickListener {
        void clicked(Folder folder);
    }
}
