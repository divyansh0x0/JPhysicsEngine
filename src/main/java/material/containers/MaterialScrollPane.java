package material.containers;

import material.component.MaterialComponent;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.ElevationDP;
import material.tools.ShadowGenerator;
import material.tools.SmoothScrolling;
import material.ui.MaterialScrollbarUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MaterialScrollPane extends JScrollPane {
    private static final int SHADOW_SIZE = 30;
    private static final Color shadowColor = new Color(0x52000000, true);
    //    private static final BufferedImage shadowYUp = ShadowGenerator.generateShadow(MaterialComponent.SCREEN_SIZE.width, SHADOW_SIZE, ShadowGenerator.Direction.UP,shadowColor);
    private static final BufferedImage shadowYDown = ShadowGenerator.generateShadow(MaterialComponent.SCREEN_SIZE.width, SHADOW_SIZE, ShadowGenerator.Direction.DOWN, shadowColor);
        private static final BufferedImage shadowXRight = ShadowGenerator.generateShadow(SHADOW_SIZE, MaterialComponent.SCREEN_SIZE.height, ShadowGenerator.Direction.RIGHT,shadowColor);
    private static final boolean forceSmoothScrolling = true;
    private @Nullable ElevationDP elevationDP;
    private static final int SCROLLBAR_SIZE = 10;
    private static final int UNIT_INCREMENT = 1;
    private boolean enableScrollShadows = false;


    public MaterialScrollPane(Component view) {
        super(view);
        enableVerticalShadowOnScroll(true);
//        setLayout(new MaterialScrollPaneLayout());
        var v = getViewport();
        v.setOpaque(false);
        v.setBackground(new Color(0x0, true));
        v.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        v.setDoubleBuffered(true);

        var hbar = this.getHorizontalScrollBar();
        hbar.setOpaque(false);
        hbar.setUI(new MaterialScrollbarUI(this));
        hbar.setPreferredSize(new Dimension(hbar.getWidth(), SCROLLBAR_SIZE));
//        hbar.setBlockIncrement(UNIT_INCREMENT);
        hbar.setUnitIncrement(UNIT_INCREMENT);

        var vbar = this.getVerticalScrollBar();
        vbar.setOpaque(false);
        vbar.setUI(new MaterialScrollbarUI(this));
        vbar.setPreferredSize(new Dimension(SCROLLBAR_SIZE, vbar.getHeight()));
//        vbar.setBlockIncrement(UNIT_INCREMENT);
        vbar.setUnitIncrement(UNIT_INCREMENT);

        setBorder(BorderFactory.createEmptyBorder());

        getVerticalScrollBar().setUnitIncrement(16);

        ThemeManager.getInstance().addThemeListener(this::updateTheme);

        updateTheme();
//
//        this.getViewport().addChangeListener((changeEvent)->{
//            revalidate();
//            repaint();
//        });
        SmoothScrolling smoothScrolling = new SmoothScrolling(this);
        smoothScrolling.setEnabled(forceSmoothScrolling);
    }


    private void updateTheme() {
        if (elevationDP == null) {
            setOpaque(false);
            setBackground(ThemeColors.TransparentColor);
        } else {
            setBackground(ThemeColors.getInstance().getColorByElevation(elevationDP));
        }
        repaint();
        revalidate();
    }

    public @Nullable ElevationDP getElevationDP() {
        return elevationDP;
    }

    public MaterialScrollPane setElevationDP(ElevationDP elevationDP) {
        this.elevationDP = elevationDP;
        updateTheme();
        return this;
    }


    @Override
    public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_RESOLUTION_VARIANT, RenderingHints.VALUE_RESOLUTION_VARIANT_DPI_FIT);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        super.paint(g2d);
        if (enableScrollShadows) {
            if(verticalScrollBar.getValue() > 0) {
                int maxY = Math.min(300, verticalScrollBar.getMaximum());
                float proceduralAplhaY = Math.min(1f, (float) verticalScrollBar.getValue() / maxY);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, proceduralAplhaY / 2));
                g2d.drawImage(shadowYDown, 0, 0, null);
            }
            if(horizontalScrollBar.getValue() > 0) {
                int maxX = Math.min(300, horizontalScrollBar.getMaximum());
                float proceduralAplhaX = Math.min(1f, (float) verticalScrollBar.getValue() / maxX);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, proceduralAplhaX / 2));
                g2d.drawImage(shadowXRight, 0, 0, null);
            }
        }
        g2d.dispose();
    }

    public boolean isEnableScrollShadows() {
        return enableScrollShadows;
    }

    public void enableVerticalShadowOnScroll(boolean enableVerticalShadowOnScroll) {
        this.enableScrollShadows = enableVerticalShadowOnScroll;
        repaint();
    }

    private class MaterialScrollPaneLayout extends ScrollPaneLayout{
        @Override
        public void layoutContainer(Container parent) {
//            super.layoutContainer(/parent);
            this.viewport.setLocation(0,0);
            this.viewport.setSize(parent.getWidth(),parent.getHeight());
            this.vsb.setBounds(0,0,10,parent.getHeight());
        }
    }
}
// class Layout extends ScrollPaneLayout {
//    private static final Insets EMPTY_INSETS = JBInsets.emptyInsets();
//
//    protected Component statusComponent;
//
//    @Override
//    public void syncWithScrollPane(JScrollPane sp) {
//        super.syncWithScrollPane(sp);
//
//        if (sp instanceof JBScrollPane) {
//            statusComponent = ((JBScrollPane)sp).getStatusComponent();
//        }
//    }
//
//    @Override
//    public void addLayoutComponent(String s, Component c) {
//        if (s.equals(STATUS_COMPONENT)) {
//            statusComponent = addSingletonComponent(statusComponent, c);
//        }
//        else {
//            super.addLayoutComponent(s, c);
//        }
//    }
//
//    @Override
//    public void layoutContainer(Container parent) {
//        JScrollPane pane = (JScrollPane)parent;
//        // Calculate inner bounds of the scroll pane
//        Rectangle viewportBounds = new Rectangle(pane.getWidth(), pane.getHeight());
//        final boolean isOverlappingScrollBar = isOverlappingScrollBar(pane);
//        @NotNull Rectangle wholePaneBounds = viewportBounds.getBounds();
//        JBInsets.removeFrom(viewportBounds, pane.getInsets());
//        // Determine positions of scroll bars on the scroll pane
//        Object property = pane.getClientProperty(Flip.class);
//        Flip flip = property instanceof Flip ? (Flip)property : Flip.NONE;
//        boolean hsbOnTop = flip == Flip.BOTH || flip == Flip.VERTICAL;
//        boolean vsbOnLeft = pane.getComponentOrientation().isLeftToRight()
//                ? flip == Flip.BOTH || flip == Flip.HORIZONTAL
//                : flip == Flip.NONE || flip == Flip.VERTICAL;
//        // If there's a visible row header remove the space it needs.
//        // The row header is treated as if it were fixed width, arbitrary height.
//        Rectangle rowHeadBounds = new Rectangle(viewportBounds.x, 0, 0, 0);
//        if (rowHead != null && rowHead.isVisible()) {
//            rowHeadBounds.width = min(viewportBounds.width, rowHead.getPreferredSize().width);
//            viewportBounds.width -= rowHeadBounds.width;
//            if (vsbOnLeft) {
//                rowHeadBounds.x += viewportBounds.width;
//            }
//            else {
//                viewportBounds.x += rowHeadBounds.width;
//            }
//        }
//        // If there's a visible column header remove the space it needs.
//        // The column header is treated as if it were fixed height, arbitrary width.
//        Rectangle colHeadBounds = new Rectangle(0, viewportBounds.y, 0, 0);
//        if (colHead != null && colHead.isVisible()) {
//            colHeadBounds.height = min(viewportBounds.height, colHead.getPreferredSize().height);
//            viewportBounds.height -= colHeadBounds.height;
//            if (hsbOnTop) {
//                colHeadBounds.y += viewportBounds.height;
//            }
//            else {
//                viewportBounds.y += colHeadBounds.height;
//            }
//        }
//        // If there's a JScrollPane.viewportBorder, remove the space it occupies
//        Border border = pane.getViewportBorder();
//        Insets insets = border == null ? null : border.getBorderInsets(parent);
//        JBInsets.removeFrom(viewportBounds, insets);
//        if (insets == null) insets = EMPTY_INSETS;
//        // At this point:
//        // colHeadBounds is correct except for its width and x
//        // rowHeadBounds is correct except for its height and y
//        // bounds - the space available for the viewport and scroll bars
//        // Once we're through computing the dimensions of these three parts
//        // we can go back and set the bounds for the corners and the dimensions of
//        // colHeadBounds.x, colHeadBounds.width, rowHeadBounds.y, rowHeadBounds.height.
//
//        // Don't bother checking the Scrollable methods if there is no room for the viewport,
//        // we aren't going to show any scroll bars in this case anyway.
//        boolean isEmpty = viewportBounds.width < 0 || viewportBounds.height < 0;
//
//        Component view = viewport == null ? null : viewport.getView();
//        Dimension viewPreferredSize = view == null ? new Dimension() : view.getPreferredSize();
//        if (view instanceof JComponent && !view.isPreferredSizeSet()) {
//            JBInsets.removeFrom(viewPreferredSize, JBViewport.getViewInsets((JComponent)view));
//        }
//        Dimension viewportExtentSize = viewport == null ? new Dimension() : viewport.toViewCoordinates(viewportBounds.getSize());
//
//        // workaround for installed JBViewport.ViewBorder:
//        // do not hide scroll bars if view is not aligned
//        Point viewLocation = new Point();
//        if (view != null) viewLocation = view.getLocation(viewLocation);
//        // If there's a vertical scroll bar and we need one, allocate space for it.
//        // A vertical scroll bar is considered to be fixed width, arbitrary height.
//        boolean vsbRequiresSpace = false;
//        boolean vsbNeeded = false;
//        int vsbPolicy = pane.getVerticalScrollBarPolicy();
//        if (!isEmpty && vsbPolicy != VERTICAL_SCROLLBAR_NEVER) {
//            vsbNeeded = vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS
//                    || isVerticalScrollBarNeeded(view, viewLocation, viewPreferredSize, viewportExtentSize);
//        }
//        Rectangle vsbBounds = new Rectangle(0, viewportBounds.y - insets.top, 0, 0);
//        if (vsb != null) {
//            if (isAlwaysOpaque(view)) vsb.setOpaque(true);
//            vsbRequiresSpace = vsb.isOpaque() && !isOverlappingScrollBar;
//            if (vsbNeeded) {
//                adjustForVSB(viewportBounds, insets, vsbBounds, vsbRequiresSpace, vsbOnLeft, isOverlappingScrollBar, wholePaneBounds);
//                if (vsbRequiresSpace && viewport != null) {
//                    viewportExtentSize = viewport.toViewCoordinates(viewportBounds.getSize());
//                }
//            }
//        }
//        // If there's a horizontal scroll bar and we need one, allocate space for it.
//        // A horizontal scroll bar is considered to be fixed height, arbitrary width.
//        boolean hsbRequiresSpace = false;
//        boolean hsbNeeded = false;
//        int hsbPolicy = pane.getHorizontalScrollBarPolicy();
//        if (!isEmpty && hsbPolicy != HORIZONTAL_SCROLLBAR_NEVER) {
//            hsbNeeded = hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS
//                    || isHorizontalScrollBarNeeded(view, viewLocation, viewPreferredSize, viewportExtentSize);
//        }
//        Rectangle hsbBounds = new Rectangle(viewportBounds.x - insets.left, 0, 0, 0);
//        if (hsb != null) {
//            if (isAlwaysOpaque(view)) hsb.setOpaque(true);
//            hsbRequiresSpace = hsb.isOpaque() && !isOverlappingScrollBar;
//            if (hsbNeeded) {
//                adjustForHSB(viewportBounds, insets, hsbBounds, hsbRequiresSpace, hsbOnTop, isOverlappingScrollBar, wholePaneBounds);
//                // If we added the horizontal scrollbar and reduced the vertical space
//                // we may have to add the vertical scrollbar, if that hasn't been done so already.
//                if (vsb != null && !vsbNeeded && vsbPolicy != VERTICAL_SCROLLBAR_NEVER) {
//                    if (!hsbRequiresSpace) {
//                        viewPreferredSize.height += hsbBounds.height;
//                    }
//                    else if (viewport != null) {
//                        viewportExtentSize = viewport.toViewCoordinates(viewportBounds.getSize());
//                    }
//                    vsbNeeded = isScrollBarNeeded(viewLocation.y, viewPreferredSize.height, viewportExtentSize.height);
//                    if (vsbNeeded) adjustForVSB(viewportBounds, insets, vsbBounds, vsbRequiresSpace, vsbOnLeft, isOverlappingScrollBar, wholePaneBounds);
//                }
//            }
//        }
//        // Set the size of the viewport first, and then recheck the Scrollable methods.
//        // Some components base their return values for the Scrollable methods on the size of the viewport,
//        // so that if we don't ask after resetting the bounds we may have gotten the wrong answer.
//        if (viewport != null) {
//            viewport.setBounds(viewportBounds);
//            if (!isEmpty && view instanceof Scrollable) {
//                viewportExtentSize = viewport.toViewCoordinates(viewportBounds.getSize());
//
//                boolean vsbNeededOld = vsbNeeded;
//                if (vsb != null && vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED) {
//                    boolean vsbNeededNew = isVerticalScrollBarNeeded(view, viewLocation, viewPreferredSize, viewportExtentSize);
//                    if (vsbNeeded != vsbNeededNew) {
//                        vsbNeeded = vsbNeededNew;
//                        if (vsbNeeded) {
//                            adjustForVSB(viewportBounds, insets, vsbBounds, vsbRequiresSpace, vsbOnLeft, isOverlappingScrollBar, wholePaneBounds);
//                        }
//                        else if (vsbRequiresSpace) {
//                            viewportBounds.width += vsbBounds.width;
//                        }
//                        if (vsbRequiresSpace) viewportExtentSize = viewport.toViewCoordinates(viewportBounds.getSize());
//                    }
//                }
//                boolean hsbNeededOld = hsbNeeded;
//                if (hsb != null && hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED) {
//                    boolean hsbNeededNew = isHorizontalScrollBarNeeded(view, viewLocation, viewPreferredSize, viewportExtentSize);
//                    if (hsbNeeded != hsbNeededNew) {
//                        hsbNeeded = hsbNeededNew;
//                        if (hsbNeeded) {
//                            adjustForHSB(viewportBounds, insets, hsbBounds, hsbRequiresSpace, hsbOnTop, isOverlappingScrollBar, wholePaneBounds);
//                        }
//                        else if (hsbRequiresSpace) {
//                            viewportBounds.height += hsbBounds.height;
//                        }
//                        if (hsbRequiresSpace && vsb != null && !vsbNeeded && vsbPolicy != VERTICAL_SCROLLBAR_NEVER) {
//                            viewportExtentSize = viewport.toViewCoordinates(viewportBounds.getSize());
//                            vsbNeeded = isScrollBarNeeded(viewLocation.y, viewPreferredSize.height, viewportExtentSize.height);
//                            if (vsbNeeded) {
//                                adjustForVSB(viewportBounds, insets, vsbBounds, vsbRequiresSpace, vsbOnLeft, isOverlappingScrollBar, wholePaneBounds);
//                            }
//                        }
//                    }
//                }
//                if (hsbNeededOld != hsbNeeded || vsbNeededOld != vsbNeeded) {
//                    viewport.setBounds(viewportBounds);
//                    // You could argue that we should recheck the Scrollable methods again until they stop changing,
//                    // but they might never stop changing, so we stop here and don't do any additional checks.
//                }
//            }
//        }
//        // Set the bounds of the row header.
//        rowHeadBounds.y = viewportBounds.y - insets.top;
//        rowHeadBounds.height = viewportBounds.height + insets.top + insets.bottom;
//        boolean fillLowerCorner = false;
//        if (rowHead != null) {
//            if (hsbRequiresSpace) {
//                Component corner = hsbOnTop ? (vsbOnLeft ? upperRight : upperLeft) : (vsbOnLeft ? lowerRight : lowerLeft);
//                fillLowerCorner = corner == null && UIManager.getBoolean("ScrollPane.fillLowerCorner");
//                if (!fillLowerCorner && ScrollSettings.isHeaderOverCorner(viewport)) {
//                    if (hsbOnTop) rowHeadBounds.y -= hsbBounds.height;
//                    rowHeadBounds.height += hsbBounds.height;
//                }
//            }
//            rowHead.setBounds(rowHeadBounds);
//            rowHead.putClientProperty(Alignment.class, vsbOnLeft ? Alignment.RIGHT : Alignment.LEFT);
//        }
//        // Set the bounds of the column header.
//        colHeadBounds.x = viewportBounds.x - insets.left;
//        colHeadBounds.width = viewportBounds.width + insets.left + insets.right;
//        boolean fillUpperCorner = false;
//        boolean hasStatusComponent = statusComponent != null && statusComponent.isShowing();
//        if (colHead != null) {
//            if (vsbRequiresSpace) {
//                Component corner = vsbOnLeft ? (hsbOnTop ? lowerLeft : upperLeft) : (hsbOnTop ? lowerRight : upperRight);
//                fillUpperCorner = corner == null && UIManager.getBoolean("ScrollPane.fillUpperCorner") && !hasStatusComponent;
//                if (!fillUpperCorner && ScrollSettings.isHeaderOverCorner(viewport)) {
//                    if (vsbOnLeft) colHeadBounds.x -= vsbBounds.width;
//                    colHeadBounds.width += vsbBounds.width;
//                }
//            }
//            colHead.setBounds(colHeadBounds);
//            colHead.putClientProperty(Alignment.class, hsbOnTop ? Alignment.BOTTOM : Alignment.TOP);
//        }
//        // Calculate overlaps for translucent scroll bars
//        int overlapWidth = 0;
//        int overlapHeight = 0;
//        if (vsbNeeded && !vsbRequiresSpace && hsbNeeded && !hsbRequiresSpace) {
//            overlapWidth = vsbBounds.width; // shrink horizontally
//            //overlapHeight = hsbBounds.height; // shrink vertically
//        }
//        // Set the bounds of the vertical scroll bar.
//        vsbBounds.y = viewportBounds.y - insets.top;
//        vsbBounds.height = viewportBounds.height + insets.top + insets.bottom;
//
//        // Forked bounds that are actually used for setting vertical scroll bar bounds
//        // after possible modification with statusComponent bounds.
//        Rectangle actualVsbBounds = new Rectangle(vsbBounds);
//        if (vsb != null) {
//            vsb.setVisible(vsbNeeded);
//            if (vsbNeeded) {
//                if (fillUpperCorner) {
//                    // This is used primarily for GTK L&F, which needs to extend
//                    // the vertical scrollbar to fill the upper corner near the column header.
//                    // Note that we skip this step (and use the default behavior)
//                    // if the user has set a custom corner component.
//                    if (!hsbOnTop) vsbBounds.y -= colHeadBounds.height;
//                    vsbBounds.height += colHeadBounds.height;
//                }
//                int overlapY = !hsbOnTop ? 0 : overlapHeight;
//                actualVsbBounds.y += overlapY;
//                actualVsbBounds.height -= overlapHeight;
//                vsb.putClientProperty(Alignment.class, vsbOnLeft ? Alignment.LEFT : Alignment.RIGHT);
//            }
//            // Modify the bounds of the translucent scroll bar.
//            if (!vsbRequiresSpace) {
//                if (!vsbOnLeft) vsbBounds.x += vsbBounds.width;
//                vsbBounds.width = 0;
//            }
//        }
//        // Set the bounds of the horizontal scroll bar.
//        hsbBounds.x = viewportBounds.x - insets.left;
//        hsbBounds.width = viewportBounds.width + insets.left + insets.right;
//        if (hsb != null) {
//            hsb.setVisible(hsbNeeded);
//            if (hsbNeeded) {
//                if (fillLowerCorner) {
//                    // This is used primarily for GTK L&F, which needs to extend
//                    // the horizontal scrollbar to fill the lower corner near the row header.
//                    // Note that we skip this step (and use the default behavior)
//                    // if the user has set a custom corner component.
//                    if (!vsbOnLeft) hsbBounds.x -= rowHeadBounds.width;
//                    hsbBounds.width += rowHeadBounds.width;
//                }
//                int overlapX = !vsbOnLeft ? 0 : overlapWidth;
//                hsb.setBounds(hsbBounds.x + overlapX, hsbBounds.y, hsbBounds.width - overlapWidth, hsbBounds.height);
//                hsb.putClientProperty(Alignment.class, hsbOnTop ? Alignment.TOP : Alignment.BOTTOM);
//            }
//            // Modify the bounds of the translucent scroll bar.
//            if (!hsbRequiresSpace) {
//                if (!hsbOnTop) hsbBounds.y += hsbBounds.height;
//                hsbBounds.height = 0;
//            }
//        }
//
//        if (hasStatusComponent) {
//            Dimension scSize = statusComponent.getPreferredSize();
//
//            switch (flip) {
//                case NONE -> {
//                    statusComponent.setBounds(actualVsbBounds.x + actualVsbBounds.width - scSize.width, actualVsbBounds.y, scSize.width,
//                            scSize.height);
//                    actualVsbBounds.y += scSize.height;
//                }
//                case HORIZONTAL -> {
//                    statusComponent.setBounds(actualVsbBounds.x, actualVsbBounds.y, scSize.width, scSize.height);
//                    actualVsbBounds.y += scSize.height;
//                }
//                case VERTICAL -> statusComponent.setBounds(actualVsbBounds.x + actualVsbBounds.width - scSize.width,
//                        actualVsbBounds.y + actualVsbBounds.height - scSize.height, scSize.width,
//                        scSize.height);
//                case BOTH -> statusComponent.setBounds(actualVsbBounds.x,
//                        actualVsbBounds.y + actualVsbBounds.height - scSize.height, scSize.width, scSize.height);
//            }
//
//            actualVsbBounds.height -= scSize.height;
//        }
//
//        if (vsb != null && vsbNeeded) {
//            vsb.setBounds(actualVsbBounds);
//        }
//
//        // Set the bounds of the corners.
//        Rectangle left = vsbOnLeft ? vsbBounds : rowHeadBounds;
//        Rectangle right = vsbOnLeft ? rowHeadBounds : vsbBounds;
//        Rectangle upper = hsbOnTop ? hsbBounds : colHeadBounds;
//        Rectangle lower = hsbOnTop ? colHeadBounds : hsbBounds;
//        if (lowerLeft != null) {
//            Rectangle lowerLeftBounds = new Rectangle(left.x, left.y + left.height, 0, 0);
//            if (left.width > 0 && lower.height > 0) updateCornerBounds(lowerLeftBounds, lower.x, lower.y + lower.height);
//            lowerLeft.setBounds(lowerLeftBounds);
//        }
//        if (lowerRight != null) {
//            Rectangle lowerRightBounds = new Rectangle(lower.x + lower.width, right.y + right.height, 0, 0);
//            if (right.width > 0 && lower.height > 0) updateCornerBounds(lowerRightBounds, right.x + right.width, lower.y + lower.height);
//            lowerRight.setBounds(lowerRightBounds);
//        }
//        if (upperLeft != null) {
//            Rectangle upperLeftBounds = new Rectangle(left.x, upper.y, 0, 0);
//            if (left.width > 0 && upper.height > 0) updateCornerBounds(upperLeftBounds, upper.x, left.y);
//            upperLeft.setBounds(upperLeftBounds);
//        }
//        if (upperRight != null) {
//            Rectangle upperRightBounds = new Rectangle(upper.x + upper.width, upper.y, 0, 0);
//            if (right.width > 0 && upper.height > 0) updateCornerBounds(upperRightBounds, right.x + right.width, right.y);
//            upperRight.setBounds(upperRightBounds);
//        }
//        if (!vsbRequiresSpace && vsbNeeded || !hsbRequiresSpace && hsbNeeded) {
//            fixComponentZOrder(vsb, 0);
//            fixComponentZOrder(viewport, -1);
//        }
//        else if (hasStatusComponent) {
//            fixComponentZOrder(statusComponent, 0);
//            fixComponentZOrder(viewport, -1);
//        }
//    }
//
//    private static boolean tracksViewportWidth(Component view) {
//        return view instanceof Scrollable && ((Scrollable)view).getScrollableTracksViewportWidth();
//    }
//
//    private static boolean tracksViewportHeight(Component view) {
//        return view instanceof Scrollable && ((Scrollable)view).getScrollableTracksViewportHeight();
//    }
//
//    @Override
//    public Dimension preferredLayoutSize(Container parent) {
//        Dimension result = new Dimension();
//
//        JScrollPane pane = (JScrollPane)parent;
//        JBInsets.addTo(result, pane.getInsets());
//
//        Border border = pane.getViewportBorder();
//        if (border != null) JBInsets.addTo(result, border.getBorderInsets(parent));
//
//        int vsbPolicy = pane.getVerticalScrollBarPolicy();
//        int hsbPolicy = pane.getHorizontalScrollBarPolicy();
//        if (viewport != null) {
//            Component view = viewport.getView();
//            if (view != null) {
//                Point viewLocation = view.getLocation();
//                Dimension viewportExtentSize = viewport.getPreferredSize();
//                if (viewportExtentSize == null) viewportExtentSize = new Dimension();
//                Dimension viewPreferredSize = view.getPreferredSize();
//                if (viewPreferredSize == null) viewPreferredSize = new Dimension();
//                if (view instanceof JComponent && !view.isPreferredSizeSet()) {
//                    JBInsets.removeFrom(viewPreferredSize, JBViewport.getViewInsets((JComponent)view));
//                }
//                result.width += viewportExtentSize.width;
//                result.height += viewportExtentSize.height;
//                if (vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED) {
//                    if (viewportExtentSize.height < viewPreferredSize.height &&
//                            isVerticalScrollBarNeeded(view, viewLocation, viewPreferredSize, viewportExtentSize)) {
//                        vsbPolicy = VERTICAL_SCROLLBAR_ALWAYS;
//                    }
//                }
//                if (hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED) {
//                    if (viewportExtentSize.width < viewPreferredSize.width &&
//                            isHorizontalScrollBarNeeded(view, viewLocation, viewPreferredSize, viewportExtentSize)) {
//                        hsbPolicy = HORIZONTAL_SCROLLBAR_ALWAYS;
//                    }
//                }
//            }
//        }
//
//        boolean isOverlappingScrollBar = isOverlappingScrollBar(pane);
//
//        // disabled scroll bars should be minimized (see #adjustForVSB and #adjustForHSB)
//        if (vsb != null && vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS && vsb.isEnabled() && ! isOverlappingScrollBar) result.width += vsb.getPreferredSize().width;
//        if (hsb != null && hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS && hsb.isEnabled() && !isOverlappingScrollBar) result.height += hsb.getPreferredSize().height;
//
//        if (rowHead != null && rowHead.isVisible()) result.width += rowHead.getPreferredSize().width;
//        if (colHead != null && colHead.isVisible()) result.height += colHead.getPreferredSize().height;
//
//        return result;
//    }
//
//    private static boolean isOverlappingScrollBar(JScrollPane scrollPane) {
//        return (scrollPane instanceof JBScrollPane) && ((JBScrollPane)scrollPane).isOverlappingScrollBar();
//    }
//
//    private static boolean isAlwaysOpaque(Component view) {
//        return !SystemInfo.isMac && ScrollSettings.isNotSupportedYet(view);
//    }
//
//    private static void updateCornerBounds(Rectangle bounds, int x, int y) {
//        bounds.width = Math.abs(bounds.x - x);
//        bounds.height = Math.abs(bounds.y - y);
//        bounds.x = Math.min(bounds.x, x);
//        bounds.y = Math.min(bounds.y, y);
//    }
//
//    private static void fixComponentZOrder(Component component, int index) {
//        if (component != null) {
//            Container parent = component.getParent();
//            synchronized (parent.getTreeLock()) {
//                if (index < 0) index += parent.getComponentCount();
//                parent.setComponentZOrder(component, index);
//            }
//        }
//    }
//
//    private void adjustForVSB(Rectangle bounds,
//                              Insets insets,
//                              Rectangle vsbBounds,
//                              boolean vsbRequiresSpace,
//                              boolean vsbOnLeft,
//                              boolean vsbOverlapping,
//                              @NotNull Rectangle wholePaneBounds) {
//        vsbBounds.width = vsb.isEnabled() ? min(bounds.width, vsb.getPreferredSize().width) : 0;
//        if (vsbOnLeft) {
//            if (vsbOverlapping) {
//                vsbBounds.x = 0;
//            }
//            else {
//                vsbBounds.x = bounds.x - insets.left/* + vsbBounds.width*/;
//            }
//            if (vsbRequiresSpace) bounds.x += vsbBounds.width;
//        }
//        else if (vsbOverlapping) {
//            vsbBounds.x = wholePaneBounds.x + wholePaneBounds.width - vsbBounds.width;
//        }
//        else {
//            vsbBounds.x = bounds.x + bounds.width + insets.right - vsbBounds.width;
//        }
//        if (vsbRequiresSpace) bounds.width -= vsbBounds.width;
//    }
//
//    private void adjustForHSB(Rectangle bounds,
//                              Insets insets,
//                              Rectangle hsbBounds,
//                              boolean hsbRequiresSpace,
//                              boolean hsbOnTop,
//                              boolean hsbOverlapping,
//                              @NotNull Rectangle wholePaneBounds) {
//        hsbBounds.height = hsb.isEnabled() ? min(bounds.height, hsb.getPreferredSize().height) : 0;
//        if (hsbOnTop) {
//            if (hsbOverlapping) {
//                hsbBounds.y = 0;
//            }
//            else {
//                hsbBounds.y = bounds.y - insets.top/* + hsbBounds.height*/;
//            }
//            if (hsbRequiresSpace) bounds.y += hsbBounds.height;
//        }
//        else if (hsbOverlapping) {
//            hsbBounds.y = wholePaneBounds.y + wholePaneBounds.height - hsbBounds.height;
//        }
//        else {
//            hsbBounds.y = bounds.y + bounds.height + insets.bottom - hsbBounds.height;
//        }
//        if (hsbRequiresSpace) bounds.height -= hsbBounds.height;
//    }
//
//    private static int min(int one, int two) {
//        return Math.max(0, Math.min(one, two));
//    }
//
//    /**
//     * @param location      a horizontal (or vertical) position of a component
//     * @param preferredSize a preferred width (or height) of a component
//     * @param extentSize    an extent size of a viewport
//     * @return {@code true} if a preferred size exceeds an extent size or if a component is not aligned
//     */
//    private static boolean isScrollBarNeeded(int location, int preferredSize, int extentSize) {
//        return preferredSize > extentSize || location != 0;
//    }
//
//    private static boolean isHorizontalScrollBarNeeded(Component view, Point location, Dimension preferredSize, Dimension extentSize) {
//        // don't bother Scrollable.getScrollableTracksViewportWidth if a horizontal scroll bar is not needed
//        return isScrollBarNeeded(location.x, preferredSize.width, extentSize.width) && !tracksViewportWidth(view);
//    }
//
//    private static boolean isVerticalScrollBarNeeded(Component view, Point location, Dimension preferredSize, Dimension extentSize) {
//        // don't bother Scrollable.getScrollableTracksViewportHeight if a vertical scroll bar is not needed
//        return isScrollBarNeeded(location.y, preferredSize.height, extentSize.height) && !tracksViewportHeight(view);
//    }
//}

