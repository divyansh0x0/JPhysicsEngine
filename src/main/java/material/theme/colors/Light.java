package material.theme.colors;

import material.tools.ColorUtils;
import material.utils.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;

import static material.tools.ColorUtils.AlphaCompositedRGB;

public class Light implements Themeable {
    public Light() {
        setAccentColor(Accent,false);
        writeSelectionColors();
        writeElevationColors();
    }
    private  final int COMPOSITING_ALPHA = 20;
    private  final Color BACKGROUND_BASE = new Color(0xEAEAEA);
    private  @Nullable Color defaultAccentColor;
    private  @Nullable Color defaultBgColor;

    private  @NotNull Color Accent = new Color(0x0E28B0);
    private  @NotNull Color Background =  BACKGROUND_BASE; // accent color mixed in background
    private  @NotNull Color activeItemColor = ColorUtils.AlphaCompositedRGB(Accent, BACKGROUND_BASE,  COMPOSITING_ALPHA * 8);
    private  final Color ColorOnAccent = new Color(0xE3E3E3);

    private  final Color TextPrimary = new Color(0x101010);
    private  final Color TextSecondary = new Color(0x343434);


    private  final Color BackgroundDanger = new Color(0xEF6F6F);
    private  final Color BackgroundSuccess = new Color(0x68EC68);
    private  final Color BackgroundWarn = new Color(0x111111);
    private  final Color FocusedBorderColor = new Color(0xDCEC73);
    private  ElevationColors ELEVATION_COLORS;
    private  ThemeSelectionColors SELECTION_COLORS;
    private  boolean isTinted;


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
        return LightButtonColors.getColors();
    }

    @Override
    public @NotNull Color getActiveBackgroundColor() {
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
        isTinted = tintBackground;
        if (defaultAccentColor == null)
            defaultAccentColor = Accent; // Saving the default accent color
        if (defaultBgColor == null)
            defaultBgColor = Background;
        Accent = Objects.requireNonNullElseGet(accentColor, () -> defaultAccentColor);

        Color colorToMerge = new Color(Accent.getRed(), Accent.getGreen(), Accent.getBlue(), COMPOSITING_ALPHA);
        activeItemColor = ColorUtils.AlphaCompositedRGB(colorToMerge, BACKGROUND_BASE, (COMPOSITING_ALPHA * 4) / 255f);

        if(tintBackground) {
            //Dynamic background
            Color bgTemp = ColorUtils.AlphaCompositedRGB(colorToMerge, BACKGROUND_BASE,0.01f);
            Background = Objects.requireNonNullElseGet(bgTemp, () -> defaultBgColor);
        }
        //rewrite selection and elevation colors
        writeElevationColors();
        writeSelectionColors();
    }
    private  void writeElevationColors() {
        Color bg1 = new Color(0xEAEAEA);
        Color bg2 = new Color(0xEFEFEF);
        Color bg3 = new Color(0xF1F1F1);
        Color bg4 = new Color(0xF3F3F3);
        Color bg6 = new Color(0xF6F6F6);
        Color bg8 = new Color(0xF9F9F9);
        Color bg12 = new Color(0xFBFBFB);
        Color bg16 = new Color(0xFCFCFC);
        Color bg24 = new Color(0xFFFFFF);
        if(isTinted)
            ELEVATION_COLORS = new ElevationColors()
                .setDP_0(AlphaCompositedRGB(Accent, Background,0.035f))
                .setDP_1(AlphaCompositedRGB(Accent, bg1, 0.030f))
                .setDP_2(AlphaCompositedRGB(Accent, bg2, 0.025f))
                .setDP_3(AlphaCompositedRGB(Accent, bg3, 0.020f))
                .setDP_4(AlphaCompositedRGB(Accent, bg4, 0.015f))
                .setDP_6(AlphaCompositedRGB(Accent, bg6, 0.01f))
                .setDP_8(AlphaCompositedRGB(Accent, bg8,0.009f))
                .setDP_12(AlphaCompositedRGB(Accent, bg12,0.006f))
                .setDP_16(AlphaCompositedRGB(Accent, bg16,0.003f))
                .setDP_24(AlphaCompositedRGB(Accent, bg24,0.000f));
        else
            ELEVATION_COLORS = new ElevationColors(Background,bg1,bg2,bg3,bg4,bg6,bg8,bg12,bg16,bg24);
    }
    private  void writeSelectionColors() {
        Color bg = AlphaCompositedRGB(Accent, Background, 0.5f);
        SELECTION_COLORS = new ThemeSelectionColors(bg, TextPrimary);
    }
    private  class LightButtonColors{
        private static   ButtonColors colors;
        private static   final Color BackgroundColor = new Color(0x1A000000, true);
        private static   final Color ForegroundColor = new Color(0x000000);
        private static   final Color BackgroundMousePressedColor = new Color(0x14000000, true);
        private static   final Color BackgroundMouseOverColor = new Color(0x1F000000, true);

         static ButtonColors getColors() {
            if(colors == null)
                colors = new ButtonColors(BackgroundColor, ForegroundColor, BackgroundMousePressedColor, BackgroundMouseOverColor);
            return colors;
        }
    }
}
