package physicsemulator.utils;

import physicsemulator.engine.Scene;
import physicsemulator.physicalbody.rigidbody.Circle2D;

import java.awt.*;

public class LineSegment2D {
    private Vector2D start;
    private Vector2D end;

    /**
     * @param start the start point of line from origin
     * @param end   the end point of line from origin
     */
    public LineSegment2D(Vector2D start, Vector2D end) {
        this.start = start;
        this.end = end;
    }

    public boolean isPointOnLine(Vector2D point) {
        double m = getSlope();
        //edge case when slope is infinity
        if (Double.isInfinite(m)) {
            //Means a vertical line, i.e. x = constant

            //if point and line has same x then they coincide
            return PhysicsUtils.areEqual(point.getX(), start.getX());
        }
        //using y = mx + c equation to validate point position where c is y intercept
        double yIntercept = start.getY() - m * start.getX();
        return PhysicsUtils.areEqual(point.getY(), m * point.getX() + yIntercept);
    }

    public LineSegment2D rotateAroundPoint(double angleDeg, Vector2D point){
        return new LineSegment2D(start.rotateBy(angleDeg,point),end.rotateBy(angleDeg,point));
    }

    /* *
     * GETTERS
     */
    public Vector2D getStart() {
        return start;
    }

    public Vector2D getEnd() {
        return end;
    }

    public Vector2D getLengthVector() {
        return end.subtract(start);
    }

    public double getLengthSquared() {
        return getLengthVector().getMagnitudeSquared();
    }
    public void setStart(Vector2D start) {
        this.start = start;
    }

    public double getSlope() {
        return (end.getY() - start.getY()) / (end.getX() - start.getX());
    }

    /* *
     * SETTERS
     */

    public void setEnd(Vector2D end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "LineSegment2D from " + start + " to" + end;
    }

}
