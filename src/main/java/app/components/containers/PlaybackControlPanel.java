package app.components.containers;

import app.audio.PlayerComponents;
import app.components.audio.AudioInfoViewer;
import app.components.buttons.control.*;
import app.components.buttons.playback.NextButton;
import app.components.buttons.playback.PlayButton;
import app.components.buttons.playback.PrevButton;
import app.components.buttons.playback.VolumeButton;
import app.settings.constraints.PlaybackPanelConstraints;
import material.animation.MaterialFixedTimer;
import material.component.MaterialSlider;
import app.components.PlaybackBar;
import material.component.enums.Orientation;
import material.containers.MaterialPanel;
import material.theme.ThemeColors;
import material.theme.enums.Elevation;
import net.miginfocom.swing.MigLayout;
import material.utils.Interpolation;
import material.utils.Log;
import material.utils.PerlinNoise;
import material.utils.filters.FastGaussianBlur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import static app.settings.constraints.ComponentParameters.CONTROL_PANEL_ELEVATION_DP;

public class PlaybackControlPanel extends MaterialPanel {
    private static final FastGaussianBlur FAST_GAUSSIAN_BLUR = new FastGaussianBlur(20);
    private static final AudioInfoViewer audioInfoViewer = new AudioInfoViewer();
    public static final int PREFF_HEIGHT = 100;
    private static PlaybackControlPanel instance;
    private static final PlaybackBar MATERIAL_PLAYBACK_BAR = new PlaybackBar();
    private static final Elevation ELEVATION = CONTROL_PANEL_ELEVATION_DP;
    private static final MigLayout migLayout = new MigLayout("nogrid, flowY, fill, gap 0, inset 0");

    private static final MaterialPanel controlPanel = new MaterialPanel(new MigLayout("flowX, fill"));
    private static final MaterialPanel utilityPanel = new MaterialPanel(new MigLayout("flowX, fill,gap 0, insets 10 10 10 10"));

    private static final PrevButton PREV_BUTTON = new PrevButton();
    private static final PlayButton PLAY_BUTTON = new PlayButton();
    private static final NextButton NEXT_BUTTON = new NextButton();

    protected static final ShuffleButton SHUFFLE_BUTTON = new ShuffleButton();
    protected static final RepeatButton REPEAT_BUTTON = new RepeatButton();
    protected static final LikeButton LIKE_BUTTON = new LikeButton();
    protected static final QueueButton QUEUE_BUTTON = new QueueButton();
    protected static final VolumeButton VOLUME_BUTTON = new VolumeButton();
    protected static final SpectrumButton SPECTRUM_BUTTON = new SpectrumButton();
    protected static final FullScreenButton CHILL_MODE_BUTTON = FullScreenButton.getInstance();
    protected static final MaterialSlider VOLUME_SLIDER = new MaterialSlider().setOrientation(Orientation.HORIZONTAL).setCurrentValue(50);

