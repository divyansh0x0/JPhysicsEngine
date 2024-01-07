
package material.tools;

import material.theme.ThemeManager;
import material.theme.enums.ThemeType;
import org.jetbrains.annotations.NotNull;
import material.utils.Log;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ColorUtils {
    public static Color ArgbToRgb(Color c, Color bg){
        float alpha = c.getAlpha()/255f;

        return new Color(
                ((1f - alpha) * bg.getRed() + alpha * c.getRed())/255f,
                ((1f - alpha) * bg.getGreen() + alpha * c.getGreen())/255f,
                ((1f - alpha) * bg.getBlue() + alpha * c.getBlue())/255f
        );
    }
    public static Color RgbToArgb(Color c, float alpha){
        float[] arr = c.getRGBComponents(null);
        return new Color(arr[0],arr[1],arr[2], Math.min(Math.max(alpha,0),1)); //making sure alpha is always between 0 and 1
    }

    public static Color AlphaCompositedRGB(Color c, Color bg, float alpha){
        return ArgbToRgb(RgbToArgb(c,alpha),bg);
    }
    @NotNull
    public static Color invertColor(@NotNull Color c) {
        float[] rgb = c.getRGBComponents(null);
        return new Color(1.0f - rgb[0], 1.0f - rgb[1], 1.0f - rgb[2]);
    }

    public static Color darken(@NotNull Color c, double percentage) {
        if (percentage > 0) {
            int R = c.getRed(), G = c.getGreen(), B = c.getBlue(), A = c.getAlpha();
            double fraction = Math.max((percentage) / 100, 0);
            R = (int) Math.round(Math.max(R - (255 * fraction), 0));
            G = (int) Math.round(Math.max(G - (255 * fraction), 0));
            B = (int) Math.round(Math.max(B - (255 * fraction), 0));

            return new Color(R, G, B, A);
        } else return c;
    }

    public static Color darken(@NotNull Color c, double percentage, boolean withAlpha) {
        if (percentage > 0) {
            int R = c.getRed(), G = c.getGreen(), B = c.getBlue(), A = c.getAlpha();
            double fraction = Math.max((percentage) / 100, 0);
            R = (int) Math.round(Math.max(R - (255 * fraction), 0));
            G = (int) Math.round(Math.max(G - (255 * fraction), 0));
            B = (int) Math.round(Math.max(B - (255 * fraction), 0));
            if (withAlpha)
                A = (int) Math.round(Math.max(A - (255 * fraction), 0));
            return new Color(R, G, B, A);
        } else return c;
    }

    public static Color lighten(@NotNull Color c, double percentage) {
        if (percentage > 0) {
            int R = c.getRed(), G = c.getGreen(), B = c.getBlue(), A = c.getAlpha();
            double fraction = Math.max((percentage) / 100, 0);
            R = (int) Math.round(Math.min(R + (255 * fraction), 255));
            G = (int) Math.round(Math.min(G + (255 * fraction), 255));
            B = (int) Math.round(Math.min(B + (255 * fraction), 255));
            return new Color(R, G, B, A);
        } else return c;
    }

    public static Color lighten(@NotNull Color c, double percentage, boolean withAlpha) {
        if (percentage > 0) {
            int R = c.getRed(), G = c.getGreen(), B = c.getBlue(), A = c.getAlpha();
            double fraction = Math.max((percentage) / 100, 0);
            R = (int) Math.round(Math.min(R + (255 * fraction), 255));
            G = (int) Math.round(Math.min(G + (255 * fraction), 255));
            B = (int) Math.round(Math.min(B + (255 * fraction), 255));
            if (withAlpha)
                A = (int) Math.round(Math.min(A + (255 * fraction), 255));

            return new Color(R, G, B, A);
        } else return c;
    }

    public static Color getAverageColor(BufferedImage bufferedImage) {
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();
        int x0 = bufferedImage.getMinX();
        int y0 = bufferedImage.getMinY();
        int x1 = x0 + w;
        int y1 = y0 + h;
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                Color pixel = new Color(bufferedImage.getRGB(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }
        int num = w * h;
        return new Color((int) (sumr / num), (int) (sumg / num), (int) (sumb / num));
    }

    public static Color getAverageColor(BufferedImage bufferedImage, Color excludedColor) {
        Color avg = getAverageColor(bufferedImage);
        if (!excludedColor.equals(avg)) {
            if (ThemeManager.getInstance().getThemeType().equals(ThemeType.Light)) {
                avg = darken(avg, 20);
            } else
                avg = lighten(avg, 20);
        } else {
            if (ThemeManager.getInstance().getThemeType().equals(ThemeType.Light)) {
                avg = darken(avg, 50);
            } else
                avg = lighten(avg, 50);
            return avg;
        }

        return avg;
    }

    public static @NotNull Color reduceColorSaturation(Color c, int percentage) {
        float[] hsl = RgbToHsl(c);
        Log.warn("before hsl: " + Arrays.toString(hsl));
        hsl[1] -= percentage / 100f * hsl[1];
        Log.warn("after hsl: " + Arrays.toString(hsl));
        return HslToRGB(hsl[0], Math.max(hsl[1], 0), hsl[2], 1);
    }

    public static float[] RgbToHsl(Color color) {
        //  Get RGB values in the range 0 - 1

        float[] rgb = color.getRGBColorComponents(null);
        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];

        //	Minimum and Maximum RGB values are used in the HSL calculations

        float min = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.max(g, b));

        //  Calculate the Hue

        float h = 0;

        if (max == min)
            h = 0;
        else if (max == r)
            h = ((60 * (g - b) / (max - min)) + 360) % 360;
        else if (max == g)
            h = (60 * (b - r) / (max - min)) + 120;
        else if (max == b)
            h = (60 * (r - g) / (max - min)) + 240;

        //  Calculate the Luminance

        float l = (max + min) / 2;
        //System.out.println(max + " : " + min + " : " + l);

        //  Calculate the Saturation

        float s = 0;

        if (max == min)
            s = 0;
        else if (l <= .5f)
            s = (max - min) / (max + min);
        else
            s = (max - min) / (2 - max - min);

        return new float[]{h, s * 100, l * 100};
    }


    public static Color HslToRGB(float h, float s, float l, float alpha) {
        if (s < 0.0f || s > 100.0f) {
            String message = "Color parameter outside of expected range - Saturation";
            throw new IllegalArgumentException(message);
        }

        if (l < 0.0f || l > 100.0f) {
            String message = "Color parameter outside of expected range - Luminance";
            throw new IllegalArgumentException(message);
        }

        if (alpha < 0.0f || alpha > 1.0f) {
            String message = "Color parameter outside of expected range - Alpha";
            throw new IllegalArgumentException(message);
        }

        //  Formula needs all values between 0 - 1.

        h = h % 360.0f;
        h /= 360f;
        s /= 100f;
        l /= 100f;

        float q = 0;

        if (l < 0.5)
            q = l * (1 + s);
        else
            q = (l + s) - (s * l);

        float p = 2 * l - q;

        float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
        float g = Math.max(0, HueToRGB(p, q, h));
        float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

        r = Math.min(r, 1.0f);
        g = Math.min(g, 1.0f);
        b = Math.min(b, 1.0f);

        return new Color(r, g, b, alpha);
    }

    private static float HueToRGB(float p, float q, float h) {
        if (h < 0) h += 1;

        if (h > 1) h -= 1;

        if (6 * h < 1) {
            return p + ((q - p) * 6 * h);
        }

        if (2 * h < 1) {
            return q;
        }

        if (3 * h < 2) {
            return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
        }

        return p;
    }

    public static Color mergeColors(Color c1, Color c2) {
        int m = 0xfefefefe;
        int c = ((c1.getRGB() & m) >>> 1) + ((c2.getRGB() & m) >>> 1);
        return new Color(c);
    }
}
