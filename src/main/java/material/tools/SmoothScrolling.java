package material.tools;

import material.listeners.KeyboardKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;

public class SmoothScrolling implements MouseWheelListener {
    private final JScrollPane _scrollingPane;
    private boolean isEnabled = true;
    private float wheelDelta = 0;

    private float totalScroll = 0;
    private static final float STEP_SIZE = 0.1f;
    private static final float deltaScrollAmount = 50;
    private static final float minScrollRatio = 0.20f;

    public SmoothScrolling(JScrollPane scrollpane) {
        this._scrollingPane = scrollpane;
        addListeners();
    }

    public void addListeners() {
        _scrollingPane.setWheelScrollingEnabled(false);
        if (Arrays.stream(_scrollingPane.getMouseWheelListeners()).noneMatch(mouseListener -> mouseListener == this))
            _scrollingPane.addMouseWheelListener(this);

        ScrollAnimationTimer.getInstance().add(this);
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float newWheelDelta = (float) e.getPreciseWheelRotation();
        boolean sameDirection = newWheelDelta * wheelDelta >= 0;
        if (sameDirection) {
            totalScroll = (float) (Math.abs(totalScroll) + deltaScrollAmount * Math.abs(e.getPreciseWheelRotation()));
        } else {
            totalScroll = 0;

        }
        wheelDelta = newWheelDelta;
    }

    private void removeListeners() {
        _scrollingPane.setWheelScrollingEnabled(false);
        _scrollingPane.removeMouseWheelListener(this);

        ScrollAnimationTimer.getInstance().remove(this);
    }

    protected void tick() {
        float changeInPos;
        //I am not comparing float values with zero because that is a bad idea
        if (wheelDelta > 0 && totalScroll > 0.1) {
            float minScroll = minScrollRatio * totalScroll;
            changeInPos = Math.max(wheelDelta * totalScroll * STEP_SIZE,minScroll);
            totalScroll -= changeInPos;
        } else if (wheelDelta < 0 && totalScroll > 0.1) {
            float minScroll = minScrollRatio * totalScroll;
            changeInPos = Math.min(wheelDelta * totalScroll * STEP_SIZE,-minScroll);
            totalScroll += changeInPos;
        } else {
            totalScroll = 0;
            changeInPos = 0;
        }
        if(totalScroll > 0) {
            if (!KeyboardKeyListener.isKeyPressed(KeyEvent.VK_SHIFT)) {//Vertical scrolling
                int currentOffset = _scrollingPane.getVerticalScrollBar().getValue();
                int y = Math.max(0, (int) Math.round(currentOffset + changeInPos));
                _scrollingPane.getVerticalScrollBar().setValue(y);
            } else {
                int currentOffset = _scrollingPane.getHorizontalScrollBar().getValue();
                _scrollingPane.getHorizontalScrollBar().setValue(Math.max(0, (int) Math.round(currentOffset + changeInPos)));
            }
            SwingUtilities.invokeLater(()->{
                _scrollingPane.getViewport().repaint(_scrollingPane.getViewport().getVisibleRect());
            });
            Toolkit.getDefaultToolkit().sync();
        }
    }

    public void setEnabled(boolean b) {
        isEnabled = b;
        if (isEnabled)
            addListeners();
        else
            removeListeners();
    }

    public boolean isEnabled() {
        return isEnabled;
    }

}
