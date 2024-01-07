package app.components;

import material.MaterialParameters;
import material.Padding;
import material.component.MaterialComponent;
import material.listeners.SeekListener;
import material.theme.ThemeColors;
import material.theme.enums.Elevation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import material.utils.Log;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.time.Duration;
import java.util.ArrayList;

public class PlaybackBar extends MaterialComponent {

    int progressBarHeight = 8; //80% height

    private Elevation elevation;
    private Elevation TOOLTIP_ELEVATION = Elevation._24;
    private Color _fillColor = ThemeColors.getAccent();
    private Color TooltipColor = ThemeColors.getColorByElevation(TOOLTIP_ELEVATION);
    private long CURRENT_TIME_NANOS;
    private long TOTAL_TIME_NANOS;
    private final ArrayList<SeekListener> seekListeners = new ArrayList<>();
    private Point mousePressedPosition;
    private boolean isMousePressed = false;
    private boolean isMouseDragged = false;
    private boolean isMouseReleased = false;
    private RoundRectangle2D _validBarPressBounds;

    private static   final Padding tooltipPadding = new Padding(2, 5);
    public PlaybackBar() {
        super();
        setDoubleBuffered(true);
        CURRENT_TIME_NANOS = 0;
        TOTAL_TIME_NANOS = 0;
        this.setFocusable(true);
    }

    @Override
    protected void animateMouseEnter() {

    }

    @Override
    protected void animateMouseExit() {

    }

