package physicsemulator.physicalbody;

import org.jetbrains.annotations.NotNull;
import physicsemulator.engine.EngineProperties;
import physicsemulator.engine.RigidBodyModel;
import physicsemulator.engine.Scene;
import physicsemulator.utils.Vector;
import physicsemulator.utils.Vector2D;

import java.awt.*;

public abstract class RigidObject implements RigidBodyModel {
    private boolean isExternalForceApplied = false;
    private boolean isStatic = false;
    private boolean isSelected;
    private double rotationDegrees;//in degrees
    private Color COLOR;
    private Vector2D collisionNormal;
    private double time;
    private Vector2D lastPos = Vector2D.ZERO;

    public RigidObject(double Mass) throws HeadlessException {
        ACCELERATION = Vector2D.ZERO;
        setMass(Mass);
        setForce(Vector2D.ZERO);
        setLinearVelocity(Vector2D.ZERO);
        setPosition(Vector2D.ZERO);
        setJerk(Vector2D.ZERO);
    }

    public RigidObject() {
        this(1f);
    }

    @Override
    public void renderObject(Graphics2D g, Scene scene) {
        LAST_FRAME_POSITION = getPosition();
//        if (!isStatic) {
//            Log.info("acc: " + ACCELERATION + "| vel: " + getCurrentVelocity());
//
//        }
        if (isSelected) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
    }
    @Override
    public synchronized void updatePhysics(double dt) {
        applyExternalForces();

        if (!isSelected()) {


            //APPLY GRAVITY
            //INTEGRATION
            if (!isStatic) {
                addForce(JERK.multiply(dt));
                Vector2D netForce = getNetForce(dt);
                ACCELERATION = netForce.multiply(getInvMass());
                CURRENT_LINEAR_VELOCITY = CURRENT_LINEAR_VELOCITY.add(ACCELERATION.multiply(dt));
//                addLinearVelocity(ACCELERATION.multiply(dt));
                addDisplacement(getCurrentVelocity().multiply(dt));
//                Log.info(ACCELERATION);
//                Vector2D xNew  =   POSITION.multiply(2).subtract(lastPos).add(ACCELERATION.multiply(dt * dt));
//                lastPos = POSITION;
//                POSITION = xNew ;
//                doVelocityVerlet(dt);
            }
            time += dt;
        }
    }

    private void doVelocityVerlet(double deltaTSeconds) {
        ACCELERATION = getNetForce(deltaTSeconds).multiply(getInvMass());
        if (!isStatic) {
            ACCELERATION = ACCELERATION.add(EngineProperties.getInstance().getAccelerationDueToGravity());
        }
        addDisplacement(getCurrentVelocity().multiply(deltaTSeconds).add(ACCELERATION.multiply(0.5f * deltaTSeconds * deltaTSeconds)));
        addLinearVelocity(ACCELERATION.multiply(deltaTSeconds));
    }

    private Vector2D getNetForce(double dt) {
        Vector2D netForce = FORCE;
        if (INSTANT_FORCE != null) {
            netForce = netForce.add(INSTANT_FORCE);
            INSTANT_FORCE = null;
        }
        if (INSTANT_JERK != null) {
            //F = F" + Jerk * dt
            netForce = netForce.add(INSTANT_JERK.multiply(dt * getMass()));
            INSTANT_JERK = null;
        }

        return netForce;
    }

    private void applyExternalForces() {
        if (!isExternalForceApplied) {
            if (!isStatic) {
                addForce(EngineProperties.getInstance().getAccelerationDueToGravity().multiply(getMass()));
                isExternalForceApplied = true;
            }
        } else {
            if (isStatic) {
                //subtract the force if the body was changed to static
                addForce(EngineProperties.getInstance().getAccelerationDueToGravity().multiply(-getMass()));
                isExternalForceApplied = false;
            }
        }
    }




    /***
     * Physics object model implementation
     */
    private Vector2D POSITION;
    private Vector2D LAST_FRAME_POSITION;

