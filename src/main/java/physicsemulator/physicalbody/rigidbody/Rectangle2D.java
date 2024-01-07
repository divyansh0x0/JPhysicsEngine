package physicsemulator.physicalbody.rigidbody;

import material.theme.ThemeColors;
import org.jetbrains.annotations.NotNull;
import physicsemulator.engine.Scene;
import physicsemulator.utils.PhysicsUtils;
import physicsemulator.utils.Vector2D;

import java.awt.*;
import java.awt.geom.Path2D;

public class Rectangle2D extends Polygon2D {
    //Location vector point is at bottom left corner
    //Center vector is from origin and is pointed to the center of rectangle
    //Size vector is diagonal of rectangle
    private Vector2D sizeVector;//It it the diagonal from bottom left to top right [Parallelogram law can be used to find height and width]
    private final Vector2D[] vertices = new Vector2D[4];
//    private Vector2D centerLocationFromOrigin; //Center of mass

    public Rectangle2D(@NotNull Vector2D bottomleft, @NotNull Vector2D topRight, Color color, double mass) {
        sizeVector = topRight.subtract(bottomleft);
        setPosition(bottomleft.add(sizeVector.multiply(0.5f)));
        setColor(color);
        setMass(mass);
    }

    public Rectangle2D(@NotNull Vector2D bottomleft, @NotNull Vector2D topRight, Color color) {
        this(bottomleft, topRight, color, 1);
    }

    public Vector2D getBottomLeft() {
        return getPosition().subtract(sizeVector.multiply(0.5f));
    }

    public Vector2D getTopRight() {
        return getPosition().add(sizeVector.multiply(0.5f));
    }

    public Vector2D[] getVertices() {
        //Four position vectors each pointing to vertex, by assuming rectangle is at origin. Correction will be done in render time
        return vertices;
    }

    public Vector2D getCenter() {
        return getPosition();
    }

    private Color defColor;

    private void updateVertices() {
        Vector2D btl = getBottomLeft();
        Vector2D tpr = getTopRight();

        vertices[0] = new Vector2D(btl.getX(), btl.getY());//bottom left
        vertices[1] = new Vector2D(tpr.getX(), btl.getY()); //bottom right
        vertices[2] = new Vector2D(tpr.getX(), tpr.getY()); //top right
        vertices[3] = new Vector2D(btl.getX(), tpr.getY());//top left


        if (!PhysicsUtils.areEqual(getRotationDegrees(), 0)) {
            for (int i = 0; i < vertices.length; i++) {
                vertices[i] = vertices[i].rotateBy(getRotationDegrees(), getPosition());
            }
//            setPosition(getPosition().rotateBy(getRotationDegrees(),getBottomLeft()));
        }
    }

    @Override
    public void setPosition(Vector2D POSITION) {
        super.setPosition(POSITION);
        if (!getLastFramePosition().equals(POSITION))
            updateVertices();
    }

    @Override
    public void setRotationDegrees(double rotationDegrees) {
        double oldRotation = getRotationDegrees();
        super.setRotationDegrees(rotationDegrees);

        if (!PhysicsUtils.areEqual(rotationDegrees, oldRotation))
            updateVertices();
    }

    @Override
    public void updatePhysics(double dt) {
        super.updatePhysics(dt);
    }

    @Override
    public void renderObject(Graphics2D g, Scene scene) {
        super.renderObject(g, scene);
//        setRotationDegrees(getRotationDegrees() + 1f);
        Path2D path2D = new Path2D.Float();
        Point center = scene.getScenePointFromPositionVector(getPosition());
        Vector2D[] vertices = getVertices();
        Point btl = scene.getScenePointFromPositionVector(vertices[0]);
        Point btr = scene.getScenePointFromPositionVector(vertices[1]);
        Point tpr = scene.getScenePointFromPositionVector(vertices[2]);
        Point tpl = scene.getScenePointFromPositionVector(vertices[3]);

//        Log.info(btl + " | " + btr + " | " + tpr + " | " + tpr);
        path2D.moveTo(btl.x, btl.y);
        path2D.lineTo(btr.x, btr.y);
        path2D.lineTo(tpr.x, tpr.y);
        path2D.lineTo(tpl.x, tpl.y);
        path2D.closePath();

        g.setColor(getColor());
        g.fill(path2D);

        g.setColor(getColor().darker());
        g.draw(path2D);
        g.setColor(ThemeColors.getInstance().getColorOnAccent());
        g.drawString(getRotationDegrees() + "deg", center.x, center.y);

    }

    @Override
    public boolean isPointIntersecting(Vector2D point) {
        Vector2D[] vertices = getVertices();
        return (isLeft(vertices[0], vertices[1], point) > 0 && isLeft(vertices[1], vertices[2], point) > 0 && isLeft(vertices[2], vertices[3], point) > 0 && isLeft(vertices[3], vertices[0], point) > 0);

//        Vector2D cp = new Vector2D(vertices[0].subtract(point));
//      return  0 <= ab.dotProduct(ap) && 0 <= ab.dotProduct(ab) && 0 <= bc.dotProduct(bp) && 0 <= bc.dotProduct(bc);
    }

    @Override
    public boolean isLineIntersecting(Vector2D start, Vector2D end) {
        if (isPointIntersecting(start) || isPointIntersecting(end))
            return true;

        //Convert points to local space if rotation is there
        start = start.rotateBy(getRotationDegrees(), getPosition());
        end = end.rotateBy(getRotationDegrees(), getPosition());
        //Using similar triangles, the proof is in theory directory. (Similar triangles)
        Vector2D lengthVec = end.subtract(start);
        Vector2D A_normal = lengthVec.normalize();
        Vector2D B = getTopRight().subtract(start);
        Vector2D C = getBottomLeft().subtract(end);

        Vector2D A_inv_normal = new Vector2D(A_normal.getX() != 0 ? 1 / A_normal.getX() : 0, A_normal.getY() != 0 ? 1 / A_normal.getY() : 0);
        Vector2D vec1 = new Vector2D(B.getX() * A_inv_normal.getX(), B.getY() * A_inv_normal.getY());
        Vector2D vec2 = new Vector2D(C.getX() * A_inv_normal.getX(), C.getY() * A_inv_normal.getY());

        double min = Math.max(Math.min(vec1.getX(), vec2.getX()), Math.min(vec1.getY(), vec2.getY()));
        double max = Math.min(Math.max(vec1.getX(), vec2.getX()), Math.max(vec1.getY(), vec2.getY()));

        if (max < 0 || min > max) {
            return false;
        }
        double t = min < 0f ? max : min;
        return t > 0 && t * t < lengthVec.getMagnitudeSquared();
    }


}
