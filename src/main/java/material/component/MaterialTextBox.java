package material.component;

import material.MaterialParameters;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.ElevationDP;
import material.tools.ColorUtils;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MaterialTextBox extends JTextField {

    private static final Ikon searchIcon = FluentUiRegularMZ.SEARCH_28;
    private final FontIcon fontIcon = new FontIcon();
    private static final int DEFAULT_COLUMNS = 200;
    private @Nullable ElevationDP elevationDP = ElevationDP._4;
    private int cornerRadius = MaterialParameters.CORNER_RADIUS;
    private String hint = "";


    public MaterialTextBox(int columns) {
        super(columns);
        Color fg = ThemeColors.getInstance().getTextSecondary();

        setDoubleBuffered(true);
        setCornerRadius(cornerRadius);
        setOpaque(false);
        setDragEnabled(true);

        setForeground(fg);
        setCaretColor(fg);
        setBorder(null);

        if (elevationDP != null)
            setBackground(ThemeColors.getInstance().getColorByElevation(elevationDP));

        fontIcon.setIkon(searchIcon);
        updateTheme();
        ThemeManager.getInstance().addThemeListener(this::updateTheme);
    }

    public MaterialTextBox() {
        this(DEFAULT_COLUMNS);
    }

    private void updateTheme() {
        if (elevationDP != null)
            setBackground(ThemeColors.getInstance().getColorByElevation(elevationDP));
        Color fg = ThemeColors.getInstance().getTextSecondary();
        setForeground(fg);
        setCaretColor(fg);

        fontIcon.setIconColor(fg);

        repaint();
    }



    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), getCornerRadius(), getCornerRadius()));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius);
        super.paintComponent(g2d);
        if (getText().isEmpty()) {
            int h = getHeight();
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            g.setColor(ColorUtils.mergeColors(getBackground(),getForeground()));
            g.drawString(hint, ins.left, h / 2 + fm.getAscent() / 2 - 2);
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0f));
        g2d.setColor(getForeground());
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        g2d.dispose();
    }



    public ElevationDP getElevationDP() {
        return elevationDP;
    }

    public MaterialTextBox setElevationDP(ElevationDP elevationDP) {
        this.elevationDP = elevationDP;
        updateTheme();
        return this;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public MaterialTextBox setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        repaint();
        return this;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
        repaint();
    }
}
