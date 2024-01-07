package material.tools;

import material.animation.MaterialFixedTimer;

import java.util.ArrayList;

public class ScrollAnimationTimer extends MaterialFixedTimer {
    private static final int FPS = 60;
    private static final int DELAY = 1000 / FPS;
    private static ScrollAnimationTimer instance;
    private final ArrayList<SmoothScrolling> scrollerList = new ArrayList<>(10);

    //    private final ArrayList<SmoothScrollingV2> scrollerList2 = new ArrayList<>(10);
    public ScrollAnimationTimer() {
        super(DELAY);
        start();
    }

    public static ScrollAnimationTimer getInstance() {
        if (instance == null)
            instance = new ScrollAnimationTimer();
        return instance;
    }

    public void add(SmoothScrolling scroller) {
        if (!scrollerList.contains(scroller)) {
            scrollerList.add(scroller);
            paintersUpdated();
        }
    }

    public void remove(SmoothScrolling scroller) {
        scrollerList.remove(scroller);
        paintersUpdated();
    }

    public void removeAll() {
        scrollerList.clear();
        paintersUpdated();
    }

    private void paintersUpdated() {
        if (!scrollerList.isEmpty())
            start();
    }

    @Override
    public void tick(float dt) {
        scrollerList.forEach(SmoothScrolling::tick);
    }
}
