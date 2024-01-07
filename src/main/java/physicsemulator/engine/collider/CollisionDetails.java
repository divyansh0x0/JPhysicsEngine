package physicsemulator.engine.collider;

import physicsemulator.utils.Vector2D;

public class CollisionDetails {
    private double depth;
    private Vector2D collisionNormal;
    private Vector2D impulse = Vector2D.ZERO;

    public CollisionDetails(double depth, Vector2D impulse, Vector2D collisionNormal) {
        this.depth = depth;
        this.impulse = impulse;
        this.collisionNormal = collisionNormal;
    }

    public Vector2D getImpulse() {
        return impulse;
    }

    public void handleImpulse(Vector2D impulse) {
        this.impulse = this.impulse.add(impulse);
    }

    public double getDepth() {
        return depth;
    }

    public void handleDepth(double depth) {
        this.depth = Math.max(this.depth, depth);
    }

    public Vector2D getCollisionNormal() {
        return collisionNormal;
    }

    public void handleCollisionNormal(Vector2D collisionNormal) {
        this.collisionNormal = this.collisionNormal.add(collisionNormal).normalize();
    }
    public void reset() {
        this.depth = 0;
        this.impulse = Vector2D.ZERO;
        this.collisionNormal = Vector2D.ZERO;

    }
    @Override
    public String toString() {
        return "CollisionResolution: " +
                "\ndepth=" + depth +
                "\ncollisionNormal=" + collisionNormal +
                "\nimpulse=" + impulse;
    }


}
