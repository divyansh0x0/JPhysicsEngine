package physicsemulator.utils;

import org.jetbrains.annotations.NotNull;

public interface Vector {
    Vector2D ZERO = new Vector2D(0, 0);

    double dotProduct(@NotNull Vector2D vec);

    /**
     * A new Vector2D is returned
     */
    Vector2D add(@NotNull Vector2D vec);

    Vector2D subtract(Vector2D vec);

    Vector2D multiply(double multiplier);

    /**
     * @return Rotates this vector by some angle from another vector
     */
    Vector2D rotateBy(double angleDeg, Vector2D positionFromOrigin);

    Vector2D normalize();

    Vector2D[] resolveVector(Vector2D b);

    double getMagnitude();

    double getMagnitudeSquared();

    boolean isZero();

    double getX();

    double getY();

    /**
     * Returns angle from positive x-axis in radians
     */
    double getAngleFromPositiveXDirection();
}
