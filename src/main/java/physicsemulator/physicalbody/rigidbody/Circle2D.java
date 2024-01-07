package physicsemulator.physicalbody.rigidbody;

import physicsemulator.engine.Scene;
import physicsemulator.physicalbody.RigidObject;
import physicsemulator.utils.Vector2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Circle2D extends RigidObject {
    private int radius;

    public Circle2D(int radius, Color c, Vector2D position, double mass) {
        this.radius = radius;
        this.setColor(c);
        setPosition(position);
        setMass(mass);
    }

    public Circle2D(int radius, Color c, Vector2D position) {
        this(radius, c, position, 10);
    }

    public Circle2D(int radius, Color c) {
        this(radius, c, Vector2D.ZERO, 10);
    }

    @Override
    public void renderObject(Graphics2D g, Scene scene) {
        super.renderObject(g, scene);

        Point p = scene.getScenePointFromPositionVector(getPosition().add(-getRadius(), getRadius()));
//        movePointToCenter(p);
        g.setColor(getColor());
        float size = (float) (radius * 2 * scene.getScale());
        Ellipse2D ellipse2D = new Ellipse2D.Float(p.x, p.y, size, size);

        g.fill(ellipse2D);

        g.setStroke(new BasicStroke(3f));
        g.drawString(getMass() + "kg", p.x, p.y);
    }

    private boolean isRectIntersecting(Vector2D btl, Vector2D tpl) {
        // Find the closest point to the circle within the rectangle
        double closestX = Math.clamp(getPosition().getX(), btl.getX(), tpl.getX());
        double closestY = Math.clamp(getPosition().getY(), btl.getY(), tpl.getY());

// Calculate the distance between the circle's center and this closest point
        double distanceX = getPosition().getX() - closestX;
        double distanceY = getPosition().getY() - closestY;

// If the distance is less than the circle's radius, an intersection occurs
        double distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        return distanceSquared <= (getRadiusSquared());
    }

    @Override
    public boolean isPointIntersecting(Vector2D point) {
        Vector2D center = getPosition();
        Vector2D centerToPoint = point.subtract(center);
        // Calculating square root is cpu intensive. This check if distance between point and center is greater than the
        //radius. We just squared it to reduce cpu cycles
        return centerToPoint.getMagnitudeSquared() <= getRadius() * getRadius();
    }

    @Override
    public boolean isLineIntersecting(Vector2D start, Vector2D end) {
        //if end point or start point of line segment is intersecting then we can avoid calculation
        if (isPointIntersecting(start) || isPointIntersecting(end))
            return true;

        //Vector A is distance from start point of line
        Vector2D A = this.getCenter().subtract(start);
        Vector2D B = end.subtract(start);
        //Find ratio of the projected length of vector A on vector B;
        double ratio = A.dotProduct(B) / B.getMagnitudeSquared();


        //if ratio was less than zero or greater than 1 than its clear that intersection did not happen
        if (ratio < 0.0f || ratio > 1.0f)
            return false;

        //Resize the B vector to intersection point and subtract that from A
        Vector2D distanceFromLine = A.subtract(B.multiply(ratio));

        //if distance from line is less than radius then intersection did happen
        return distanceFromLine.getMagnitudeSquared() <= getRadiusSquared();

    }

    public int getRadius() {
        return radius;
    }

    public Vector2D getCenter() {
        return getPosition();
    }

    public void setRadius(int diameter) {
        this.radius = diameter;
    }

    public double getRadiusSquared() {
        return radius * radius;
    }
}
