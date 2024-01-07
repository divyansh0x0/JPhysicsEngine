package material.window.buttons;

import material.Padding;
import material.component.MaterialNavButton;
import material.listeners.MouseClickListener;

import java.awt.event.InputEvent;

public abstract class CaptionButton extends MaterialNavButton implements MouseClickListener {
    private static final Padding PADDING = new Padding(5);
    public CaptionButton() {
        super();
        setText(null);
        setPadding(PADDING);
        setIconSizeRatio(0.9);
        addLeftClickListener(this);
        setTransparentBackground(true);
    }

    @Override
    public abstract void clicked(InputEvent e);
}
