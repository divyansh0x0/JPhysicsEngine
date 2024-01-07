package material.theme.colors;

import material.tools.ColorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;

import static material.tools.ColorUtils.AlphaCompositedRGB;

public class Dark implements Themeable {
    public Dark() {
        setAccentColor(Accent,false);
        writeSelectionColors();
        writeElevationColors();
    }
    private static final byte COMPOSITING_ALPHA = 12;
    private static final Color BACKGROUND_BASE = new Color(0x121212);

    private static @Nullable Color defaultAccentColor = new Color(0x4D78E5);
    private static @Nullable Color defaultBgColor;
        private static Color Background = new Color(0x121212);
//    private static @NotNull Color Background = ColorUtils.ArgbToRgb(new Color(78, 121, 230, COMPOSITING_ALPHA), BACKGROUND_BASE);
    private static @NotNull Color Accent = new Color(0x4D78E5);
    private static @NotNull Color activeItemColor =ColorUtils.AlphaCompositedRGB(Accent, BACKGROUND_BASE, (COMPOSITING_ALPHA * 4) / 255f);
    private static final Color ColorOnAccent = new Color(0x121212);

    private static final Color TextPrimary = new Color(0xDCDCDC);
    private static final Color TextSecondary = new Color(0xA1A1A1);


    private static final Color BackgroundDanger = new Color(0x621414);
    private static final Color BackgroundSuccess = new Color(0x145E14);
    private static final Color BackgroundWarn = new Color(0x3B420A);
    private static final Color FocusedBorderColor = Color.yellow;
    private static ElevationColors ELEVATION_COLORS;
    private static ThemeSelectionColors SELECTION_COLORS;



    @Override
    public Color getBackgroundColor() {
        return Background;
    }

    @Override
    public Color getAccentColor() {
        return Accent;
    }

    @Override
    public Color getColorOnAccent() {
        return ColorOnAccent;
    }

    @Override
    public Color getTextColorPrimary() {
        return TextPrimary;
    }

    @Override
    public Color getTextColorSecondary() {
        return TextSecondary;
    }

    @Override
    public Color getBackgroundColorDanger() {
        return BackgroundDanger;
    }

    @Override
    public Color getBackgroundColorSuccess() {
        return BackgroundSuccess;
    }

    @Override
    public Color getBackgroundColorWarn() {
        return BackgroundWarn;
    }

    @Override
    public Color getFocusedBorderColor() {
        return FocusedBorderColor;
    }

    @Override
    public ButtonColors getIconButtonColors() {

        return DarkButtonColors.getColors();
    }

    @Override
    public Color getActiveBackgroundColor() {
        return activeItemColor;
    }

    @Override
    public ElevationColors getElevationColors() {
        return ELEVATION_COLORS;
    }

    @Override
    public ThemeSelectionColors getSelectionColors() {
        return SELECTION_COLORS;
    }

    @Override
    public void setAccentColor(@Nullable Color accentColor, boolean tintBackground) {
        if (defaultAccentColor == null)
            defaultAccentColor = Accent; // Saving the default accent color
        if (defaultBgColor == null)
            defaultBgColor = Background;
        Accent = Objects.requireNonNullElseGet(accentColor, () -> defaultAccentColor);

        Color colorToMerge = new Color(Accent.getRed(), Accent.getGreen(), Accent.getBlue(), COMPOSITING_ALPHA);
        activeItemColor = ColorUtils.AlphaCompositedRGB(colorToMerge, BACKGROUND_BASE, (COMPOSITING_ALPHA * 4) / 255f);

        if(tintBackground) {
            //Dynamic background
            Color bgTemp = ColorUtils.ArgbToRgb(colorToMerge, BACKGROUND_BASE);
            Background = Objects.requireNonNullElseGet(bgTemp, () -> defaultBgColor);
        }
        //rewrite selection and elevation colors
        writeElevationColors();
        writeSelectionColors();
    }

    private static void writeElevationColors() {
        final Color ElevationColorsMask = Color.WHITE;
        ELEVATION_COLORS = new ElevationColors()
                .setDP_0(Background)
                .setDP_1(AlphaCompositedRGB(ElevationColorsMask, Background, 0.01f))
                .setDP_2(AlphaCompositedRGB(ElevationColorsMask, Background, 0.02f))
                .setDP_3(AlphaCompositedRGB(ElevationColorsMask, Background, 0.03f))
                .setDP_4(AlphaCompositedRGB(ElevationColorsMask, Background, 0.04f))
                .setDP_6(AlphaCompositedRGB(ElevationColorsMask, Background, 0.06f))
                .setDP_8(AlphaCompositedRGB(ElevationColorsMask, Background, 0.08f))
                .setDP_12(AlphaCompositedRGB(ElevationColorsMask, Background, 0.12f))
                .setDP_16(AlphaCompositedRGB(ElevationColorsMask, Background, 0.14f))
                .setDP_24(AlphaCompositedRGB(ElevationColorsMask, Background, 0.16f));
    }
    private static void writeSelectionColors() {
        Color bg = AlphaCompositedRGB(Accent, Background, 0.2f);
        SELECTION_COLORS = new ThemeSelectionColors(bg, TextPrimary);
    }
    private static class DarkButtonColors {
        private static ButtonColors colors;
        private static final Color ForegroundColor = new Color(0xBBBBBB);
        private static final Color BackgroundColor = new Color(0x1AFFFFFF, true);
        private static final Color BackgroundMousePressedColor = new Color(0x14FFFFFF, true);
        private static final Color BackgroundMouseOverColor = new Color(0x14FFFFFF, true);

        static ButtonColors getColors() {
            if (colors == null)
                colors = new ButtonColors(BackgroundColor, ForegroundColor, BackgroundMousePressedColor, BackgroundMouseOverColor);
            return colors;
        }
    }
}
