package material.component;

import material.Padding;
import material.component.enums.LabelStyle;
import material.fonts.MaterialFonts;
import material.theme.ThemeColors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import material.utils.GraphicsUtils;
import material.utils.Log;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class MaterialLabel extends MaterialComponent {

    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    private LabelStyle labelStyle;
    private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
    private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
    private String text;
    private boolean isForcedFg = false;
    private @NotNull Padding padding = new Padding(0);
    private boolean enableAutoResize = false;

    public MaterialLabel(String text, LabelStyle style, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) throws HeadlessException {
        super();
        this.labelStyle = style;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.setOpaque(false);
        this.setText(text);
//        this.setFont(MaterialFonts.getInstance().getDefaultFont());
        updateTheme();
    }


    public MaterialLabel(String text, LabelStyle style) throws HeadlessException {
        this(text, style, HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
    }

    public MaterialLabel(String text) throws HeadlessException {
        this(text, LabelStyle.SECONDARY);
    }

    public MaterialLabel() throws HeadlessException {
        this("");
    }

    public void updateTheme() {
        var colors = ThemeColors.getInstance();
        if (labelStyle != null) {
            switch (labelStyle) {
                case PRIMARY -> {
                    animateFG(colors.getTextPrimary());
                }
                case SECONDARY -> {
                    animateFG(colors.getTextSecondary());
                }
            }
            this.setBackground(ThemeColors.TransparentColor);

            repaint();
            applyFontStyle();
        } else {
            labelStyle = LabelStyle.SECONDARY;
            updateTheme();
            if (!isForcedFg) {
                if (labelStyle != null) {
                    switch (labelStyle) {
                        case PRIMARY -> {
                            animateFG(colors.getTextPrimary());
                        }
                        case SECONDARY -> {
                            animateFG(colors.getTextSecondary());
                        }
                    }
                    this.setBackground(ThemeColors.TransparentColor);
                    repaint();
                    applyFontStyle();
                } else {
                    labelStyle = LabelStyle.SECONDARY;
                    updateTheme();
                }
            }
        }
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        Log.info("Custom preferred size set for this label: " + this);
    }

    @Override
    public Dimension getPreferredSize() {
        if(enableAutoResize) {
            FontMetrics fontMetrics = getFontMetrics(getFont());
            int width = fontMetrics.stringWidth(text) + padding.getHorizontal();
            int height = fontMetrics.getHeight() + padding.getVertical();
            return new Dimension(width, height);
        }
        else
            return super.getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        FontMetrics fm = g2d.getFontMetrics();
        int availableWidth = getWidth() - padding.getHorizontal();
        int width = this.getWidth(), height = this.getHeight();
        String clippedText = GraphicsUtils.clipString(g2d, text, availableWidth);

        //draw text
        g2d.setColor(getForeground());
        g2d.setFont(this.getFont().deriveFont(getFontSize()));
        FontMetrics ft = g2d.getFontMetrics();
        Rectangle2D stringBounds = ft.getStringBounds(text, g2d);
        int tX = padding.getLeft(); //Text x coordinate
        switch (horizontalAlignment) {
            case LEFT -> tX = padding.getLeft();
            case CENTER -> tX = (int) (width - stringBounds.getWidth()) / 2; //Text x coordinate
            case RIGHT -> tX = (int) ((width - stringBounds.getWidth()) - padding.getRight()); //Text x coordinate
        }
        int tY = padding.getTop(); //Text y coordinate
        switch (verticalAlignment) {
            case TOP -> tY = (int) (padding.getTop() + stringBounds.getHeight());
            case CENTER -> tY = (int) (((height - stringBounds.getHeight()) / 2) + ft.getAscent());
            case BOTTOM -> tY = (int) (getHeight() - stringBounds.getHeight() - padding.getBottom());

        }
        super.drawCompatibleString(clippedText, tX, tY, g2d, getFont());
        g2d.dispose();
    }
    public LabelStyle getLabelStyle() {
        return labelStyle;
    }

    public MaterialLabel setLabelStyle(LabelStyle labelStyle) {
        this.labelStyle = labelStyle;
        updateTheme();
        return this;
    }


    public void setForcedColor(@Nullable Color fg) {
        if (fg == null)
            isForcedFg = false;
        else {
            isForcedFg = true;
            animateFG(fg);
        }
    }

    public boolean isForcedFg() {
        return isForcedFg;
    }

    public void setText(String text) {
        this.text = text;
        repaint();
    }

    private Dimension computeRequiredSize(String str) {
        if (getGraphics() != null) {
            FontMetrics fontMetrics = getGraphics().create().getFontMetrics();
            double strH = fontMetrics.getHeight();
            double strW = fontMetrics.stringWidth(str);
            double maxW = getMaximumSize().getWidth();
            double rows = 1;
            if (strW > maxW)
                rows = strW / maxW;

            strH = (strH * rows) + fontMetrics.getAscent() + fontMetrics.getDescent() + padding.getVertical();
            strW = strW / rows - padding.getHorizontal();

            return new Dimension((int) Math.ceil(strW), (int) Math.ceil(strH));
        }
        return new Dimension(5 * str.length() + padding.getHorizontal(), (int) (getFontSize() + padding.getVertical()));
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        repaint();
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        repaint();
    }


    public @NotNull Padding getPadding() {
        return padding;
    }

    public void setPadding(@NotNull Padding padding) {
        this.padding = padding;
        repaint();
    }

    public enum HorizontalAlignment {
        LEFT,
        CENTER,
        RIGHT

    }

    public enum VerticalAlignment {
        TOP,
        CENTER,
        BOTTOM
    }

    public boolean isEnableAutoResize() {
        return enableAutoResize;
    }

    public void setEnableAutoResize(boolean enableAutoResize) {
        this.enableAutoResize = enableAutoResize;
        revalidate();
    }
}

