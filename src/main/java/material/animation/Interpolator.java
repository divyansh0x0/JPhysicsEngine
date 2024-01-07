package material.animation;

import material.theme.ThemeColors;
import material.theme.colors.ColorPool;

import java.awt.*;

public class Interpolator {
    public static Color acquireLerpRBG(Color from, Color to, double progress) {
        if (from == null)
            from = ThemeColors.TransparentColor;
        if (to == null)
            to = ThemeColors.TransparentColor;
        if (from.equals(to))
            return to;
        progress = Math.min(1, Math.max(progress, 0));
        return ColorPool.getInstance().acquireColor
                (
                        (int) (from.getRed() + (to.getRed() - from.getRed()) * progress),
                        (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * progress),
                        (int) (from.getBlue() + (to.getBlue() - from.getBlue()) * progress),
                        (int) (from.getAlpha() + (to.getAlpha() - from.getAlpha()) * progress)
                );
    }
    public static Color lerpRBG(Color from, Color to, double progress) {
        if (from == null)
            from = ThemeColors.TransparentColor;
        if (to == null)
            to = ThemeColors.TransparentColor;
        if (from.equals(to))
            return to;
        progress = Math.min(1, Math.max(progress, 0));
        return new Color
                (
                        (int) (from.getRed() + (to.getRed() - from.getRed()) * progress),
                        (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * progress),
                        (int) (from.getBlue() + (to.getBlue() - from.getBlue()) * progress),
                        (int) (from.getAlpha() + (to.getAlpha() - from.getAlpha()) * progress)
                );
    }
    public static void acquireLerpRBG(int[] fromRgb, int[] toRgb, float progress, double[] intermediateRgb) {
        if (intermediateRgb.length == 4) {
            intermediateRgb[0] = correctValue(fromRgb[0] + (toRgb[0] - fromRgb[0]) * progress);
            intermediateRgb[1] = correctValue(fromRgb[1] + (toRgb[1] - fromRgb[1]) * progress);
            intermediateRgb[2] = correctValue( fromRgb[2] + (toRgb[2] - fromRgb[2]) * progress);
            intermediateRgb[3] = correctValue( fromRgb[3] + (toRgb[3] - fromRgb[3]) * progress);
        } else if (intermediateRgb.length == 3) {
            intermediateRgb[0] = correctValue( fromRgb[0] + (toRgb[0] - fromRgb[0]) * progress);
            intermediateRgb[1] = correctValue( fromRgb[1] + (toRgb[1] - fromRgb[1]) * progress);
            intermediateRgb[2] = correctValue( fromRgb[2] + (toRgb[2] - fromRgb[2]) * progress);
        }
    }
    private static float correctValue(float v) {
        return Math.max(Math.min(v, 1), 0);
    }
}
