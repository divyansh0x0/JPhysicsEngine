package material.window.buttons;

import material.window.MaterialWindow;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import javax.swing.*;
import java.awt.event.InputEvent;

public class MinimizeButton extends CaptionButton{
    private static final Ikon ICON = FluentUiRegularMZ.MINIMIZE_48;
    public MinimizeButton() {
        setIcon(ICON);
    }

    @Override
    public void clicked(InputEvent e) {
        JFrame jFrame = (JFrame) SwingUtilities.getRoot(this);
        if(jFrame instanceof MaterialWindow){
            ((MaterialWindow) jFrame).minimize();
        }
    }
}
