package material.animation;

import java.awt.*;

public class ForegroundAnimation implements ColorAnimationModel {
    private final Component component;
    private final float duration;
    private Color from;
    private Color to;
    private float progress;
    private float currTime;

    public ForegroundAnimation(Component component, float duration) {
        this.component = component;
        this.from = component.getBackground();
        this.duration = duration;
    }

    public void toColor(Color to) {
        from = component.getForeground();
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
        if (p > 1f)
            p = 1f;
        if (to != null) {
            if (from == null) {
                this.component.setForeground(to);
                p = 1f;
            } else {
                this.component.setForeground(Interpolator.lerpRBG(from, to, p));
            }
        }
        progress = p;
    }

    @Override
    public void forceCompleteAnimation() {
        setProgress(1f);
        this.component.setBackground(to);
    }

    @Override
    public void prepareForNewAnimation() {
        progress = 0f;
        currTime = 0f;
        from = null;
        to = null;
    }

    @Override
    public void incrementAnimationTime(float delta) {
        if (to != null && to.equals(from))
            forceCompleteAnimation();
        else {
            currTime += delta;
            setProgress(currTime / duration);
        }
    }
    @Override
    public boolean isCompleted() {
        return progress >= 1.0f;
    }
}
