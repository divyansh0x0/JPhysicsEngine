package material.window.buttons;

import material.utils.OsUtils;
import material.window.MaterialWindow;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MaxRestoreButton extends CaptionButton {
    private static final Ikon maxICON = FluentUiRegularMZ.MAXIMIZE_48;
    private static final Ikon restoreICON = FluentUiRegularMZ.RESTORE_16;
    private MaterialWindow window;
   public MaxRestoreButton() {

        super();
        setIcon(maxICON);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                window.setMouseOnMaxBtn(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                window.setMouseOnMaxBtn(false);
            }
        });
    }

    public void changeIcon() {
        if (window.isVisible()) {
            setIcon(!window.isMaximized() ? maxICON : restoreICON);
        }
    }

    @Override
    public void clicked(InputEvent e) {
        if(window != null)
            window.toggleMaximize();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        JFrame jFrame = (JFrame) SwingUtilities.getRoot(this);
        if (jFrame instanceof MaterialWindow) {
            window = (MaterialWindow) jFrame;
        }
        if(window != null) {
            changeIcon();
            window.addWindowStateListener(l -> changeIcon());
        }
    }
}
