package material.animation;

import java.util.ArrayList;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;

public class AnimationTimer {
    int Duration;
    private Animator animator;
    private ArrayList<AnimationListener> listeners = new ArrayList<>();

    public AnimationTimer(int duration) {
        Duration = duration;
        this.animator = new Animator(duration);
        animator.addTarget(new TimingTarget() {
            @Override
            public void timingEvent(float v) {

            }

            @Override
            public void begin() {
                startedCallback();
            }

            @Override
            public void end() {
                stoppedCallback();
            }

            @Override
            public void repeat() {

            }
        });
    }

    public AnimationTimer() {
        this(100);
    }

    public void start() {
        animator.start();
        stoppedCallback();
    }

    public void stop() {
        animator.stop();
        stoppedCallback();
    }

    public void addListener(AnimationListener animationListener) {
        if (!listeners.contains(animationListener))
            listeners.add(animationListener);
    }

    public void stoppedCallback() {
        listeners.forEach(AnimationListener::onEnd);
    }

    public void startedCallback() {
        listeners.forEach(AnimationListener::onStart);
    }
}
