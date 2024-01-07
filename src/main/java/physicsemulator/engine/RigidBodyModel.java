package physicsemulator.engine;

import org.jetbrains.annotations.NotNull;
import physicsemulator.utils.Vector2D;

import java.awt.*;

public interface RigidBodyModel {
    boolean isSelected();

    boolean isStatic();

    double getMass();

    double getInvMass();

    double getFrictionCoefficient();

    void setPosition(Vector2D newPos);

    void setMass(double m);

    void setStatic(boolean isStatic);

    void setFrictionCoefficient(double k);


    void applyImpulse(@NotNull Vector2D impulse);
    void applyInstantaneousJerk(@NotNull Vector2D jerk);
    void applyInstantaneousForce(@NotNull Vector2D force);
    void addForce(@NotNull Vector2D force);

    void addLinearVelocity(@NotNull Vector2D vel);

    void addDisplacement(@NotNull Vector2D disp);

    void addJerk(@NotNull Vector2D jerk);


    void setForce(@NotNull Vector2D acc);

    void setLinearVelocity(@NotNull Vector2D vel);

    void setJerk(@NotNull Vector2D jerk);


    Vector2D getPosition();
    Vector2D getLastFramePosition();

    Vector2D getForce();

    Vector2D getCurrentVelocity();

    Vector2D getLinearMomentum();

    Vector2D getJerk();

    /**
     * @param dt must be in seconds
     */
    void updatePhysics(double dt);

    void renderObject(Graphics2D g, Scene scene);

    void setSelected(boolean b);



    boolean isPointIntersecting(Vector2D p);
    boolean isLineIntersecting(Vector2D start, Vector2D end);

}
