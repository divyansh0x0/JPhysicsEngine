package physicsemulator.engine;

import physicsemulator.utils.Vector2D;

public class EngineProperties {
    private static EngineProperties instance;
    private Vector2D ACCELERATION_DUE_TO_GRAVITY = new Vector2D(0,-100f);
    private Vector2D FORCE_BY_AIR = new Vector2D(0,0);
    private Vector2D INITIAL_VELOCITY_OF_AIR = new Vector2D(0,0);
    private double restitutionCoefficient= 1;

    /* ********************************************
     *
     *              GETTERS
     *
     ********************************************/
    public Vector2D getAccelerationDueToGravity() {
        return ACCELERATION_DUE_TO_GRAVITY;
    }

    public Vector2D getAirResistanceForce() {
        return FORCE_BY_AIR;
    }
    public Vector2D getInitialVelocityOfAir() {
        return INITIAL_VELOCITY_OF_AIR;
    }


    /* ********************************************
     *
     *              SETTERS
     *
     ********************************************/
    public void setAccelerationDueToGravity(Vector2D acc) {
        this.ACCELERATION_DUE_TO_GRAVITY = acc;
    }
    public void setAirResistanceForce(Vector2D f) {
        this.FORCE_BY_AIR = f;
    }


    public void setInitialVelocityOfAir(Vector2D initialVelocityOfAir) {
        INITIAL_VELOCITY_OF_AIR = initialVelocityOfAir;
    }

    /**
     * methods to make class singleton
     */
    private EngineProperties(){

    }
    public static EngineProperties getInstance(){
        if(instance == null)
            instance= new EngineProperties();
        return instance;
    }

    public double getRestitutionCoefficient() {
        return restitutionCoefficient;
    }

    public void setRestitutionCoefficient(double restitutionCoefficient) {
        this.restitutionCoefficient = restitutionCoefficient;
    }
}
