package physicsemulator.physicalbody.rigidbody;

import org.jetbrains.annotations.NotNull;
import physicsemulator.engine.Scene;
import physicsemulator.physicalbody.RigidObject;
import physicsemulator.utils.Vector2D;

import java.awt.*;

/**
 * Axis aligned bounding box
 */
public class AABB extends RigidObject {
    private Vector2D sizeVector;//It it the diagonal from bottom left to top right [Parallelogram law can be used to find height and width]
    private Vector2D centerLocationFromOrigin; //Center of mass

    public AABB(@NotNull Vector2D bottomleft, Vector2D topRight){
        sizeVector = bottomleft.subtract(topRight);
        centerLocationFromOrigin = bottomleft.add(sizeVector.multiply(0.5f));
    }
    public Vector2D getBottomLeft(){
        return centerLocationFromOrigin.subtract(sizeVector.multiply(0.5f));
    }
    public Vector2D getTopRight(){
        return centerLocationFromOrigin.add(sizeVector.multiply(0.5f));
    }

    public Vector2D getSizeVector() {
        return sizeVector;
    }

    public void setSizeVector(Vector2D sizeVector) {
        this.sizeVector = sizeVector;
    }

    public Vector2D getCenterLocationFromOrigin() {
        return centerLocationFromOrigin;
    }

    public void setCenterLocationFromOrigin(Vector2D centerLocationFromOrigin) {
        this.centerLocationFromOrigin = centerLocationFromOrigin;
    }


    @Override
    public boolean isPointIntersecting(Vector2D p) {
        Vector2D tpr = getTopRight();
        Vector2D btl = getBottomLeft();
        return p.getX() <= tpr.getX() && btl.getX() <= p.getX()  //Check if intersection in x-axis
               && p.getY() <= tpr.getY() && btl.getY() <= p.getY() ;//Check if intersection in y-axis
    }

    @Override
    public boolean isLineIntersecting(Vector2D start, Vector2D end) {
        return false;
    }

    @Override
    public void renderObject(Graphics2D g, Scene scene) {
        super.renderObject(g, scene);

    }
}
