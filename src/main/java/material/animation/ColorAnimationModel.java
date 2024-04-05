package material.animation;

import javax.swing.*;

public interface ColorAnimationModel {
    float getProgress();
    float getDurationInMs();
    void forceCompleteAnimation();
    void incrementAnimationTime(float delta);

    boolean isCompleted();

}
