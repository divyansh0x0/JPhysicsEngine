package material.component;

import material.containers.MaterialPanel;
import material.theme.ThemeColors;
import material.theme.enums.ElevationDP;
import org.kordamp.ikonli.Ikon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MaterialNavButton extends MaterialIconButton {

    private boolean isActive = false;
    private Color MouseOverColor;

    Color focusedBorderColor;


    private ElevationDP elevation = ElevationDP._0;

    public MaterialNavButton(Ikon ikon, String text) {
        super(ikon, text);
        setOpaque(false);
        setCornerRadius(0);
        setIconSizeRatio(0.7);
        setBold(true);
        applyFontStyle();
        addListeners();
    }

    public MaterialNavButton(Ikon ikon) {
        this(ikon, "");
    }

    public MaterialNavButton() {
        this(null, "");
    }

    @Override
    public void updateTheme() {
        focusedBorderColor = ThemeColors.getInstance().getFocusedBorderColor();
        animateBG(new Color(0x0, true));

        if (isActive)
            animateFG(ThemeColors.getInstance().getAccent());
        else
            animateFG(ThemeColors.getInstance().getTextPrimary());


//        if (ThemeManager.getInstance().getThemeType().equals(ThemeType.Dark))
//            MouseOverColor = new Color(0xFFFFFFF, true);
//        else
//            MouseOverColor = new Color(0x1B000000, true);
        MouseOverColor = ThemeColors.getInstance().getIconButtonColors().getBackgroundMouseOverColor();

        repaint();
        applyFontStyle();
    }

    protected void addListeners() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                entered();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exited();
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    setActive(!isActive);
                    exited();
                }
            }
        });

    }


    private void exited() {
        if (getParent() instanceof MaterialPanel) {
            SwingUtilities.invokeLater(()->{
                setBackground(ThemeColors.TransparentColor);
            });
        }
    }

    private void entered() {
        if (getParent() instanceof MaterialPanel) {
            SwingUtilities.invokeLater(()->{
                setBackground(MouseOverColor);
            });
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public MaterialNavButton setActive(boolean active) {
        isActive = active;
        updateTheme();
        return this;
    }

    public ElevationDP getElevation() {
        return elevation;
    }

    public MaterialNavButton setElevation(ElevationDP elevation) {
        this.elevation = elevation;
        repaint();
        return this;
    }
}
