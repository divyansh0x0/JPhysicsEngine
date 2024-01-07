package material.theme.colors;

import org.jetbrains.annotations.Nullable;

import java.awt.*;

public interface Themeable {
    /**
     * Background color of the app
     */
    Color getBackgroundColor();

    /**
     * Secondary color of the app. Used by buttons, checkboxes, text boxes, radio buttons etc;
     */
    Color getAccentColor();

    /**
     * Text color on primary color
     */
    Color getColorOnAccent();

    /**
     * Get primary color of text. Used for headings.
     */
    Color getTextColorPrimary();

    /**
     * Get primary color of text. Used for secondary text or paragraphs.
     */
    Color getTextColorSecondary();

    Color getBackgroundColorDanger();

    Color getBackgroundColorSuccess();

    Color getBackgroundColorWarn();

    Color getFocusedBorderColor();
    ButtonColors getIconButtonColors();

    Color getActiveBackgroundColor();

    ElevationColors getElevationColors();

    ThemeSelectionColors getSelectionColors();
    void setAccentColor(@Nullable Color accentColor, boolean tintBackground);
}
