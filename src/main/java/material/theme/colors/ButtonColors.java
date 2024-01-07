package material.theme.colors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ButtonColors {

    private @Nullable Color BackgroundColor;
    private @NotNull Color ForegroundColor;
    private @Nullable Color BackgroundMousePressedColor;
    private @Nullable Color BackgroundMouseOverColor;

    public ButtonColors(@Nullable Color backgroundColor, @NotNull Color foregroundColor, @Nullable Color backgroundMousePressedColor, @Nullable Color backgroundMouseOverColor) {
        BackgroundColor = backgroundColor;
        ForegroundColor = foregroundColor;
        BackgroundMousePressedColor = backgroundMousePressedColor;
        BackgroundMouseOverColor = backgroundMouseOverColor;
    }

    public Color getBackgroundColor() {
        return BackgroundColor;
    }

    public ButtonColors setBackgroundColor(Color backgroundColor) {
        BackgroundColor = backgroundColor;
        return this;
    }

    public Color getForegroundColor() {
        return ForegroundColor;
    }

    public ButtonColors setForegroundColor(Color foregroundColor) {
        ForegroundColor = foregroundColor;
        return this;
    }

    public Color getBackgroundMousePressedColor() {
        return BackgroundMousePressedColor;
    }

    public ButtonColors setBackgroundMousePressedColor(Color backgroundMousePressedColor) {
        BackgroundMousePressedColor = backgroundMousePressedColor;
        return this;
    }

    public Color getBackgroundMouseOverColor() {
        return BackgroundMouseOverColor;
    }

    public ButtonColors setBackgroundMouseOverColor(Color backgroundMouseOverColor) {
        BackgroundMouseOverColor = backgroundMouseOverColor;
        return this;
    }
}
