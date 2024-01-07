package material.ui;

import material.MaterialParameters;
import material.containers.MaterialScrollPane;
import material.theme.ThemeColors;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.*;
import material.tools.ColorUtils;

public class MaterialScrollbarUI extends BasicScrollBarUI {
    private final MaterialScrollPane jScrollPane;
    private static final int SCROLLBAR_ALPHA_ROLLOVER = 100;
    private static final int SCROLLBAR_ALPHA = 50;
    private static final int MIN_THUMB_SIZE = 10;
    private static final int SCROLLBAR_SIZE = 10;

    public MaterialScrollbarUI(MaterialScrollPane jScrollPane) {
        this.jScrollPane = jScrollPane;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new EmptyButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new EmptyButton();
    }

    @Override
    protected void paintDecreaseHighlight(Graphics g) {
        super.paintDecreaseHighlight(g);
    }

    @Override
    protected void paintIncreaseHighlight(Graphics g) {
//        super.paintIncreaseHighlight(g);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Color thumbColor;

        thumbColor = ColorUtils.mergeColors(ThemeColors.getInstance().getAccent(), ThemeColors.getInstance().getBackground());
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//        int alpha = isThumbRollover() ? SCROLLBAR_ALPHA_ROLLOVER : SCROLLBAR_ALPHA;
        int x = thumbBounds.x;
        int y = thumbBounds.y;
        int width = thumbBounds.width;
        width = Math.max(width, MIN_THUMB_SIZE);

        int height = thumbBounds.height;
        height = Math.max(height, MIN_THUMB_SIZE);

        g2d.setColor(thumbColor);
        g2d.fillRoundRect(x, y, width, height, MaterialParameters.CORNER_RADIUS, MaterialParameters.CORNER_RADIUS);
    }

    @Override
    protected void setThumbBounds(int x, int y, int width, int height) {
        super.setThumbBounds(x, y, width, height);
        if(jScrollPane != null)
            jScrollPane.repaint();
    }

    private static class EmptyButton extends JButton {
        public EmptyButton() {
            setOpaque(false);
            setFocusable(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder());
        }
    }
}
