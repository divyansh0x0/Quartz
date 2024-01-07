package app.components.buttons.playback;

import app.components.listeners.VolumeChangedListener;
import app.settings.constraints.ControlButtonConstraints;
import material.component.MaterialSlider;
import material.constraints.LayoutConstraints;
import material.listeners.ValueChangedListener;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import static app.components.Icons.*;

public class VolumeButton extends PlaybackButton implements FocusListener, ValueChangedListener {
    private static final double[] volumeThresholds = {0.15D, 0.60D, 1D};
    private static final double MINIMUM_VOLUME = 0;
    private ArrayList<VolumeChangedListener> volumeChangedListeners = new ArrayList<VolumeChangedListener>();
    private static final int VolumeSliderWidth = 100;
    private static final double MAXIMUM_VOLUME = 1;

    private static final float VolumeChangeSteps = 0.1f;//10%
    private static final int padding = LayoutConstraints.PADDING;

    private MaterialSlider volumeSlider;

    public VolumeButton() {
        super(VOLUME_MEDIUM);
        setMaximumSize(ControlButtonConstraints.BUTTON_SIZE);
        setMinimumSize(ControlButtonConstraints.BUTTON_SIZE);
        setPreferredSize(ControlButtonConstraints.BUTTON_SIZE);
        setIconSizeRatio(0.90);
        setPadding(ControlButtonConstraints.PADDING);

        setFocusable(true);
        addFocusListener(this);
        handleSliderValueUpdate();
    }

    private void handleSliderValueUpdate() {
        if (volumeSlider != null) {
            double volume = volumeSlider.getCurrentValue();
            if (volume == 0d) {
                setIcon(VOLUME_OFF);
                setToolTipText("Unmute");
                setActive(true);
            } else {
                if (volume > 0d && volume <= volumeThresholds[0]) {
                    setIcon(VOLUME_LOW);
                } else if (volume > volumeThresholds[0] && volume < volumeThresholds[1]) {
                    setIcon(VOLUME_MEDIUM);
                } else
                    setIcon(VOLUME_HIGH);
                setToolTipText("Mute");
                oldVolume = volume;
                setActive(false);
            }
        }
    }

    double oldVolume = 0d;

    @Override
    public synchronized void changeUI(boolean isActive) {
        if (volumeSlider != null) {
            if (isActive) {
                oldVolume = volumeSlider.getCurrentValue() != 0 ? volumeSlider.getCurrentValue() : 0.5;
                setVolume(0);
            } else
                setVolume(oldVolume);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        setActive(true);
    }

    @Override
    public void focusLost(FocusEvent e) {
        setActive(false);

    }


    private Popup popup;
    @Override
    public void valueChanged(double newValue) {
        volumeChanged(newValue);
        handleSliderValueUpdate();
    }

    public void setVolume(double volume) {
        volumeSlider.setCurrentValue(volume);
    }

    public void onVolumeChange(VolumeChangedListener volumeChangedListener) {
        if (!volumeChangedListeners.contains(volumeChangedListener))
            volumeChangedListeners.add(volumeChangedListener);
    }

    private void volumeChanged(double newVolume) {
        for (VolumeChangedListener listener : volumeChangedListeners) {
            listener.volumeChanged(newVolume);
        }
    }

    @Override
    public void addNotify() {
        handleSliderValueUpdate();
        super.addNotify();
    }

    public MaterialSlider getVolumeSlider() {
        return volumeSlider;
    }

    public void setVolumeSlider(MaterialSlider volumeSlider) {
        this.volumeSlider = volumeSlider;
        volumeSlider.setMinimumValue(MINIMUM_VOLUME);
        volumeSlider.setMaxValue(MAXIMUM_VOLUME);
        volumeSlider.onValueChange(this);
    }
}