    @Override
    public void updateTheme() {
        if (elevation == null)
            elevation = Elevation._8;
        if (TOOLTIP_ELEVATION == null)
            TOOLTIP_ELEVATION = Elevation._24;
        Color bgColor = ThemeColors.getColorByElevation(elevation);
        Color fgColor = ThemeColors.getTextSecondary();
        _fillColor = ThemeColors.getAccent();
        TooltipColor = ThemeColors.getColorByElevation(TOOLTIP_ELEVATION);

        this.animateBG(bgColor);
        this.animateFG(fgColor);
        //Finally, reapplying font styles
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        try {
            Graphics2D g2d = (Graphics2D) g;
            final int gap = 5;
            final int tooltipRadius = 5;
            final int TOOLTIP_FONT_SIZE = 12;
            int cornerRadius = MaterialParameters.CORNER_RADIUS;

            String currStr = formatTime(CURRENT_TIME_NANOS);
            String totalStr = formatTime(TOTAL_TIME_NANOS);

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2d.setColor(getForeground());
            g2d.setFont(getFont());
            FontMetrics fontMetrics = g2d.getFontMetrics();

            Rectangle2D cR2 = fontMetrics.getStringBounds(currStr, g2d);
            Rectangle2D tR2 = fontMetrics.getStringBounds(totalStr, g2d);
            int x = 0; // x position of current time text;
            int y = (int) (((getHeight() - cR2.getHeight()) / 2) + fontMetrics.getAscent()); // y position of current time text;
            g2d.drawString(currStr, x, y);


            //progressbar background fill
            g2d.setColor(getBackground());
            x = (int) (cR2.getWidth() + gap); // x position of progress bar;
            y = (getHeight() - progressBarHeight) / 2; // y position of progress bar
            int pWidth = (int) (getWidth() - ((cR2.getWidth() + tR2.getWidth()) + (gap * 2))); // width of progress bar
            _validBarPressBounds = new RoundRectangle2D.Double(x, y, pWidth, progressBarHeight, cornerRadius, cornerRadius);
            g2d.fill(_validBarPressBounds);
            //progress bar Border
//            g2d.setColor(ColorUtils.darken(getBackground(),10));
//            g2d.draw(new RoundRectangle2D.Double(x - 1, y - 1, pWidth + 1, progressBarHeight + 1, cornerRadius,cornerRadius));

            //Total time label
            g2d.setColor(getForeground());
            x = (int) (cR2.getWidth() + (gap * 2) + pWidth); //x position of total time text
            y = (int) (((getHeight() - tR2.getHeight()) / 2) + fontMetrics.getAscent());//y position of total time text
            g2d.drawString(totalStr, x, y);

            //filling progress bar
            if (TOTAL_TIME_NANOS > 0) {
                int fillWidth;

                if (mousePressedPosition != null && (isMousePressed || isMouseDragged)) {
                    int seekBarMouseX = (int) (mousePressedPosition.getX() - _validBarPressBounds.getX()); // Position of mouse (x,0) from background (x,0)
                    seekBarMouseX = (int) Math.min(Math.max(seekBarMouseX, 0), _validBarPressBounds.getWidth());
                    fillWidth = seekBarMouseX;
                    //Tooltip
                    Font tooltipFont = new Font(getFont().getName(), getFont().getStyle(), TOOLTIP_FONT_SIZE);
                    fontMetrics = g2d.getFontMetrics(tooltipFont);
                    long timeByPos = (long) ((fillWidth * TOTAL_TIME_NANOS) / _validBarPressBounds.getWidth());
                    String tooltipText = formatTime(timeByPos);
                    Rectangle2D tooltipTextBounds = fontMetrics.getStringBounds(tooltipText, g2d);
                    int mousePosX = (int) (seekBarMouseX + _validBarPressBounds.getX()); //position of mouse from (0,0) of component
                    int tWidth = (int) (tooltipTextBounds.getWidth() + (tooltipPadding.getLeft() + tooltipPadding.getRight())); //width of tooltip
                    int tHeight = (int) (tooltipTextBounds.getHeight() + (tooltipPadding.getTop() + tooltipPadding.getBottom())); //height of tooltip
                    int tbX = mousePosX; // tooltip background x position
                    int tbY = (int) (_validBarPressBounds.getY() - tHeight / 2); // tooltip background y position
                    RoundRectangle2D tBG = new RoundRectangle2D.Double(tbX, tbY, tWidth, tHeight, tooltipRadius, tooltipRadius); //Tooltip background round rectangle
                    //Drawing tooltip background
                    g2d.setColor(TooltipColor);
                    g2d.fill(tBG);

                    //Tooltip Text
                    int ttX = (int) Math.round(tBG.getX() + (tBG.getWidth() / 2 - tooltipTextBounds.getWidth() / 2)); // tooltip text x position
                    int ttY = (int) Math.round(((tBG.getY() + tBG.getHeight() / 2) - tooltipTextBounds.getHeight() / 2) + fontMetrics.getAscent()); // tooltip text y positionLog.info("");
                    g2d.setFont(tooltipFont);
                    g2d.setColor(getForeground());
                    g2d.drawString(tooltipText, ttX, ttY);
                    g2d.setFont(getFont());//Set font to default
                } else {
                    if (isMouseReleased && mousePressedPosition != null) {
                        int seekBarMouseX = (int) (mousePressedPosition.getX() - _validBarPressBounds.getX());
                        fillWidth = (int) Math.min(Math.max(seekBarMouseX, 0), _validBarPressBounds.getWidth());
                        this.CURRENT_TIME_NANOS = (long) ((fillWidth * TOTAL_TIME_NANOS) / _validBarPressBounds.getWidth());
                        isMouseReleased = false;
                        mousePressedPosition = null;
                        triggerSeekEvent(CURRENT_TIME_NANOS);
                    } else {
                        fillWidth = (int) Math.round(((double) CURRENT_TIME_NANOS / (double) TOTAL_TIME_NANOS * _validBarPressBounds.getWidth()));

                    }
                }
                //Playback position
                g2d.setColor(_fillColor);
                g2d.setClip(_validBarPressBounds);
                g2d.fillRect((int) _validBarPressBounds.getX(), (int) _validBarPressBounds.getY(), fillWidth, progressBarHeight);

                //Smoothen the  playback position by drawing border
                Rectangle progressbarRect = new Rectangle((int) _validBarPressBounds.getX(), (int) _validBarPressBounds.getY(), fillWidth, progressBarHeight);
                g2d.setClip(progressbarRect);
                g2d.draw(_validBarPressBounds);


                g2d.dispose();
                Toolkit.getDefaultToolkit().sync();
            }
        } catch (Exception e) {
            Log.error(e);
            e.printStackTrace();
        }
    }

