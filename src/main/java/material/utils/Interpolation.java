package material.utils;

import org.jetbrains.annotations.Range;

public class Interpolation {
    public static float lerp(float from, float to, @Range(from = 0, to = 1) float progress){
        return from + (to-from)*progress;
    }
    public static double lerp(double start, double end, double t) {
        return start + (end - start) * t;
    }

    public static double easeInOut(double t) {
        return t < 0.5 ? 0.5 * Math.pow(t * 2, 2) : 0.5 * (2 - Math.pow(2 * (1 - t), 2));
    }

}
