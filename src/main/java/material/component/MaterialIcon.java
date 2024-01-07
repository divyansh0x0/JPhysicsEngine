package material.component;

import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.ThemeType;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

import java.awt.*;

public class MaterialIcon extends MaterialComponent{
    private final FontIcon Icon;
    private Color forcedDarkThemeColor;
    private Color forcedLightThemeColor;
    private int padding;
    private float iconSizeRatio = 0.9f;

    public MaterialIcon(@Nullable Ikon icon) {
        Icon = new FontIcon();
        if(icon != null)
            Icon.setIkon(icon);
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        int maxSize = Math.max(w, h);
        int size = (int) ((maxSize - padding * 2) * iconSizeRatio);
        if(size > 0){
            int x = (w-size)/2;
            int y = (h-size)/2;

            Graphics2D g2d = (Graphics2D) g;
            Icon.setIconColor(getForeground());
            Icon.setIconSize(size);
            g2d.drawImage(Icon.toImageIcon().getImage(),x,y,null);
        }
        super.paintComponent(g);
    }

    @Override
    public void updateTheme() {
        if(forcedDarkThemeColor == null && forcedLightThemeColor == null){
            Color color = ThemeColors.getInstance().getIconButtonColors().getForegroundColor();
            setForeground(color);
        }
        else{
            ThemeType themeType = ThemeManager.getInstance().getThemeType();
            if(forcedDarkThemeColor != null && themeType.equals(ThemeType.Dark)){
                setForeground(forcedDarkThemeColor);
            } else if (forcedLightThemeColor != null && themeType.equals(ThemeType.Light)) {
                setForeground(forcedLightThemeColor);
            }
        }
        repaint();
    }

    public Ikon getIcon() {
        return Icon.getIkon();
    }

    public void setIcon(Ikon icon) {
        Icon.setIkon(icon);
    }

    public Color getForcedDarkThemeColor() {
        return forcedDarkThemeColor;
    }

    public void setForcedDarkThemeColor(Color forcedDarkThemeColor) {
        this.forcedDarkThemeColor = forcedDarkThemeColor;
    }

    public Color getForcedLightThemeColor() {
        return forcedLightThemeColor;
    }

    public void setForcedLightThemeColor(Color forcedLightThemeColor) {
        this.forcedLightThemeColor = forcedLightThemeColor;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public float getIconSizeRatio() {
        return iconSizeRatio;
    }

    public void setIconSizeRatio(float iconSizeRatio) {
        this.iconSizeRatio = iconSizeRatio;
    }
}
