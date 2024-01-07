package material.component;

import material.theme.ThemeColors;
import org.kordamp.ikonli.Ikon;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MaterialMenuItem extends MaterialIconButton{
    public static final int HEIGHT = 48;
    public static final String CONSTRAINTS = "gapY 0,growX, h " + HEIGHT + "!";
    private Color oldBg;
    public MaterialMenuItem(Ikon icon, String text) {
        super(icon, text);
        setTransparentBackground(true);
        setIconSizeRatio(0.7);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                handleMouseEnter();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                handleMouseExit();
            }
        });
    }


    public MaterialMenuItem(String str) {
        this(null, str);
    }
    private void handleMouseEnter(){
        if(oldBg == null)
            oldBg = getBackground();
        setBackground(ThemeColors.getInstance().getIconButtonColors().getBackgroundMouseOverColor());
    }

    private void handleMouseExit(){
        resetColor();
    }

    public void resetColor(){
        if(oldBg != null)
            setBackground(oldBg);
    }

}