    private double INV_MASS;
    private double MASS;
    private double FRICTION_COEFFICIENT;
    private Vector2D FORCE;
    private Vector2D INITIAL_LINEAR_VELOCITY;
    private Vector2D CURRENT_LINEAR_VELOCITY;
    private Vector2D JERK;
    private Vector2D INSTANT_JERK;
    private Vector2D INSTANT_FORCE;
    private Vector2D ACCELERATION;

    @Override
    public synchronized void applyImpulse(@NotNull Vector2D impulse) {
        //using v = J/m + u
        addLinearVelocity(impulse.multiply(getInvMass()));
    }

    @Override
    public synchronized void applyInstantaneousJerk(@NotNull Vector2D jerk) {
        if (INSTANT_JERK != null)
            INSTANT_JERK.add(jerk);
        else
            INSTANT_JERK = jerk;
    }

    @Override
    public synchronized void applyInstantaneousForce(@NotNull Vector2D force) {
        if (INSTANT_FORCE != null)
            INSTANT_FORCE = INSTANT_FORCE.add(force);
        else
            INSTANT_FORCE = force;
    }
    /* **********************************************
     *              ADDERS
     **********************************************/


    @Override
    public synchronized void addForce(@NotNull Vector2D force) {
        FORCE = getForce().add(force);
    }

    @Override
    public synchronized void addLinearVelocity(@NotNull Vector2D vel) {
        CURRENT_LINEAR_VELOCITY= CURRENT_LINEAR_VELOCITY.add(vel);
    }

    @Override
    public synchronized void addDisplacement(@NotNull Vector2D disp) {
        setPosition(getPosition().add(disp));
    }

    @Override
    public void addJerk(@NotNull Vector2D jerk) {
        setJerk(getJerk().add(jerk));
    }

    /* **************************************
     *              SETTERS
     ***************************************/
    public void setStatic(boolean b) {
        isStatic = b;
        setMass(getMass());//reset the mass
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public void setRotationDegrees(double rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
    }

    public void setPosition(Vector2D POSITION) {
        this.POSITION = POSITION;
        lastPos = getPosition();
    }

    public void setColor(Color COLOR) {
        this.COLOR = COLOR;
    }

    @Override
    public synchronized void setMass(double m) {
        MASS = m;
        INV_MASS = !isStatic ? 1 / m : 0;
    }

    @Override
    public void setJerk(@NotNull Vector2D jerk) {
        this.JERK = jerk;
    }

    @Override
    public void setFrictionCoefficient(double k) {
        FRICTION_COEFFICIENT = k;
    }


    @Override
    public void setForce(@NotNull Vector2D force) {
        FORCE = force;
    }


    @Override
    public void setLinearVelocity(@NotNull Vector2D vel) {
        INITIAL_LINEAR_VELOCITY = vel;
        CURRENT_LINEAR_VELOCITY = vel;
    }

    /* **********************************************
     *                  GETTERS
     ********************************************/
    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    public Color getColor() {
        return COLOR;
    }

    @Override
    public Vector2D getPosition() {
        return POSITION;
    }

    @Override
    public Vector2D getLastFramePosition() {
        if (LAST_FRAME_POSITION == null)
            return Vector.ZERO;
        return LAST_FRAME_POSITION;
    }


    @Override
    public double getMass() {
        return MASS;
    }

    @Override
    public double getInvMass() {
        return INV_MASS;
    }

    @Override
    public synchronized double getFrictionCoefficient() {
        return FRICTION_COEFFICIENT;
    }

    @Override
    public synchronized Vector2D getLinearMomentum() {
        //This calculates momentum of body
        return getCurrentVelocity().multiply(MASS);
    }

    @Override
    public synchronized Vector2D getForce() {
        return FORCE;
    }

    @Override
    public synchronized Vector2D getCurrentVelocity() {
        return CURRENT_LINEAR_VELOCITY;
    }

    public double getRotationDegrees() {
        return rotationDegrees;
    }

    @Override
    public Vector2D getJerk() {
        return JERK;
    }

//
//    /* ***********************************************
//     *                  ABSTRACT
//     ************************************************/
//    public abstract boolean isPointIntersecting(Vector2D point);


}
