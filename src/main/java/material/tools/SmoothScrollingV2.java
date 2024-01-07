package material.tools;

import material.listeners.KeyboardKeyListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class SmoothScrollingV2 implements MouseWheelListener {

    private static final double STEP = 0.0004;
    private static final double MAX_FORCE = 0.015;
    private static final double PSEUDO_FRICTION = 0.9;
    private static final double ACC_REDUCTION_FACTOR = 0.9;
    private static final double SPEED_THRESHOLD = 0.01;
    private JScrollPane _scrollingPane;
    private double _velocity = 0.0;
    private double _force = 0.0;
    private long _lastUpdate = 0;
    private long _lastScroll = 0;
    private boolean isEnabled;
    private double _lastVelocity = 0.0;


    public SmoothScrollingV2(JScrollPane scrollpane){
        this._scrollingPane = scrollpane;
        addListeners();
    }

    public void addListeners(){
        _scrollingPane.setWheelScrollingEnabled(false);
        _scrollingPane.addMouseWheelListener(this);

//        ScrollAnimationTimer.getInstance().add(this);
    }


  @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (_lastScroll == 0) {
            _lastScroll = System.nanoTime();
            return;
        }

        long currentTime = System.nanoTime();
        double elapsedMillis = (currentTime - _lastScroll) * 1.0e-6;
        _lastScroll = currentTime;

        if (elapsedMillis == 0) {
            return;
        }

        double wheelDelta = e.getPreciseWheelRotation();
        boolean sameDirection = _velocity * wheelDelta >= 0;

        if (sameDirection) {
            double currentStep = wheelDelta * STEP;
            _force += currentStep + currentStep / (elapsedMillis * 0.0007);

            // Limit the magnitude of the force to MAX_FORCE.
            double forceMagnitude = Math.abs(_force);
            if (forceMagnitude > MAX_FORCE) {
                _force *= MAX_FORCE / forceMagnitude;
            }
        } else {
            _force = 0;
            _velocity = 0;
        }
    }

    private void removeListeners(){
        _scrollingPane.setWheelScrollingEnabled(false);
        _scrollingPane.removeMouseWheelListener(this);

//        ScrollAnimationTimer.getInstance().remove(this);
    }
    protected void tick() {
        if (_lastUpdate == 0) {
            _lastUpdate = System.nanoTime();
            return;
        }

        long currentTime = System.nanoTime();
        double elapsedMillis = (currentTime - _lastUpdate) * 1.0e-6;
        _lastUpdate = currentTime;

        double exponent = elapsedMillis / 16.0;

        _force *= Math.pow(ACC_REDUCTION_FACTOR, exponent);
        _velocity += _force * elapsedMillis;
        _velocity *= Math.pow(PSEUDO_FRICTION, exponent);

        double speed = Math.abs(_velocity);
        if (speed >= SPEED_THRESHOLD) {
            if (!KeyboardKeyListener.isKeyPressed(KeyEvent.VK_SHIFT)) {//Vertical scrolling
                int currentOffset = _scrollingPane.getVerticalScrollBar().getValue();
                int y = Math.max(0, (int) Math.round(currentOffset + _velocity * elapsedMillis));
                _scrollingPane.getVerticalScrollBar().setValue(y);
                _scrollingPane.getViewport().repaint();
                Toolkit.getDefaultToolkit().sync();
            } else {
                int currentOffset = _scrollingPane.getHorizontalScrollBar().getValue();
                _scrollingPane.getHorizontalScrollBar().setValue(Math.max(0, (int) Math.round(currentOffset + _velocity * elapsedMillis)));
                _scrollingPane.getViewport().paintImmediately(_scrollingPane.getViewport().getVisibleRect());
                Toolkit.getDefaultToolkit().sync();
            }
        } else {
            _velocity = 0.0;
        }
    }

    public void setEnabled(boolean b) {
         isEnabled = b;
         if(isEnabled)
             addListeners();
         else
             removeListeners();
    }
}
