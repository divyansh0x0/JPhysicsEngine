package physicsemulator.physicalbody.rigidbody;

import physicsemulator.physicalbody.RigidObject;
import physicsemulator.utils.Vector2D;

import java.awt.*;

public abstract class Polygon2D extends RigidObject {

    double isLeft( Vector2D P0, Vector2D P1, Vector2D P2 )
    {
        return ( (P1.getX() - P0.getX()) * (P2.getY() - P0.getY()) - (P2.getX() - P0.getX()) * (P1.getY() - P0.getY()) );
    }
    public abstract Vector2D[] getVertices();
    public abstract Vector2D getCenter();

}