    public PlaybackControlPanel() {
        super(migLayout);
        setElevationDP(ELEVATION);
//        setBackgroundNoiseEnabled(true,0.7f);

        setPreferredSize(new Dimension(-1, PREFF_HEIGHT));
        controlPanel.setElevationDP(null); //Transparent background
        utilityPanel.setElevationDP(null); //Transparent background

        MATERIAL_PLAYBACK_BAR.setMaximumSize(PlaybackPanelConstraints.PlaybackBarMaxSize);
        MATERIAL_PLAYBACK_BAR.setMinimumSize(PlaybackPanelConstraints.PlaybackBarMinimumSize);

        controlPanel.add(LIKE_BUTTON, "cell 0 0");
        controlPanel.add(PREV_BUTTON, "cell 1 0");
        controlPanel.add(SHUFFLE_BUTTON, "cell 2 0");
        controlPanel.add(PLAY_BUTTON, "cell 3 0");
        controlPanel.add(REPEAT_BUTTON, "cell 4 0");
        controlPanel.add(NEXT_BUTTON, "cell 5 0");
        controlPanel.add(QUEUE_BUTTON, "cell 6 0");
        controlPanel.add(CHILL_MODE_BUTTON, "cell 7 0");

        utilityPanel.add(SPECTRUM_BUTTON, "w 30:30:20%, growX");
        utilityPanel.add(Box.createHorizontalBox(), "east, w 10!");
        utilityPanel.add(VOLUME_SLIDER, "east, w 30:100:60%, h 10, grow");
        utilityPanel.add(VOLUME_BUTTON, "east, w 30:30:20%, growX");
        add(MATERIAL_PLAYBACK_BAR, "h 40, alignX center, alignY top, grow, w 60%:60%:60%");

        add(audioInfoViewer, "west, grow, w 100:40%:40%");
        add(controlPanel, "h 50, alignX center, alignY top");
        add(utilityPanel, "east, w 100:40%:40%, grow");

        VOLUME_BUTTON.setVolumeSlider(VOLUME_SLIDER);
        addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                e.consume();

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                e.consume();
            }
        });
    }

    public static PlaybackControlPanel getInstance() {
        if (instance == null)
            instance = new PlaybackControlPanel();
        return instance;
    }

    public PlayerComponents getPlayerComponents() {
        return new PlayerComponents(audioInfoViewer, MATERIAL_PLAYBACK_BAR, PREV_BUTTON, SHUFFLE_BUTTON, PLAY_BUTTON, REPEAT_BUTTON, NEXT_BUTTON, LIKE_BUTTON, VOLUME_BUTTON);
    }

    private static final int RADIUS = PREFF_HEIGHT/3;
    private double x; // x-coordinate of the circle
    private double y; // y-coordinate of the circle

    private MaterialFixedTimer timer; // timer for animation
    private double time; // time value for Perlin noise
    private Random random; // random number generator

    private static final int OCTAVES = 1; // number of noise octaves

    //The amplitude variable is used to scale the Perlin noise values before applying them to the circle's position. It determines how far the circle can move from its starting position along the x and y axes.
    private static final double AMPLITUDE = 0.8;
    //By adjusting the value of the frequency variable, you can control the visual appearance of the circle's movement. Higher values will result in more intricate and rapid movement, while lower values will create smoother and more gradual movement.
    private static final double FREQUENCY = 0.00005; // frequency of the noise
    private static Color BlobColor;
    private static BufferedImage BlobImage;
    private boolean isAnimatedBlobInitialized = false;

    public void init() {
        random = new Random();
        time = 0.0;

        x = 400; // start at a random x-coordinate
        y = PREFF_HEIGHT /2d; // start at a random y-coordinate
        isAnimatedBlobInitialized = true;
        timer = new MaterialFixedTimer(1000f / 60) {
            @Override
            public void tick(float dt){
                // Update the circle position based on Perlin noise
                double noiseValueX = 0.0;
                double noiseValueY = 0.0;

                double amplitude = AMPLITUDE;
                double frequency = FREQUENCY;

                for (int i = 0; i < OCTAVES; i++) {
                    noiseValueX += PerlinNoise.noise(x * frequency, y * frequency, time) * amplitude;
                    noiseValueY += PerlinNoise.noise(y * frequency, x * frequency, time) * amplitude;

                    amplitude *= 0.5;
                    frequency *= 2.0;
                }

                int offsetX = (int) (getWidth() * noiseValueX);
                int offsetY = (int) (getHeight() * noiseValueY);
                // Apply interpolation to smooth out the movement
                double t = time % 1.0;
                double easedT = Interpolation.easeInOut(t) / 8.0;

                x = Interpolation.lerp(x, x + offsetX, easedT);
                y = Interpolation.lerp(y, y + offsetY, easedT);
                // Apply boundary checks
                if (x < RADIUS) {
                    x = RADIUS;
                } else if (x > getWidth() - RADIUS) {
                    x = getWidth() - RADIUS;
                }

                if (y < RADIUS) {
                    y = RADIUS;
                } else if (y > getHeight() - RADIUS) {
                    y = getHeight() - RADIUS;
                }

                repaint();

                time += dt;
            }
        };
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (BlobImage != null) {
            // Draw the circle at the current position
            Graphics2D g2d = (Graphics2D) g.create();
//            g2d.setColor(ThemeColors.getAccent());
            int x1 = (int) (x - RADIUS);
            int y1 = (int) (y - RADIUS);
            g2d.drawImage(BlobImage,x1, y1,null);
            g2d.dispose();
            Toolkit.getDefaultToolkit().sync();
        }
    }

    @Override
    public void updateTheme() {
        if(isAnimatedBlobInitialized && !ThemeColors.getAccent().equals(BlobColor)){
            BlobColor = ThemeColors.getAccent();
            int BlobSize = RADIUS * 2;
            int BlobImageSize = BlobSize * 2;
            int point = (BlobImageSize - BlobSize)/2;
            float alpha = 0.2f;
            BlobImage = new BufferedImage(BlobImageSize,BlobImageSize,BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D) BlobImage.getGraphics();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));
            g2d.setColor(BlobColor);
            g2d.fillOval(point,point,BlobSize,BlobSize);
            g2d.dispose();
            BlobImage = FAST_GAUSSIAN_BLUR.filter(BlobImage,null);
            Log.success("New blob generated successfully");
        }
        super.updateTheme();
    }

    @Override
    public void addNotify() {
        super.addNotify();
//        init();
    }
}
