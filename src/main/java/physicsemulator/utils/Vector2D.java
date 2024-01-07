package physicsemulator.utils;

import org.jetbrains.annotations.NotNull;

//ALL VECTORS ARE ASSUMED TO BE FROM ORIGIN OF SCENE. POINT CONVERSION SHOULD BE DONE SEPARATELY
public class Vector2D implements Vector {
    //    public static final Vector2D NULL = new Vector2D(0, 0);
    private double x = 0f;
    private double y = 0f;
    private double magnitudeSquared;

    public Vector2D(double x, double y) {
        if (!PhysicsUtils.areEqual(x, 0))
            this.x = x;
        if (!PhysicsUtils.areEqual(y, 0))
            this.y = y;
        this.magnitudeSquared = x * x + y * y;
    }

    public Vector2D() {
        this(0, 0);
    }

    public Vector2D(Vector2D vec) {
        this(vec.getX(), vec.getY());
    }

    public Vector2D(Vector2D comp1, Vector2D comp2) {
        this(comp1.getY() + comp2.getX(), comp1.getY() + comp2.getY());
    }

    public void pointTo(double x, double y) {
        this.x = x;
        this.y = y;
        this.magnitudeSquared = x * x + y * y;
    }

    public void pointTo(Vector2D vec) {
        pointTo(vec.getX(), vec.getY());
    }

    @Override
    public double dotProduct(@NotNull Vector2D vec) {
        return (this.x * vec.x + this.y * vec.y);
    }

    @Override
    public Vector2D add(@NotNull Vector2D vec) {
        if (vec.isZero())
            return this;
        return new Vector2D(this.x + vec.x, this.y + vec.y);
    }

    public Vector2D add(double x, double y) {
        return new Vector2D(this.x + x, this.y + y);
    }

    @Override
    public Vector2D subtract(Vector2D vec) {
        if (vec.isZero())
            return this;
        return new Vector2D(this.x - vec.x, this.y - vec.y);
    }

    public Vector2D subtract(int x, int y) {
        return new Vector2D(this.x - x, this.y - y);
    }

    @Override
    public Vector2D multiply(double multiplier) {
        if (isZero() || multiplier == 1)
            return this;
        return new Vector2D(x * multiplier, y * multiplier);
    }

    @Override
    public Vector2D rotateBy(double angleDeg, Vector2D positionFromOrigin) {
        if (angleDeg < 0)
            angleDeg = 360 - (angleDeg % 360);
        double x = this.x - positionFromOrigin.getX();
        double y = this.y - positionFromOrigin.getY();

        double cos = (double) Math.cos(Math.toRadians(angleDeg));
        double sin = (double) Math.sin(Math.toRadians(angleDeg));

        double newX = (x * cos) - (y * sin) + positionFromOrigin.getX(); //rotation formula
        double newY = (x * sin) + (y * cos) + positionFromOrigin.getY();
        return new Vector2D(newX, newY);
    }

    @Override
    public Vector2D normalize() {
        if (isZero() || (getX() == 1 && getY() == 1))
            return this;
        double invMagnitude = 1 / getMagnitude();
        return new Vector2D(getX() * invMagnitude, getY() * invMagnitude);
    }

    @Override
    public Vector2D[] resolveVector(Vector2D otherVec) {
        Vector2D vecAlongB = otherVec.multiply(this.dotProduct(otherVec) / otherVec.dotProduct(otherVec));
        Vector2D other = this.subtract(vecAlongB);
        return new Vector2D[]{vecAlongB, other};
    }

    @Override
    public double getMagnitude() {
        return (double) Math.sqrt(getMagnitudeSquared());
    }

    @Override
    public double getMagnitudeSquared() {
        return magnitudeSquared;
    }

    @Override
    public boolean isZero() {
        return PhysicsUtils.areEqual(x, 0) && PhysicsUtils.areEqual(y, 0);
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getAngleFromPositiveXDirection() {
        if (!isZero())
            return (double) Math.toDegrees(Math.atan2(y, x));
        else
            throw new RuntimeException("Null vectors do not have an angle from x axis");
    }

    @Override
    public String toString() {
        return "%si + %sj".formatted(x, y);
    }

    public Vector2D copy() {
        return new Vector2D(x, y);
    }


    public Vector2D absolute() {
        return new Vector2D(Math.abs(x), Math.abs(y));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector2D) {
            return PhysicsUtils.areEqual(this.x, ((Vector2D) obj).x) && PhysicsUtils.areEqual(this.y, ((Vector2D) obj).y);
        }
        return super.equals(obj);
    }
}
