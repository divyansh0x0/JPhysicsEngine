package material.window.buttons;

import material.listeners.MouseClickListener;
import material.utils.Log;
import material.window.MaterialWindow;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignW;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CloseButton extends CaptionButton implements MouseClickListener {
    private static final Ikon ICON = MaterialDesignW.WINDOW_CLOSE;
    public CloseButton() {
        setIcon(ICON);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
            }
        });
    }

    @Override
    public void clicked(InputEvent e) {
        Component c =SwingUtilities.getRoot(this);
        if(c instanceof MaterialWindow){
            synchronized (c) {
                ((MaterialWindow) c).close();
            }
        }
        else if(c instanceof JDialog){
            synchronized (c) {
                ((JDialog) c).dispose();
            }
        }

    }

}
