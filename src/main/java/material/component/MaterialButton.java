package material.component;

import material.constraints.LayoutConstraints;
import material.containers.MaterialPanel;
import material.listeners.MouseClickListener;
import material.theme.ThemeColors;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class MaterialButton extends MaterialComponent {
    private String text = "";
    private final ArrayList<MouseClickListener> clickListeners = new ArrayList<>();
    public final double DARKENING_AND_LIGHTENING_PERCENTAGE = 10;
    private Color forcedColor;
    Color focusedBorderColor = Color.yellow;
    private int CornerRadius = LayoutConstraints.CORNER_RADIUS;
    private Color mouseOverColor = null;
    private Color mouseDownColor = null;
    public MaterialButton(String text) {
        super();
        this.setText(text);
        this.setPreferredSize(new Dimension(200, 100));
        this.setFocusable(true);
        addListeners();
    }


    public MaterialButton() {
        this("");
    }


    @Override
    public void updateTheme() {
        ThemeColors colors = ThemeColors.getInstance();
        Color bgColor = forcedColor == null? colors.getAccent() : forcedColor;
        Color fgColor = colors.getColorOnAccent();

        focusedBorderColor = colors.getFocusedBorderColor();

        applyFontStyle();
        animateBG(bgColor);
        animateFG(fgColor);

    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int x = 0, y = 0;
        int width = this.getWidth(), height = this.getHeight();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //draw bg
        g2d.setColor(this.getBackground());
        g2d.fillRoundRect(x, y, width, height, CornerRadius, CornerRadius);

        //draw text
        g2d.setFont(this.getFont());
        FontMetrics ft = g2d.getFontMetrics();
        Rectangle2D r2 = ft.getStringBounds(getText(), g2d);
        int tX = (int) (width - r2.getWidth()) / 2; //Text x coordinate
        int tY = (int) (((height - r2.getHeight()) / 2) + ft.getAscent()); //Text y coordinate
        g2d.setColor(this.getForeground());
        g2d.drawString(getText(), tX, tY);
        g2d.dispose();
    }

    private boolean isPressed = false;

    protected void addListeners() {
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                released(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                entered();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exited();

            }
        });
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    for (var listener : clickListeners)
                        listener.clicked(e);
                    exited();

                }
            }
        });
    }

    private synchronized void released(@NotNull MouseEvent e) {
        if (isEnabled() && !e.isConsumed()) {
            for (var listener : clickListeners)
                listener.clicked(e);
        }
    }

    private void exited() {
        if (getParent() instanceof MaterialPanel) {
            SwingUtilities.invokeLater(()->{
                setBackground(getActiveBgColor());
            });
        }
    }

    private void entered() {
        if (getParent() instanceof MaterialPanel) {
            SwingUtilities.invokeLater(()->{
                setBackground(getActiveBgColor().brighter());
            });
        }
    }

    private Color getActiveBgColor(){
        return forcedColor == null ? ThemeColors.getInstance().getAccent() : forcedColor;
    }

    public void addClickListener(MouseClickListener listener) {
        if (!clickListeners.contains(listener))
            clickListeners.add(listener);
    }

    public Color getForcedColor() {
        return forcedColor;
    }

    public void setForcedColor(Color forcedColor) {
        this.forcedColor = forcedColor;
        updateTheme();
    }

    public int getCornerRadius() {
        return CornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        CornerRadius = cornerRadius;
        repaint();
    }

    public void setFontSize(int size) {
        var f = this.getFont();
        this.setFont(new Font(f.getName(), f.getStyle(), size));
        repaint();
    }

    public String getText() {
        return text;
    }

    public MaterialButton setText(String text) {
        this.text = text;
        repaint();
        return this;
    }
}
