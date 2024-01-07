package material.component;

import material.Padding;
import material.theme.PaddingModel;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.ElevationDP;
import material.theme.models.ElevationDPModel;
import material.window.MaterialPopup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fluentui.FluentUiRegularAL;
import org.kordamp.ikonli.swing.FontIcon;
import material.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class MaterialComboBox extends MaterialComponent implements ElevationDPModel, PaddingModel {
    private static final Ikon ARROW_DOWN = FluentUiRegularAL.CARET_DOWN_24;
    private static final Ikon ARROW_UP = FluentUiRegularAL.CARET_UP_24;
    private final FontIcon fontIcon = new FontIcon();
    private final ArrayList<ComboSelectionChanged> selectionListeners = new ArrayList<>();
    private static final Dimension BUTTON_DIMENSIONS = new Dimension(100, 30);
    private Padding padding = new Padding(5);

    private ElevationDP elevationDP = ElevationDP._4;

    private final String[] items;
    private final MaterialMenuItem[] buttons;
    private int selectedItemIndex = -1;

    private MaterialPopup popup = new MaterialPopup();
    private final Object lock = new Object();

    public MaterialComboBox(String @NotNull ... items) {
        this.items = items;
        buttons = new MaterialMenuItem[items.length];
        for (int i = 0; i < buttons.length; i++) {
            MaterialMenuItem materialButton = new MaterialMenuItem(items[i]);
            materialButton.addLeftClickListener(e -> updateSelected(materialButton));
            popup.add(materialButton);
            buttons[i] = materialButton;
        }

        // Add a mouse listener to handle dropdown visibility
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e))
                    togglePopup();
            }
        });
        ThemeManager.getInstance().addThemeListener(this::updateTheme);
    }


    @Override
    public void updateTheme() {
        if(elevationDP != null) {
            setBackground(ThemeColors.getInstance().getColorByElevation(elevationDP));
        }else{
            setBackground(ThemeColors.getInstance().getBackground());
        }
        setForeground(ThemeColors.getInstance().getTextPrimary());
    }

    private void updateSelected(MaterialMenuItem materialButton) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == materialButton) {
                selectedItemIndex = i;
                break;
            }
        }
        Log.info("Selected index: " + selectedItemIndex);
        togglePopup();
        repaint();
        handleSelectionChanged();
    }

    private void updateButtonColors(MaterialButton materialButton) {

    }

    private void togglePopup() {
        synchronized (lock) {
            if (!popup.isVisible()) {
                popup.show(this.getLocationOnScreen());
                if(buttons != null && buttons.length > 0)
                    buttons[getSelectedItemIndex()].resetColor();
            } else {
                popup.setVisible(false);
            }
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw the combo box background
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw the border
        g2d.setColor(getBackground().brighter());
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // Draw the selected item
        Shape defaultClip = g2d.getClip();
        g2d.setClip(padding.getLeft(),padding.getTop(),getWidth() - getPadding().getLeft(), getHeight() - getPadding().getBottom());

        String text = getSelectedString();
        Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text,g2d);
        int tX =  padding.getLeft();
        int tY = (int) Math.max(padding.getTop(), (getHeight() - bounds.getHeight())/2 + g2d.getFontMetrics().getAscent());
        g2d.setColor(getForeground());
        g2d.drawString(text, tX, tY);

        g2d.setClip(defaultClip);
        // Draw the dropdown arrow
        if(popup.isVisible()){
            fontIcon.setIkon(ARROW_UP);
            fontIcon.setIconColor(ThemeColors.getInstance().getAccent());
        }
        else{
            fontIcon.setIkon(ARROW_DOWN);
            fontIcon.setIconColor(getForeground());
        }
        int iconHeight = (int) (getHeight() - padding.getVertical() * 0.9f);
        fontIcon.setIconSize(iconHeight);
        int iX = getWidth() - fontIcon.getIconWidth()- padding.getRight();
        int iY = (getHeight() - fontIcon.getIconHeight())/2;
        g2d.drawImage(fontIcon.toImageIcon().getImage(), iX,iY,null);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        Dimension newSize = new Dimension(width, BUTTON_DIMENSIONS.height);
        synchronized (getTreeLock()) {
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setPreferredSize(newSize);
            }
        }
    }

    public String getSelectedString() {
        if (selectedItemIndex >= 0 && selectedItemIndex < items.length) {
            return items[selectedItemIndex];
        }
        return "";
    }


    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public void setSelectedItemIndex(int selectedItemIndex) {
        this.selectedItemIndex = selectedItemIndex;
        repaint();
    }

    private void handleSelectionChanged(){
        synchronized (lock){
            for(ComboSelectionChanged listener : selectionListeners)
                listener.selectionChanged(buttons[getSelectedItemIndex()], getSelectedString());
        }
    }

    public void addSelectionListener(ComboSelectionChanged selectionListener){
        selectionListeners.add(selectionListener);
    }
    public ArrayList<ComboSelectionChanged> getSelectionListeners() {
        return selectionListeners;
    }

    @Override
    public @Nullable ElevationDP getElevationDP() {
        return elevationDP;
    }

    @Override
    public MaterialComboBox setElevationDP(@Nullable ElevationDP elevationDP) {
        this.elevationDP = elevationDP;
        repaint();
        return this;
    }

    @Override
    public Padding getPadding() {
        return padding;
    }

    @Override
    public MaterialComboBox setPadding(Padding padding) {
        this.padding = padding;
        repaint();
        return this;
    }

    public interface  ComboSelectionChanged{
       void selectionChanged(MaterialMenuItem item, String name);
    }

}
