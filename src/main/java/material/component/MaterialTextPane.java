package material.component;

import java.awt.*;
import material.Padding;
import material.component.enums.LabelStyle;
import material.theme.ThemeColors;
import material.theme.ThemeManager;

import javax.swing.*;

public class MaterialTextPane extends JTextPane {
    private Padding padding = new Padding(0);
    private LabelStyle labelStyle;
    private boolean isBold = true;
    private boolean isItalic = false;


    public MaterialTextPane(String text, LabelStyle style) throws HeadlessException {
        this.labelStyle = style;
        this.setOpaque(false);
        this.setBackground(ThemeColors.TransparentColor);
        this.setEditable(false);
        this.setFocusable(true);
        this.setText(text);
        this.setBorder(null);
        updateTheme();
        ThemeManager.getInstance().addThemeListener(this::updateTheme);

    }

    public MaterialTextPane(String text) throws HeadlessException {
        this(text, LabelStyle.SECONDARY);
    }

    public MaterialTextPane() throws HeadlessException {
        this("", LabelStyle.SECONDARY);
    }


    private void updateTheme() {
        var colors = ThemeColors.getInstance();
        if (labelStyle != null) {
            switch (labelStyle) {
                case PRIMARY -> {
                    setForeground(colors.getTextPrimary());
                }
                case SECONDARY -> {
                    setForeground(colors.getTextSecondary());
                }
            }
            repaint();
            applyFontStyle();
        } else {
            labelStyle = LabelStyle.SECONDARY;
            updateTheme();
        }
    }

    private void applyFontStyle() {
        Font f = this.getFont();
        if (f != null) {
            if (isBold && isItalic)
                this.setFont(new Font(f.getName(), Font.BOLD & Font.ITALIC, f.getSize()));
            else if (isBold) {
                this.setFont(new Font(f.getName(), Font.BOLD, f.getSize()));
            } else if (isItalic) {
                this.setFont(new Font(f.getName(), Font.ITALIC, f.getSize()));
            }
            this.repaint();
        }

    }

    public LabelStyle getLabelStyle() {
        return labelStyle;
    }

    public MaterialTextPane setLabelStyle(LabelStyle labelStyle) {
        this.labelStyle = labelStyle;
        updateTheme();
        return this;
    }

    public boolean isBold() {
        return isBold;
    }

    public MaterialTextPane setBold(boolean bold) {
        isBold = bold;
        repaint();
        return this;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public MaterialTextPane setItalic(boolean italic) {
        isItalic = italic;
        repaint();
        return this;
    }

    public void setFontSize(int size) {
        var f = this.getFont();
        this.setFont(new Font(f.getName(), f.getStyle(), size));
        repaint();
    }

    public int getFontSize() {
        return this.getFont().getSize();
    }

    public Padding getPadding() {
        return padding;
    }

    public MaterialTextPane setPadding(Padding padding) {
        this.padding = padding;
        return this;
    }
}
