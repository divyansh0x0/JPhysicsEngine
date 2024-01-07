package physicsemulator.utils;

import material.utils.Log;
import physicsemulator.engine.CollisionType;
import physicsemulator.physicalbody.RigidObject;

import java.util.Arrays;

public class PhysicsUtils {
    public static float toSeconds(float nanos) {
        return (float) (nanos * 1e-9);
    }

    /**
        Compares A and B to a decimal value up to precision value given
     */
    public static boolean areEqual(double A, double B, double precision){
            // Correct method to compare
            // floating-point numbers
        return Math.abs(A - B) < precision;
    }

    public static boolean areEqual(double A, double B){
        return areEqual(A,B,0.000000001f);
    }
    public static boolean areEqual(Vector2D v1, Vector2D v2, double precision){
        return areEqual(v1.getX(),v2.getX(),precision) && areEqual(v1.getY(),v2.getY(),precision);
    }
    public static boolean areEqual(Vector2D v1, Vector2D v2){
        return areEqual(v1.getX(),v2.getX()) && areEqual(v1.getY(),v2.getY());
    }
    /**
     * Rotate point (x1,y1) about another point (x2,y2) by some angle in degrees
     */

    public double[] rotatePoints(double x1, double y1, double x2, double y2, double angleDeg){
        if(areEqual(angleDeg,0,5))
            return new double[]{x1,y1};

        double x = x1 - x2;
        double y = y1 - y2;

        double cos = (double) Math.cos(Math.toRadians(angleDeg));
        double sin = (double) Math.sin(Math.toRadians(angleDeg));

        double newX = (x * cos) - (y * sin) + x2; //rotation formula
        double newY = (x * sin) + y * cos + y2;
        return new double[]{newX,newY};
    }

    /**
     * Fast inverse square root algorithm
     */
    public static double invSqrt(double number) {
        final double threehalfs = 1.5F;

        double x2 = number * 0.5F;
        double y = number;

        // evil floating point bit level hacking
        long i = Double.doubleToRawLongBits(y);
        i = 0x5f3759df - (i >> 1);
        y = Double.longBitsToDouble(i);

        // 1st iteration
        y = y * (threehalfs - (x2 * y * y));

        // 2nd iteration, this can be removed
        // y = y * ( threehalfs - ( x2 * y * y ) );

        return y;
    }

}
