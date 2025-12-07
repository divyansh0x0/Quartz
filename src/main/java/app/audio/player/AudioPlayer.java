package app.audio.player;

import app.audio.AudioData;
import material.utils.OsInfo;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.PlayBin;

import javax.sound.sampled.FloatControl;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


class AudioPlayer implements AudioPlayerModel {
    private static final float DEFAULT_MASTER_GAIN_VALUE = 0.0f;
    private AudioData currentAudio;
    private final Object lock = new Object();
    private final PlayBin playBin;
    private final ArrayList<Runnable> MediaEndedListeners = new ArrayList<>();
    private final ArrayList<AudioPlayerExceptionListener> audioPlayerExceptionListeners = new ArrayList<>();
    private final ArrayList<AudioVisualizerListener> visualizerListeners = new ArrayList<>();
    private static final String SPECTRUM_ELEMENT_NAME = "spectrum";
    private int THRESHOLD;
    private int SPECTRUM_BANDS;
    private float[] MAGNITUDES;
    private final Element SPECTRUM;
    // Define a threshold interval in milliseconds to control the execution rate
    private long lastExecutionTime = 0;
    private static final short THROTTLE_INTERVAL = (short) ((short) 1000 / OsInfo.getRefreshRate()); // Adjust this value as needed
    private boolean isVisualizerSamplingEnabled;

    //JNA uses the platform specific encoding by default so to allow UTF-8, encoding needs to be set manually
    static {
        System.setProperty("jna.encoding", "UTF-8");
    }

    private boolean isDisposed;

    public AudioPlayer() {

        GStreamerConfig.configurePaths();



        Gst.init(Version.BASELINE, "Quartz");
        this.SPECTRUM = ElementFactory.make(SPECTRUM_ELEMENT_NAME, SPECTRUM_ELEMENT_NAME);
        playBin = new PlayBin("Quartz-Playbin");
        playBin.set("audio-filter", SPECTRUM);
        playBin.getBus().connect((Bus.ERROR) (source, code, message) -> handleError(code, message));
        playBin.getBus().connect((Bus.EOS) source -> handleMediaEnd());
        playBin.getBus().connect("element", (bus, message) -> {
            if (isVisualizerSamplingEnabled) {
                // Get the current time
                long currentTime = System.currentTimeMillis();

                // Check if the time since the last execution is greater than the threshold interval
                if (currentTime - lastExecutionTime >= THROTTLE_INTERVAL) {
                    if (SPECTRUM_ELEMENT_NAME.equals(message.getSource().getName())) {
                        setNativeArrayOfMagnitudes(message.getStructure().getValues(Float.class, "magnitude"));
                    }

                    // Update the last execution time
                    lastExecutionTime = currentTime;
                }
            }
        });
        THRESHOLD = (int) SPECTRUM.getPropertyDefaultValue("threshold");
        SPECTRUM_BANDS = ((Long) SPECTRUM.getPropertyDefaultValue("bands")).intValue();


        MAGNITUDES = new float[SPECTRUM_BANDS];

        setVolume(1);
    }


    /*
    Data handlers
     */

    private void setNativeArrayOfMagnitudes(List<Float> magnitudeslist) {
        if (playBin.isPlaying() && !magnitudeslist.isEmpty()) {
            // convert levels for display //
            for (int i = 0; i < magnitudeslist.size(); i++) {
                MAGNITUDES[i] = magnitudeslist.get(i) - THRESHOLD;
            }
            magnitudeslist.clear();
            handleVisualizerDataUpdate(MAGNITUDES);
        }

    }


    private float getRequiredVolumeChangeInDecibels(FloatControl control, double level) {
        final float min = control.getMinimum() * 0.5f;
        float range = Math.abs(control.getMinimum()) + Math.abs(control.getMaximum());
        float levelInRange = (float) (range * level);
        //        Log.info("current value: " + control.getValue() + "| value required: " + requiredValue + " | required value change: " + requiredValueChange);
        return Math.min(min + levelInRange, DEFAULT_MASTER_GAIN_VALUE);
    }


