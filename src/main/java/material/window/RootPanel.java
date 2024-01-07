package material.window;

import java.awt.*;

import material.containers.MaterialPanel;
import material.theme.ThemeColors;
import net.miginfocom.swing.MigLayout;

public class RootPanel extends MaterialPanel {

    public RootPanel() {
        super(new MigLayout("nogrid, flowy, fill, inset 0, gap 0"));
    }

    @Override
    public void updateTheme() {
        if (getElevationDP() != null) {
            Color bgColor = ThemeColors.getInstance().getColorByElevation(getElevationDP());
            setOpaque(true);
            setBackground(bgColor);
        } else {
            setOpaque(false);
            setBackground(ThemeColors.TransparentColor);
        }
        repaint();
    }
}
