package material.borders;

import java.awt.*;

import material.MaterialParameters;

import javax.swing.border.*;

public class RoundedBorder implements Border {
    private int thickness;
    private int cornerRadius;
    private Color color;
    public RoundedBorder(int thickness, int cornerRadius, Color c){
        this.thickness = thickness;
        this.cornerRadius = cornerRadius;
        this.color = c;
    }
    public RoundedBorder(int cornerRadius, Color c){
        this(1,cornerRadius, c);
    }
    public RoundedBorder(Color c){
        this(1, MaterialParameters.CORNER_RADIUS, c);
    }
    public RoundedBorder(){
        this(1, MaterialParameters.CORNER_RADIUS, Color.BLACK);
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        Rectangle bounds = c.getBounds();
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));
        g2d.drawRoundRect(0,0,bounds.width - thickness,bounds.height - thickness, cornerRadius,cornerRadius);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness,thickness,thickness,thickness);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
