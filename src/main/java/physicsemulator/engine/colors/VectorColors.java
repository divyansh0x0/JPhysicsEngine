package physicsemulator.engine.colors;

import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.ThemeType;

public class VectorColors {
    public static VectorColorsModel getColorModel(){
        if(ThemeManager.getInstance().getThemeType().equals(ThemeType.Light))
            return LightMode.getInstance();
        else
            return DarkMode.getInstance();
    }
}
