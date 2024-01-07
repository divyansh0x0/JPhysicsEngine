package material.animation;

import material.MaterialParameters;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class AnimationLibrary {
    private static final int ANIMATION_DURATION = (int) MaterialParameters.COLOR_ANIMATION_DURATION.toNanos(); // Duration of animation in nanos
    private static final int FPS = 60; // Number of animation steps
    private static final Object componentAnimationsLock = new Object();
    private static final int DEFAULT_SIZE_ARR = 10;
    private static MaterialFixedTimer timer;
    private static final ConcurrentHashMap<JComponent, ArrayList<ColorAnimationModel>> componentAnimations = new ConcurrentHashMap<>(100);

    static {
        //A permanent running timer which increments on going animations at every tick

        timer = new MaterialFixedTimer(1000f / FPS) {
            @Override
            public void tick(float delta) {
                if (!componentAnimations.isEmpty()) {
                    synchronized (componentAnimations) {
                        Collection<ArrayList<ColorAnimationModel>> values = componentAnimations.values();
                        ArrayList<?>[] animationModels = values.toArray(new ArrayList[0]);
                        for (int i = animationModels.length - 1; i >= 0; i--) {
                            var animationsArr = animationModels[i];
                            if (animationsArr == null)
                                continue;
                            for (int j = animationsArr.size() - 1; j >= 0; j--) {
                                ColorAnimationModel animation = (ColorAnimationModel) animationsArr.get(j);
                                if (animation == null)
                                    continue;
                                if (animation.getProgress() < 1f) {
                                    animation.incrementAnimationTime(delta);
                                } else {
                                    animationsArr.remove(j);
                                }
                            }
                        }
                    }
                }
            }
        };
        timer.start();
    }

    public static void animateBackground(JComponent component, Color toColor, float durationMs) {
        if (component.isVisible() && toColor != null && component.getBackground() != null && !component.getBackground().equals(toColor)) {
            BackgroundAnimation animation = getAnimation(component, BackgroundAnimation.class);
            if (animation == null) {
                animation = new BackgroundAnimation(component, durationMs);
                ArrayList<ColorAnimationModel> arr = componentAnimations.get(component);
                if (arr == null) {
                    arr = new ArrayList<>(DEFAULT_SIZE_ARR);
                    arr.add(animation);
                    componentAnimations.put(component, arr);
                } else
                    arr.add(animation);
            } else {
                animation.prepareForNewAnimation();
            }
            animation.toColor(toColor);
        } else
            component.setBackground(toColor);
    }

    public static void animateForeground(JComponent component, Color toColor, float durationMs) {
        if(component.isVisible() && toColor != null && component.getForeground() != null && !component.getForeground().equals(toColor)) {
            ForegroundAnimation animation = getAnimation(component, ForegroundAnimation.class);
            if (animation == null) {
                animation = new ForegroundAnimation(component,durationMs);
                ArrayList<ColorAnimationModel> arr = componentAnimations.get(component);
                if(arr == null){
                    arr= new ArrayList<>(DEFAULT_SIZE_ARR);
                    arr.add(animation);
                    componentAnimations.put(component,arr);
                }
                else
                    arr.add(animation);
            } else {
                animation.prepareForNewAnimation();
            }
            animation.toColor(toColor);
        }else{
            component.setForeground(toColor);
        }
    }

    private static <model> model getAnimation(JComponent component, Class<model> animationClass) {
        ArrayList<ColorAnimationModel> animations = componentAnimations.get(component);
        if(animations == null)
            return null;
        for (int i = animations.size() - 1; i >= 0; --i) {
            ColorAnimationModel animationModel = animations.get(i);
            if (animationModel.getClass().isInstance(animationClass.getSimpleName())) {
                return animationClass.cast(animationModel);
            }
        }
        return null;
    }


    private static Color interpolateColor(Color fromColor, Color toColor, float progress) {
        return Interpolator.lerpRBG(fromColor, toColor, progress);
    }
}
