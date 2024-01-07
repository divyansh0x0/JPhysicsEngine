package material.component;

import material.component.enums.Orientation;
import material.listeners.ValueChangedListener;
import material.theme.ThemeColors;
import material.theme.enums.ElevationDP;
import material.utils.Log;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class MaterialSlider extends MaterialComponent implements MouseInputListener, MouseMotionListener {
    private final  ArrayList<ValueChangedListener> valueChangedListeners = new ArrayList<ValueChangedListener>();
    private ElevationDP elevationDP;
    private double maxValue = 100;
    private double minimumValue = 0;
    private double currentValue = 50;
    private Orientation orientation;
    private static final int barThickness = 8;
    private static final int cornerRadius = barThickness;
    private boolean isMousePressed;
    private Point MousePressedLocation;

    public MaterialSlider(Orientation orientation, double minimumValue, double maxValue) {
        super();
        this.minimumValue = minimumValue;
        this.maxValue = maxValue;
        setOrientation(orientation);
        addMouseListener(this);
        addMouseMotionListener(this);
        updateTheme();
    }

    public MaterialSlider() {
        this(Orientation.HORIZONTAL, 0, 100);
    }

    @Override
    public void updateTheme() {
        if (elevationDP == null)
            elevationDP = ElevationDP._6;
        animateBG(ThemeColors.getInstance().getColorByElevation(elevationDP));
        animateFG(ThemeColors.getInstance().getAccent());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int width, height, x, y, pX, pY, pWidth, pHeight; // p = progress
        if (Orientation.VERTICAL.equals(orientation)) {
            width = barThickness;
            height = getHeight();
            x = getWidth() / 2 - width / 2;
            y = 0;

            pWidth = width;
            pHeight = (int) Math.round((currentValue / maxValue) * height);

        } else {
            width = getWidth();
            height = barThickness;
            x = 0;
            y = getHeight() / 2 - height / 2;

            pWidth = (int) Math.round((currentValue / maxValue) * width);
            pHeight = height;
        }

        g2d.setColor(getBackground());
        RoundRectangle2D bg = new RoundRectangle2D.Double(x, y, width, height, cornerRadius, cornerRadius);
//        RoundRectangle2D progress = new RoundRectangle2D.Double(pX, pY, pWidth, pHeight, cornerRadius, cornerRadius);
        //Background
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g2d.fill(bg);

        g2d.setClip(bg);

        //Progress indicator
        g2d.setColor(getForeground());
        Rectangle progress = new Rectangle((int)bg.getX(), (int)bg.getY(), pWidth,pHeight);
        g2d.setClip(progress);
        g2d.fill(bg);

        //border
//        float StrokeWidth = 1f;
//        g2d.setClip(null);
//        g2d.setStroke(new BasicStroke(StrokeWidth));
//        g2d.setColor(ColorUtils.darken(getBackground(),10));
//        g2d.draw(new RoundRectangle2D.Double(x - StrokeWidth,y-StrokeWidth,width + StrokeWidth, height + StrokeWidth, cornerRadius, cornerRadius));

    }

    public ElevationDP getElevationDP() {
        return elevationDP;
    }

    public MaterialSlider setElevationDP(ElevationDP elevationDP) {
        this.elevationDP = elevationDP;
        repaint();
        return this;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public synchronized MaterialSlider setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        repaint();
        return this;
    }

    public double getMinimumValue() {
        return minimumValue;
    }

    public synchronized MaterialSlider setMinimumValue(double minimumValue) {
        this.minimumValue = minimumValue;
        repaint();
        return this;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public synchronized MaterialSlider setOrientation(Orientation orientation) {
        if (this.orientation != orientation) {
            this.orientation = orientation;
            repaint();
        }
        return this;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public synchronized MaterialSlider setCurrentValue(double currentValue) {
        if(currentValue == this.currentValue)
            return this;
        this.currentValue = Math.max(Math.min(currentValue, maxValue), 0);
        repaint();
        valueChanged(this.currentValue);
        return this;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(isEnabled()) {
            isMousePressed = true;
            calculateCurrentValue(e);
        }
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        isMousePressed = false;

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isMousePressed)
            calculateCurrentValue(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public void onValueChange(ValueChangedListener listener) {
        if (!valueChangedListeners.contains(listener))
            valueChangedListeners.add(listener);
    }

    private synchronized void valueChanged(double newVal) {
        for (ValueChangedListener listener : valueChangedListeners) {
            listener.valueChanged(newVal);
        }
    }

    private void calculateCurrentValue(MouseEvent e) {
        double val = 0;
        if (Orientation.HORIZONTAL.equals(orientation)) {
            double percentage = (double) e.getX() / (double) getWidth();
            val = (percentage * (maxValue - minimumValue) + minimumValue);
        } else {
            double y = (getHeight()) - e.getY();
            double percentage = y / (double) getHeight();
            val = (percentage * (maxValue - minimumValue) + minimumValue);
            setCurrentValue(val);
        }
        val = Math.max(Math.min(maxValue, val), minimumValue);
        setCurrentValue(val);
    }

    public int getBarThickness() {
        return barThickness;
    }
}
