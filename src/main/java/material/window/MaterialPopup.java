package material.window;

import material.constants.Size;
import material.fonts.MaterialFonts;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.ElevationDP;
import net.miginfocom.swing.MigLayout;
import material.utils.Log;
import material.utils.OsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

public class MaterialPopup extends JFrame {
    public static final Size MAX_SIZE = new Size(1920, 1920 / 12 * 9);
    //    private static CustomWindow instance;
//    private final MaterialPanel container = new MaterialPanel().setElevationDP(null);
    private PopupWindowProc windowProc;

    public MaterialPopup() {
        super();

        if (OsUtils.isCustomWindowSupported())
            windowProc = new PopupWindowProc();
        else
            setUndecorated(true);

        setBackground(ThemeColors.getInstance().getColorByElevation(ElevationDP._24));
//        setFont(MaterialFonts.getInstance().getDefaultFont());

        this.setMaximumSize(MAX_SIZE);
        this.setFont(Font.getFont(Font.SERIF));
        this.getContentPane().setLayout(new MigLayout());
        this.setType(Type.UTILITY);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        ThemeManager.getInstance().addThemeListener(this::updateTheme);
        updateTheme();
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                SwingUtilities.invokeLater(()->setVisible(false));
                Log.info("Hiding Popup");
            }
        });
    }

    @Override
    public void setMinimumSize(Dimension minimumSize) {
        super.setMinimumSize(minimumSize);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (windowProc != null) {
            windowProc.init(this);
            windowProc.setVisible(b);
        }
    }

    public synchronized void close() {
        this.dispose();
    }

    public void setFont(Font f) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void updateTheme() {
        Color bg = ThemeColors.getInstance().getColorByElevation(ElevationDP._24);
        this.getRootPane().setBackground(bg);
        this.getContentPane().setBackground(bg);
        this.setBackground(bg);
    }

    public void show(Point location) {
        SwingUtilities.invokeLater(()->{
            this.setLocation(location);
            this.pack();
            this.setVisible(true);
        });
    }
}