package material.animation;

public interface ColorAnimationModel {
    float getProgress();
    float getDurationInMs();
    void forceCompleteAnimation();
    void incrementAnimationTime(float delta);

    boolean isCompleted();
}