    private void addListeners() {
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateSeek(e);
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                getReadyForSeek(e);

                requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                finalizeSeek();
            }
        });
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }


    private void updateSeek(MouseEvent e) {
        if (isMousePressed) {
            isMouseDragged = true;
            if (mousePressedPosition == null)
                mousePressedPosition = new Point();
            mousePressedPosition.setLocation(e.getX(), e.getY());
            repaint();
        }
    }

    private void getReadyForSeek(MouseEvent e) {
        if (canInteract() && _validBarPressBounds != null && isValidSeekMousePress(e.getX(), e.getY())) {
            mousePressedPosition = new Point(e.getX(), e.getY());
            isMousePressed = true;
            isMouseReleased = false;
            repaint();

        }
    }

    private boolean canInteract() {
        return TOTAL_TIME_NANOS > 0;
    }

    private boolean isValidSeekMousePress(int x, int y) {
        RoundRectangle2D bounds = _validBarPressBounds;
        return x >= bounds.getX() &&
                x <= bounds.getWidth() + bounds.getX() &&
                y >= bounds.getY() &&
                y <= bounds.getHeight() + bounds.getY();
    }

    private void finalizeSeek() {
        isMousePressed = false;
        isMouseDragged = false;
        isMouseReleased = true;
        repaint();

    }

    private void animateFillColor(Color to) {
        animateFG(to);
    }

    @Contract(pure = true)
    private @NotNull String formatTime(@NotNull Duration d) {
        long m = d.toMillis();
        long sec = (m / 1000) % 60;
        long min = (m / 1000) / 60;
        return "%02d:%02d".formatted(min, sec);
    }
    @Contract(pure = true)
    private @NotNull String formatTime(@NotNull long d) {
        long m = (long) (d/1e6);
        long sec = (m / 1000) % 60;
        long min = (m / 1000) / 60;
        return "%02d:%02d".formatted(min, sec);
    }

    public long
    getCurrentTime() {
        return CURRENT_TIME_NANOS;
    }

    public void setCurrentTime(long timeToSet) {
        if (timeToSet < 0 || timeToSet == this.CURRENT_TIME_NANOS)
            return;
        if(timeToSet > getTotalTimeNanos()) //if time to set is greater than total time then set current time to total time
            timeToSet = getTotalTimeNanos();
        this.CURRENT_TIME_NANOS = timeToSet;
        repaint();
    }

    public long getTotalTimeNanos() {
        return TOTAL_TIME_NANOS;
    }

    public void setTotalTime(long totalTime) {
        if(TOTAL_TIME_NANOS == 0 || TOTAL_TIME_NANOS != totalTime) {
            TOTAL_TIME_NANOS = totalTime;
            repaint();
        }
    }

    public Color getColor() {
        return _fillColor;
    }

    /**
     * Set progress color of playback bar.
     */
    public PlaybackBar setColor(Color color) {
        this._fillColor = color;
        animateFillColor(color);
        return this;
    }

    public void onSeek(SeekListener listener) {
        if (!seekListeners.contains(listener))
            seekListeners.add(listener);
    }

    public void triggerSeekEvent(long newCurrTime) {
        Log.success("seeking");
        seekListeners.forEach(listener -> listener.seeked(Duration.ofNanos(newCurrTime)));
    }

    public Elevation getElevationDP() {
        return elevation;
    }

    public void setElevationDP(Elevation elevation) {
        this.elevation = elevation;
        repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        addListeners();
        repaint();
        revalidate();
    }

}
