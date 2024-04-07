package material.animation;

import material.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class AnimationLibrary {
    private static final byte BACKGROUND_ANIMATION_INDEX = 0;
    private static final byte FOREGROUND_ANIMATION_INDEX = 1;
    private static final byte NUMBER_OF_ANIMATION_TYPES = 2;
    private static final byte FPS = 60; // Number of animation steps
    private static MaterialFixedTimer timer;
    private static final ConcurrentHashMap<JComponent, ColorAnimationModel[]> componentAnimations = new ConcurrentHashMap<>(0);

    static {
        //A permanent running timer which increments on going animations at every tick

        timer = new MaterialFixedTimer(1000f / FPS) {
            @Override
            public void tick(float delta) {
                if (!componentAnimations.isEmpty()) {
                    synchronized (componentAnimations) {
                        Collection<ColorAnimationModel[]> animationModels = componentAnimations.values();

                        for (ColorAnimationModel[] animationsArr : animationModels) {
                            if (animationsArr == null)
                                continue;
                            for (int j = animationsArr.length - 1; j >= 0; j--) {
                                ColorAnimationModel animation = animationsArr[j];
                                if (animation == null)
                                    continue;
                                animation.incrementAnimationTime(delta);
                                if (animation.isCompleted()) {
                                    animation.forceCompleteAnimation();
                                }
                            }
                        }
                    }
                }
            }
        };
        timer.start();
    }

    //TODO FIX THIS. IT BREAKS WHEN SAME COMPONENT IS ANIMATED MULTIPLE TIMES
    public static synchronized void animateBackground(JComponent component, Color toColor, float durationMs) {
//        Log.info("preparing animation for " + component);
//        if (component.isVisible() && toColor != null && component.getBackground() != null && !component.getBackground().equals(toColor)) {
            BackgroundAnimation animation = getBackgroundAnimation(component, durationMs);
            animation.reuse(toColor,durationMs);
//        }
//        else{
//            SwingUtilities.invokeLater(()->{
//                component.setBackground(toColor);
//            });
//            Log.info("[static]" + component + " to " + toColor);
//
//        }
    }

    private static BackgroundAnimation getBackgroundAnimation(JComponent component, float durationMs) {
        ColorAnimationModel[] animationModels = componentAnimations.get(component);

        if(animationModels != null) {
            for (ColorAnimationModel animation : animationModels) {
                if (animation instanceof BackgroundAnimation)
                    return (BackgroundAnimation) animation;
            }
        }
        else {
            animationModels = new ColorAnimationModel[NUMBER_OF_ANIMATION_TYPES];
        }
        BackgroundAnimation animation = new BackgroundAnimation(component, durationMs);

        animationModels[BACKGROUND_ANIMATION_INDEX] = animation;

        componentAnimations.put(component, animationModels);
        return animation;
    }


    public static synchronized void animateForeground(JComponent component, Color toColor, float durationMs) {
        ForegroundAnimation animation = getForegroundAnimation(component, durationMs);
        animation.reuse(toColor,durationMs);
    }
    private static ForegroundAnimation getForegroundAnimation(JComponent component, float durationMs) {
        ColorAnimationModel[] animationModels = componentAnimations.get(component);

        if(animationModels != null) {
            for (ColorAnimationModel animation : animationModels) {
                if (animation instanceof ForegroundAnimation)
                    return (ForegroundAnimation) animation;
            }
        }
        else {
            animationModels = new ColorAnimationModel[NUMBER_OF_ANIMATION_TYPES];
        }
        ForegroundAnimation animation = new ForegroundAnimation(component, durationMs);

        animationModels[FOREGROUND_ANIMATION_INDEX] = animation;

        componentAnimations.put(component, animationModels);
        return animation;
    }
    //TODO fix this unsafe code by making it safe
//    private static <model> model getAnimation(JComponent component, Class<model> animationClass) {
//        ArrayList<ColorAnimationModel> animations = componentAnimations.get(component);
//        if (animations == null)
//            return null;
//        for (int i = animations.size() - 1; i >= 0; --i) {
//            ColorAnimationModel animationModel = animations.get(i);
//            if (animationModel.getClass().getSimpleName().equals(animationClass.getSimpleName()))
//                return (model) animationModel;
//        }
//        return null;
//    }


    private static void putNewAnimation(BackgroundAnimation animation) {

    }
}

