package material.animation;

import javax.swing.*;
import java.awt.*;

public class ForegroundAnimation implements ColorAnimationModel {
    private final JComponent component;
    private float duration;
    private Color from;
    private Color to;
    private float progress;
    private float currTime;
    private boolean isCompleted = false;
    public void setDuration(float durationMs) {
        duration = durationMs;
    }
    public ForegroundAnimation(JComponent component, float duration) {
        this.component = component;
        this.from = component.getForeground();
        this.duration = duration;
    }

    public void reuse(Color toColor, float durationMs) {
        this.from = component.getForeground();
        this.duration = durationMs;
        this.to = toColor;
        this.progress = 0f;
        this.currTime = 0;
        this.isCompleted = false;
    }

    public void toColor(Color to) {
        this.from = component.getBackground();
        this.to = to;
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    public float getDurationInMs() {
        return duration;
    }

    public void setProgress(float p) {
        if(isCompleted)
            return;
        if (p > 1f)
            p = 1f;
        progress = p;
        if(p < 1f) {
            if (to != null) {
                if (from == null) {
                    SwingUtilities.invokeLater(() -> {
                        this.component.setForeground(to);

                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        this.component.setForeground(Interpolator.lerpRBG(from, to, progress));
                    });
                }
                this.component.repaint();
            }
        }
        else {
            SwingUtilities.invokeLater(() -> {

                this.component.setForeground(to);
                isCompleted = true;
            });
        }
    }

    @Override
    public void forceCompleteAnimation() {
        setProgress(1f);
    }

//    @Override
//    public void prepareForNewAnimation() {
////        forceCompleteAnimation();
//        progress = 0f;
//        currTime = 0f;
//        from = null;
//        to = null;
//    }

    @Override
    public void incrementAnimationTime(float delta) {
        if(!isCompleted()) {
            if (to != null && to.equals(from))
                forceCompleteAnimation();
            else {
                currTime += delta;
                setProgress(currTime / duration);
            }
        }
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }


    @Override
    public String toString() {
        return "BackgroundAnimation{" +
                "component=" + component +
                ", progress=" + progress +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
