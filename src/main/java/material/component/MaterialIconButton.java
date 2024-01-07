package material.component;

import material.Padding;
import material.listeners.MouseClickListener;
import material.theme.ThemeColors;
import material.theme.colors.ButtonColors;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;
import material.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class MaterialIconButton extends MaterialComponent {
    protected final ArrayList<MouseClickListener> mouseClickListeners = new ArrayList<>();
    private final int gap = 10;
    private double iconSizeRatio = 0.9; //Percentage of the size of the icon in comparison to the button;
    Padding padding = new Padding(10);
    private boolean isBold = true;
    private boolean isItalic = false;
    private int CORNER_RADIUS = 0;
    private FontIcon Icon;
    private String Text;

    private boolean isTransparentBackground;

    public MaterialIconButton(Ikon icon, String text) {
        super();
        if (icon != null) {
            Icon = new FontIcon();
            Icon.setIkon(icon);
            if (getForeground() != null)
                Icon.setIconColor(getForeground());
        }
        Text = text;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && isEnabled()) {
                    leftMouseButtonCallback(e);
                }
            }
        });
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
//        Color focusedBorderColor = ThemeColors.getInstance().getFocusedBorderColor();
        int iSize = 0;
        int iX = 0;
        int iY;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //background
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

//        //Keyboard focus color
//        if (hasFocus()) {
//            int bx = focusedBorderSize / 2, by = focusedBorderSize / 2;
//            int bWidth = getWidth() - (focusedBorderSize);
//            int bHeight = getHeight() - (focusedBorderSize);
//            g2d.setStroke(new BasicStroke(focusedBorderSize));
//            Shape border = new RoundRectangle2D.Double(bx, by, bWidth, bHeight, CORNER_RADIUS, CORNER_RADIUS);
//            g2d.setColor(focusedBorderColor);
//            g2d.draw(border);
//        }

        //Draw icon
        if (getIcon() != null) {
            int smallerSide = Math.min(getWidth(), getHeight()); // size of smaller size of button
            if (smallerSide == getWidth())
                iSize = (int) ((smallerSide - padding.getLeft() - padding.getRight()) * iconSizeRatio);
            else
                iSize = (int) ((smallerSide - padding.getTop() - padding.getBottom()) * iconSizeRatio);

            Icon.setIconSize(iSize);
            Icon.setIconColor(getForeground());

            iX = padding.getLeft();
            iY = (getHeight() - iSize) / 2;
            try {
                var iImage = Icon.toImageIcon();
                if (getText().isEmpty())
                    iX = (getWidth() - iSize) / 2;
                g2d.drawImage(iImage.getImage(), iX, iY, iImage.getIconWidth(), iImage.getIconHeight(), null);
            }catch (Exception e){
                Log.error(e + " in icon button:  " + getText() + " | " + getIcon().getIkon());
            }
        }
        if (!getText().isEmpty()) {
            //draw text
            g2d.setFont(this.getFont());
            FontMetrics ft = g2d.getFontMetrics();
            Rectangle2D r2 = ft.getStringBounds(getText(), g2d);
//            int tX = (int) (this.getWidth() - r2.getWidth() - padding); //Text x coordinate
            int tX = iX + iSize + gap;
            int tY = (int) (((this.getHeight() - r2.getHeight()) / 2) + ft.getAscent()); //Text y coordinate
            g2d.setColor(this.getForeground());
            g2d.drawString(getText(), tX, tY);
        }
    }

    public MaterialIconButton(Ikon icon) {
        this(icon, "");
    }

    public MaterialIconButton() {
        this(null);
    }

    @Override
    public void updateTheme() {
        ButtonColors buttonColors = ThemeColors.getInstance().getIconButtonColors();
        this.animateFG(buttonColors.getForegroundColor());
        if (!isTransparentBackground) {
            this.setBackground(buttonColors.getBackgroundColor());
        } else {
            this.setBackground(ThemeColors.TransparentColor);
        }
    }


    boolean isMouseOver(int x, int y) {
        return x >= 0 && y >= 0 && x <= getWidth() && y <= getHeight();
    }

    public int getCornerRadius() {
        return CORNER_RADIUS;
    }

    public MaterialIconButton setCornerRadius(int cornerRadius) {
        this.CORNER_RADIUS = cornerRadius;
        repaint();
        return this;
    }

    public FontIcon getIcon() {
        return Icon;
    }

    public MaterialIconButton setIcon(Ikon icon) {
        if(Icon == null)
            Icon = new FontIcon();
        Icon.setIkon(icon);
        repaint();
        return this;
    }

    public String getText() {
        return Text;
    }

    public MaterialIconButton setText(String text) {
        Text = text != null ? text : "";
        repaint();
        return this;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
        applyFontStyle();
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
        applyFontStyle();
    }

    public Padding getPadding() {
        return padding;
    }

    public MaterialIconButton setPadding(Padding padding) {
        this.padding = padding;
        repaint();
        return this;
    }

    public double getIconSizeRatio() {
        return iconSizeRatio;
    }

    public MaterialIconButton setIconSizeRatio(double ratio) {
        this.iconSizeRatio = ratio;
        repaint();
        return this;
    }

    public MaterialIconButton setTransparentBackground(boolean b) {
        setOpaque(!b);
        isTransparentBackground = b;
        updateTheme();
        return this;
    }

    public boolean isTransparentBackground() {
        return isTransparentBackground;
    }

    public int getGap() {
        return gap;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        applyFontStyle();
    }

    public void addLeftClickListener(MouseClickListener listener) {
        if (!mouseClickListeners.contains(listener))
            mouseClickListeners.add(listener);
    }

    public void removeLeftClickListeners() {
        mouseClickListeners.clear();
    }

    private void leftMouseButtonCallback(@NotNull MouseEvent e) {
        mouseClickListeners.forEach(listener -> listener.clicked(e));
    }
}
