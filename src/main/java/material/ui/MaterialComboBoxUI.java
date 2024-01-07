package material.ui;
import material.theme.ThemeColors;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class MaterialComboBoxUI extends BasicComboBoxUI {

    private Color accentColor = new Color(33, 150, 243);
    private Color backgroundColor = ThemeColors.getInstance().getBackground();
    private Color textColor = ThemeColors.getInstance().getTextPrimary();
    private Color arrowColor = ThemeColors.getInstance().getAccent();

    private boolean isFocused = false;

    public static MaterialComboBoxUI createUI(JComponent c) {
        return new MaterialComboBoxUI();
    }

    @Override
    protected JButton createArrowButton() {
        JButton button = new JButton();
        button.setOpaque(false);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setIcon(new ArrowIcon());
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(new ArrowIcon(accentColor));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(new ArrowIcon());
            }
        });
        return button;
    }

    @Override
    protected ComboPopup createPopup() {
        return new BasicComboPopup(comboBox) {
            @Override
            protected JScrollPane createScroller() {
                JScrollPane scroller = new JScrollPane(list);
                scroller.getVerticalScrollBar().setUI(new MaterialScrollbarUI(null));
                return scroller;
            }
        };
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        comboBox.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                comboBox.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                comboBox.repaint();
            }
        });
    }

    @Override
    protected DefaultListCellRenderer createRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setForeground(textColor);
                c.setBackground(backgroundColor);
                return c;
            }
        };
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        if (hasFocus || isFocused) {
            g.setColor(accentColor);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        } else {
            g.setColor(backgroundColor);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        if (comboBox.isEditable()) {
            g.setColor(textColor);
            Component c = comboBox.getEditor().getEditorComponent();
            if (c instanceof JTextField textField) {
                String text = textField.getText();
                if (text != null) {
                    FontMetrics fm = g.getFontMetrics();
                    int textWidth = SwingUtilities.computeStringWidth(fm, text);
                    int textX = bounds.x + 5;
                    int textY = bounds.y + fm.getAscent() + (bounds.height - fm.getHeight()) / 2;
                    g.drawString(text, textX, textY);
                    int caretX = textX + textWidth;
                    int caretY = bounds.y + (bounds.height - fm.getHeight()) / 2;
                    g.drawLine(caretX, caretY, caretX, caretY + fm.getHeight() - 1);
                }
            }
        } else {
            super.paintCurrentValue(g, bounds, hasFocus);
        }
    }

    private class ArrowIcon implements Icon {
        private int width = 10;
        private int height = 6;
        private Color color;

        public ArrowIcon() {
            this.color = arrowColor;
        }

        public ArrowIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(color);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int[] xs = {x, x + width / 2, x + width};
            int[] ys = {y + height, y, y + height};
            g2.fillPolygon(xs, ys, 3);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
