package material.theme;

import material.theme.colors.*;
import material.theme.enums.ElevationDP;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public final class ThemeColors {
    public static final Color TransparentColor = new Color(0x0, true);
    private static ThemeColors Instance;
    private @NotNull Color Background;
    private @NotNull Color Accent;
    private @NotNull Color ColorOnAccent;

    private @NotNull Color TextPrimary;
    private @NotNull Color TextSecondary;


    private @NotNull Color BackgroundDanger;
    private @NotNull Color BackgroundSuccess;
    private @NotNull Color BackgroundWarn;
    private final Themeable defaultTheme = new Dark();
    private @NotNull material.theme.colors.ElevationColors ElevationColors = defaultTheme.getElevationColors();
    private @NotNull ButtonColors IconButtonColors = defaultTheme.getIconButtonColors();
    private @NotNull Color FocusedBorderColor = defaultTheme.getFocusedBorderColor();
    private @NotNull Color ActiveBackgroundColor = defaultTheme.getActiveBackgroundColor();

    private @NotNull ThemeSelectionColors SelectionColors = defaultTheme.getSelectionColors();
    private ThemeColors() {
        Background = defaultTheme.getBackgroundColor();
        Accent = defaultTheme.getAccentColor();
        ColorOnAccent = defaultTheme.getColorOnAccent();
        TextPrimary = defaultTheme.getTextColorPrimary();
        TextSecondary = defaultTheme.getTextColorSecondary();
        BackgroundDanger = defaultTheme.getBackgroundColorDanger();
        BackgroundSuccess = defaultTheme.getBackgroundColorSuccess();
        BackgroundWarn = defaultTheme.getBackgroundColorWarn();
    }

    @NotNull
    public Color getBackground() {
        return Background;
    }

    void setBackground(@NotNull Color background) {
        Background = background;
    }

    @NotNull
    public Color getAccent() {
        return Accent;
    }

    void setAccent(@NotNull Color accent) {
        Accent = accent;
    }

    @NotNull
    public Color getColorOnAccent() {
        return ColorOnAccent;
    }

    void setColorOnAccent(@NotNull Color colorOnAccent) {
        ColorOnAccent = colorOnAccent;
    }

    @NotNull
    public Color getTextPrimary() {
        return TextPrimary;
    }

    protected void setTextPrimary(@NotNull Color textPrimary) {
        TextPrimary = textPrimary;
    }

    @NotNull
    public Color getTextSecondary() {
        return TextSecondary;
    }

    protected void setTextSecondary(@NotNull Color textSecondary) {
        TextSecondary = textSecondary;
    }
    public @NotNull Color getActiveBackgroundColor() {
        return ActiveBackgroundColor;
    }

    public void setActiveBackgroundColor(@NotNull Color activeBackgroundColor) {
        ActiveBackgroundColor = activeBackgroundColor;
    }
    @NotNull
    public Color getBackgroundDanger() {
        return BackgroundDanger;
    }

    protected void setBackgroundDanger(@NotNull Color backgroundDanger) {
        BackgroundDanger = backgroundDanger;
    }

    @NotNull
    public Color getBackgroundSuccess() {
        return BackgroundSuccess;
    }

    protected void setBackgroundSuccess(@NotNull Color backgroundSuccess) {
        BackgroundSuccess = backgroundSuccess;
    }

    @NotNull
    public Color getBackgroundWarn() {
        return BackgroundWarn;
    }

    protected void setBackgroundWarn(@NotNull Color backgroundWarn) {
        BackgroundWarn = backgroundWarn;
    }

    @NotNull
    public Color getFocusedBorderColor() {
        return FocusedBorderColor;
    }

    protected void setFocusedBorderColor(@NotNull Color focusedBorderColor) {
        FocusedBorderColor = focusedBorderColor;
    }

    @NotNull
    private material.theme.colors.ElevationColors getElevationColors() {
        return ElevationColors;
    }

    protected void setElevationColors(@NotNull material.theme.colors.ElevationColors elevationColors) {
        ElevationColors = elevationColors;
    }

    @NotNull
    public Color getColorByElevation(@NotNull ElevationDP elevationDp) {
        material.theme.colors.ElevationColors el = getInstance().getElevationColors();
        switch (elevationDp) {
            case _0 -> {
                return el.getDP_0();
            }
            case _1 -> {
                return el.getDP_1();
            }
            case _2 -> {
                return el.getDP_2();
            }
            case _3 -> {
                return el.getDP_3();
            }
            case _4 -> {
                return el.getDP_4();
            }
            case _6 -> {
                return el.getDP_6();
            }
            case _8 -> {
                return el.getDP_8();
            }
            case _12 -> {
                return el.getDP_12();
            }
            case _16 -> {
                return el.getDP_16();
            }
            case _24 -> {
                return el.getDP_24();
            }
        }
        return el.getDP_24();
    }

    public @NotNull ButtonColors getIconButtonColors() {
        return IconButtonColors;
    }

    protected void setIconButtonColors(ButtonColors iconButtonColors) {
        IconButtonColors = iconButtonColors;
    }

    public @NotNull ThemeSelectionColors getSelectionColors() {
        return SelectionColors;
    }

    protected void setSelectionColors(@NotNull ThemeSelectionColors selectionColors) {
        SelectionColors = selectionColors;
    }

    public static ThemeColors getInstance() {
        if (Instance == null)
            Instance = new ThemeColors();
        return Instance;
    }

}
