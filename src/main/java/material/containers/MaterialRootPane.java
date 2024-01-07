package material.containers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class MaterialRootPane extends MaterialPanel implements ComponentListener {
    private RootLayoutFilter rootLayoutFilter = RootLayoutFilter.DARKEN;
    private final JPanel glasspane;
    public MaterialRootPane() {
//        setLayout(new MigLayout("fill"));
        glasspane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (rootLayoutFilter != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    switch (rootLayoutFilter) {
                        case DARKEN -> {
                            Color bg = new Color(0x50000000, true);
                            g2d.setColor(bg);
                            g2d.fillRect(0, 0, getWidth(), getHeight());
                        }
                    }
                    g2d.dispose();
                }
            }
        };
        glasspane.setOpaque(false);
        addComponentListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
//        if(rootLayoutFilter != null){
//            Graphics2D g2d = (Graphics2D) g.create();
//            switch (rootLayoutFilter){
//                case DARKEN ->{
//                    Color bg = new Color(0x80000000,true);
//                    g2d.setColor(bg);
//                    g2d.fillRect(0,0,getWidth(),getHeight());
//                }
//            }
//            g2d.dispose();
//        }
    }

    public RootLayoutFilter getRootLayoutFilter() {
        return rootLayoutFilter;
    }

    public MaterialRootPane setRootLayoutFilter(RootLayoutFilter rootLayoutFilter) {
        this.rootLayoutFilter = rootLayoutFilter;
        return this;
    }

    @Override
    public void addNotify() {
        getRootPane().setGlassPane(glasspane);
        glasspane.setVisible(false);
        glasspane.repaint();
        glasspane.setBounds(0,0,getWidth(),getHeight());
        super.addNotify();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        glasspane.setBounds(0,0,getWidth(),getHeight());
        if(glasspane.isVisible())
            glasspane.repaint();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
