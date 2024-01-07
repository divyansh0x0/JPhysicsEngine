package material.colorspace;

import java.awt.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class HSV {
    private final float Hue;
    private final float Saturation;
    private final float Value;

    public HSV(float Hue, float Saturation, float Value) {
        this.Hue = Hue;
        this.Saturation = Saturation;
        this.Value = Value;
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static HSV convertToHSB(int r, int g, int b) {
        float hsv[] = new float[3];
        Color.RGBtoHSB(r,g,b,hsv);
        return new HSV(hsv[0], hsv[1], hsv[2]);
    }

    @Contract("_ -> new")
    public static @NotNull HSV convertToHSB(@NotNull Color c) {
        return convertToHSB(c.getRed(), c.getGreen(), c.getBlue());
    }

    public Color convertToColor() {
        return new Color(Color.HSBtoRGB(getHue(), getSaturation(), getValue()));
    }

    public float getHue() {
        return Hue;
    }

    public float getSaturation() {
        return Saturation;
    }

    public float getValue() {
        return Value;
    }

    @Override
    public String toString() {
        return "Hue: " + Hue + ", Saturation=" + Saturation + "%, Value=" + Value + "%";
    }
}