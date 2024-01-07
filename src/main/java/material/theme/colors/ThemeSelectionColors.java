package material.theme.colors;

import java.awt.*;

public class ThemeSelectionColors {
    private Color Background;
    private Color Foreground;

    public ThemeSelectionColors(Color background, Color foreground) {
        Background = background;
        Foreground = foreground;
    }

    public final Color getBackground() {
        return Background;
    }

    public final Color getForeground() {
        return Foreground;
    }
}
