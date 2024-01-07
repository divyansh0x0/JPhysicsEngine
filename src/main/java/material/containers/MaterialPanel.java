package material.containers;

import material.MaterialParameters;
import material.animation.AnimationLibrary;
import material.animation.MaterialFixedTimer;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.ElevationDP;
import material.utils.Log;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class MaterialPanel extends JPanel implements Serializable {
    private @Nullable ElevationDP elevationDP = ElevationDP._0;

    private int CornerRadius = 0;
    private boolean isFirstTimeLoaded = true;
    private boolean isColorChangeAnimated = true;
    private boolean isBackgroundNoiseEnabled = false;
    private static BufferedImage imageWithNoise;
    private float noiseImageAlpha = 1f;
    private BufferedImage backgroundImg;

    private int backgroundHeight;

    public MaterialPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        ThemeManager.getInstance().addThemeListener(this::updateTheme);

    }

    public MaterialPanel(LayoutManager layout) {
        this(layout, true);
    }

    public MaterialPanel(boolean isDoubleBuffered) {
        this(new FlowLayout(), isDoubleBuffered);
    }

    public MaterialPanel() {
        this(true);
    }

    public void updateTheme() {
        if (elevationDP != null) {
            Color bgColor = ThemeColors.getInstance().getColorByElevation(elevationDP);
            setOpaque(true);
            if (isFirstTimeLoaded) {
                setBackground(bgColor);
                isFirstTimeLoaded = false;
            } else
                animateBG(bgColor);
        } else {
            setOpaque(false);
            Color transparent = ThemeColors.TransparentColor;
            if (isFirstTimeLoaded) {
                setBackground(transparent);
                isFirstTimeLoaded = false;
            } else
                animateBG(transparent);
        }
        if (isBackgroundNoiseEnabled)
            rewriteNoiseImage();
        repaint();
    }

    public void animateBG(Color to) {
        if (isAnimatedColorChange()) {
            AnimationLibrary.animateBackground(this, to, MaterialParameters.COLOR_ANIMATION_DURATION.toMillis());
        } else
            setBackground(to);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        updateTheme();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        if (getCornerRadius() > 0) {
            g2d.setPaint(getBackground());
            g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), getCornerRadius(), getCornerRadius()));
        }
        if (isBackgroundNoiseEnabled && imageWithNoise != null) {
            Log.info("Rendering noise");
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, noiseImageAlpha));
            g2d.drawImage(imageWithNoise, 0, 0, getWidth(), getHeight(), null);
        }
        if (backgroundImg != null) {
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            for (int i = 0; i < getHeight(); i += backgroundImg.getHeight()) {
                g2d.drawImage(backgroundImg, 0, i, null);
            }
        }
        g2d.dispose();
    }

    public @Nullable ElevationDP getElevationDP() {
        return elevationDP;
    }

    /**
     * ElevationColors of the panel. Null is used to set the panel transparent
     * (Note: setOpaque() should be true for it to work)
     */
    public MaterialPanel setElevationDP(@Nullable ElevationDP elevationDP) {
        this.elevationDP = elevationDP;
        updateTheme();
        return this;
    }

    public int getCornerRadius() {
        return CornerRadius;
    }

    public MaterialPanel setCornerRadius(int cornerRadius) {
        CornerRadius = cornerRadius;
        setOpaque(CornerRadius <= 0);
        repaint();
        return this;
    }

    public void setAnimatedColorChange(boolean b) {
        this.isColorChangeAnimated = b;
    }

    public boolean isAnimatedColorChange() {
        return this.isColorChangeAnimated;
    }

    public boolean isBackgroundNoiseEnabled() {
        return isBackgroundNoiseEnabled;
    }

    public void setBackgroundNoiseEnabled(boolean backgroundNoiseEnabled, @Range(from = 0, to = 1) float alpha) {
        if (backgroundNoiseEnabled == true) {
            Log.warn("Background noise enabled");
        }
        isBackgroundNoiseEnabled = backgroundNoiseEnabled;
        if (imageWithNoise == null)
            rewriteNoiseImage();
        noiseImageAlpha = alpha;
        revalidate();
        repaint();
    }

    public void setBackgroundNoiseEnabled(boolean backgroundNoiseEnabled) {
        setBackgroundNoiseEnabled(backgroundNoiseEnabled, 1f);
    }

    private void rewriteNoiseImage() {
        if (isBackgroundNoiseEnabled) {
            ThemeManager.getInstance().getThemeBasedNoiseImage(imageWithNoise -> {
                SwingUtilities.invokeLater(() -> {
                    MaterialPanel.imageWithNoise = imageWithNoise;
                    repaint();
                });
            });
        }
    }

    MaterialFixedTimer animationTimer;

    public void setBackgroundImage(BufferedImage img) {
        this.backgroundImg = img;
        repaint();
    }
}