    /*
        FROST PLAYER MODEL
    */
    @Override
    public void load(AudioData audio) throws FileNotFoundException {
        if (audio != null) {
            currentAudio = audio;
            playBin.stop();
            if (audio.getFile().exists())
                playBin.setURI(audio.getFile().toURI());
            else throw new FileNotFoundException(audio.getFile().getAbsolutePath() + " not found!");
        }
    }

    @Override
    public void play() {
        if (currentAudio != null) {
            playBin.play();
        }
    }

    @Override
    public synchronized void pause() {
        playBin.pause();
    }

    @Override
    public synchronized void stop() {
        playBin.stop();
    }

    @Override
    public synchronized void seek(long newPosMs) {
        if (getTotalTimeMs() > 0) {
            playBin.seek(newPosMs, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public synchronized long getTotalTimeMs() {
        return playBin.queryDuration(TimeUnit.MILLISECONDS); //returns -1 nanos if query failed
    }

    @Override
    public long getCurrentTimeMs() {
        return playBin.queryPosition(TimeUnit.MILLISECONDS);
    }

    @Override
    public double getVolume() {
        return playBin.getVolume();
    }

    @Override
    public void setVolume(double newVolume) {
        if (newVolume != playBin.getVolume())
            playBin.setVolume(newVolume);
    }

    @Override
    public void dispose() {
        synchronized (playBin) {
            isDisposed = true;
            playBin.stop();
            playBin.dispose();
            Gst.quit();
            Gst.deinit();
        }
    }




    /*
    LISTENERS HANDLING
     */
    private synchronized void handleMediaEnd() {
        for (Runnable mediaEndedListener : MediaEndedListeners) {
            mediaEndedListener.run();
        }
    }

    private void handleVisualizerDataUpdate(float[] newSamples) {
        synchronized (lock) {
            for (AudioVisualizerListener listener : visualizerListeners) {
                listener.visualizerDataUpdated(newSamples, THRESHOLD);
            }
        }
    }

    private void handleError(int code, String message) {
        for (AudioPlayerExceptionListener l : audioPlayerExceptionListeners)
            l.handleException(new AudioControllerException(code, message));
    }

    /*
     ADD LISTENERS
     */
    public synchronized void addExceptionListener(AudioPlayerExceptionListener listener) {
        if (!audioPlayerExceptionListeners.contains(listener))
            audioPlayerExceptionListeners.add(listener);
    }

    public synchronized void addVisualizerDataListener(AudioVisualizerListener listener) {
        if (!visualizerListeners.contains(listener))
            visualizerListeners.add(listener);
    }

    /*
    REMOVE LISTENERS
     */
    private synchronized void removeExceptionListener(AudioPlayerExceptionListener exceptionListener) {
        audioPlayerExceptionListeners.remove(exceptionListener);
    }

    public void addMediaEndedListener(Runnable mediaEnded) {
        MediaEndedListeners.add(mediaEnded);
    }

    /*
    ------------------------------------------------------------------------------------------------------------------
                                                SETTERS
    --------------------------------------------------------------------------------------------------------------------
     */
    public void setThreshold(int THRESHOLD) {
        this.THRESHOLD = THRESHOLD;
        this.SPECTRUM.set("threshold", THRESHOLD);
    }

    public void setSpectrumBands(int SPECTRUM_BANDS) {
        this.SPECTRUM_BANDS = SPECTRUM_BANDS;
        if (this.MAGNITUDES == null)
            this.MAGNITUDES = new float[SPECTRUM_BANDS];

        this.SPECTRUM.set("bands", SPECTRUM_BANDS);
    }

    /*
    ------------------------------------------------------------------------------------------------------------------
                                                    GETTERS
    --------------------------------------------------------------------------------------------------------------------
     */
    public int getThreshold() {
        return THRESHOLD;
    }

    public long getSpectrumBands() {
        return SPECTRUM_BANDS;
    }

    public void enableVisualizerSampling(boolean b) {
        this.isVisualizerSamplingEnabled = b;
    }

    public boolean isVisualizerSamplingEnabled() {
        return isVisualizerSamplingEnabled;
    }

    public boolean isDisposed() {
        return isDisposed;
    }
}
