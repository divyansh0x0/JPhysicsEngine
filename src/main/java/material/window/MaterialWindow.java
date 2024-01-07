package material.window;

import material.constants.Size;
import material.containers.MaterialPanel;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.ElevationDP;
import material.utils.Log;
import material.utils.OsUtils;
import material.window.buttons.CloseButton;
import material.window.buttons.MaxRestoreButton;
import material.window.buttons.MinimizeButton;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

// `xprop -name 'Frost' -format _MOTIF_WM_HINTS 32c -set _MOTIF_WM_HINTS 2` command can be used to remove windows border in x11 linux distros
// `xprop -name 'Frost' -format _MOTIF_WM_HINTS 32c -set _MOTIF_WM_HINTS '0x2, 0x2, 0x2, 0x2,0x2,0x2'` will remove windows border but won't remove mouse resize
// ProcessBuilder processBuilder = new ProcessBuilder();
//         processBuilder.command("xprop", "-name","" + getName() + "", "-format","_MOTIF_WM_HINTS", "32c", "-set", "_MOTIF_WM_HINTS", "0x2, 0x2, 0x2, 0x2,0x2,0x2"); [Took me 1 hour to figure this shit out]
public class MaterialWindow extends JFrame {
    // private static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
    private final RootPanel _root = new RootPanel();
    private boolean isCaptionBarEnabled;
    private boolean isMaximized = false;
    private @Nullable BorderlessWindowProc windowProc;
    private static final String insetsLayoutConstraints = "nogrid, flowy, fill, gap 0,";
    private final MigLayout windowInsetsLayout = new MigLayout(insetsLayoutConstraints + "insets 1");
    private boolean isFullscreen = false;
    private Dimension nonMaximizedSize = null;
    private Point lastNonMaximizedPos = null;
    private boolean wasMaximized = false;
    private final MaterialPanel INSETS_ROOT_PANE = new MaterialPanel(windowInsetsLayout);
    private boolean isMouseOnMaximizeBtn = false;
    private Rectangle MouseDragArea = new Rectangle(0,0,0,0);
    private final AtomicBoolean isMouseOnDragArea = new AtomicBoolean(false);

    public MaterialWindow(String name, Size minimumSize, boolean addCaptionBar, boolean addWindowProcedure) {
        this.isCaptionBarEnabled = addCaptionBar;
        if (OsUtils.isCustomWindowSupported() && addWindowProcedure) {
            windowProc = new BorderlessWindowProc();
        }
        setProperties();

        // Container for root so that the insets of the whole window can be changed
        INSETS_ROOT_PANE.setElevationDP(ElevationDP._0);
        //Actual root container
        MigLayout rootLayout = new MigLayout("nogrid, flowy, fill, inset 0, gap 0");
        _root.setLayout(rootLayout);

        INSETS_ROOT_PANE.add(_root, "grow, h 0:100%:100%, w 0:100%:100%");

        this.setName(name);
        this.setTitle(name);
        this.setMinimumSize(minimumSize);
        this.setPreferredSize(minimumSize);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setBackground(ThemeColors.getInstance().getBackground());
        this.add(INSETS_ROOT_PANE);

        //CaptionBar
        if (addCaptionBar) {
            MaterialPanel captionBar = new MaterialPanel(new MigLayout("fill, insets 0 5 0 5"));
            String CAPTION_BUTTONS_CONSTRAINT = "east, growy, w " + (DecorationParameters.getTitleBarHeight()) + "!";
            captionBar.add(new CloseButton(), CAPTION_BUTTONS_CONSTRAINT);
            captionBar.add(new MaxRestoreButton(), CAPTION_BUTTONS_CONSTRAINT);
            captionBar.add(new MinimizeButton(), CAPTION_BUTTONS_CONSTRAINT);
            _root.add(captionBar, "top,growx, h " + DecorationParameters.getTitleBarHeight() + "!");
        }
        ThemeManager.getInstance().addThemeListener(this::updateTheme);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if(isCaptionBarEnabled) {
                    Rectangle rect = MouseDragArea == null ? new Rectangle(DecorationParameters.getResizeBorderThickness(), DecorationParameters.getResizeBorderThickness(), 0, 0) : MouseDragArea;
                    rect.setSize(getWidth() - DecorationParameters.getControlBoxWidth(), getHeight());
                }
            }
        });
        addWindowStateListener(e -> {
            synchronized (this) {
                if (windowProc == null) {
                    setMaximizedFlag(e.getNewState() == MAXIMIZED_BOTH);
                } else
                    setMaximizedFlag(e.getNewState() == MAXIMIZED_BOTH);
            }
        });
    }

    public MaterialWindow(String name, Size minimumSize, boolean addCaptionButtons) {
        this(name, minimumSize, addCaptionButtons, true);
    }

    public MaterialWindow(String name, Size minimumSize) {
        this(name, minimumSize, true, true);

    }

    private void updateTheme() {
        _root.setBackground(ThemeColors.getInstance().getBackground());
        if (windowProc != null) {
            this.setBackground(ThemeColors.getInstance().getBackground());
        }
    }

    /* *************************************************************************
     *                              GETTERS
     ***************************************************************************/
    public boolean isCaptionBarEnabled() {
        return isCaptionBarEnabled;
    }
    public boolean isFullscreen() {
        return isFullscreen;
    }

    public @NotNull JFrame getJFrame() {
        return this;
    }

    public RootPanel getRootPanel() {
        return _root;
    }


    public boolean isMaximized() {
        return isMaximized;
    }

    /* *************************************************************************
     *                              UTILITIES
     ***************************************************************************/
    public synchronized void close() {
        try {
            SwingUtilities.invokeLater(() -> {
                this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                this.dispose();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void minimize() {
        if (windowProc != null) {
            windowProc.minimizeWindow();
        } else {
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_ICONIFIED));
            this.setExtendedState(ICONIFIED);
        }
    }

    public synchronized void toggleMaximize() {
        if (!isMaximized) {
            nonMaximizedSize = this.getSize();
            lastNonMaximizedPos = this.getLocationOnScreen();
        }
        if (windowProc != null) {
            windowProc.toggleMaximize();
        } else {
            if (!isMaximized()) {
                setMaximizedFlag(true);
                this.setExtendedState(MAXIMIZED_BOTH);
            } else {
                setMaximizedFlag(false);
                this.setExtendedState(NORMAL);
            }
        }
    }

    public void restore() {
        this.setExtendedState(this.getExtendedState() | NORMAL);

    }
    /* **************************************************************************
     *                                SETTERS
     ****************************************************************************/
    public void setCaptionBarEnabled(boolean b){
        this.isCaptionBarEnabled= b;

    }
    private void setMaximizedFlag(boolean b) {
        synchronized (getTreeLock()) {
            this.isMaximized = b;
            if (b) {
                windowInsetsLayout.setLayoutConstraints(insetsLayoutConstraints + "insets " + DecorationParameters.getResizeBorderThickness());
            } else {
                windowInsetsLayout.setLayoutConstraints(insetsLayoutConstraints + "insets 1");
            }
        }
        INSETS_ROOT_PANE.paintImmediately(0, 0, getWidth(), getHeight());
        INSETS_ROOT_PANE.revalidate();
    }

    public void setFullscreen(boolean isFullscreen) {
        if (!this.isFullscreen && this.isShowing()) {
            if (!isMaximized) {
                nonMaximizedSize = this.getSize();
                lastNonMaximizedPos = this.getLocationOnScreen();
            }
            wasMaximized = isMaximized();
        }
        this.isFullscreen = isFullscreen;
        if (windowProc == null) {
            GraphicsDevice device = this.getGraphicsConfiguration().getDevice();
            if (device.isFullScreenSupported()) {
                if (isFullscreen) {
                    device.setFullScreenWindow(this);
                } else {
                    device.setFullScreenWindow(null);
                }
            } else
                Log.error("Full screen mode not available");
        } else {
            windowProc.setFullscreen(isFullscreen);
            //if fullscreen was exited
            if (!this.isFullscreen && nonMaximizedSize != null && lastNonMaximizedPos != null) {
                //TODO fix this, size should change if last state was not maximized. When fullscreen is exited, it will be in non full screen mode without the toggleMaximize()
                //restore window size and location
                this.setLocation(lastNonMaximizedPos);
                this.setSize(nonMaximizedSize);
                //If window was maximized before entering fullscreen, then exiting a fullscreen will not maximize the window
                //This fixes that
                if (wasMaximized)
                    toggleMaximize();
            }

        }
    }

    public void setGripSize(int size) {
        if (windowProc != null)
            windowProc.setGripSize(size);
    }

    @Override
    public void setResizable(boolean resizable) {
        super.setResizable(resizable);
        if (windowProc != null)
            windowProc.setResizable(resizable);
    }

    private void setProperties() {
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("Swing.aatext", "true");

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

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        try {
            if (windowProc != null) {
                Log.info("Preparing window procedure for material window");
                windowProc.init(this);
                windowProc.setVisible(b);
            }
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        } catch (Exception e) {
            Log.error(e.getMessage());
        }
        updateTheme();
    }


    /* **************************************************************************
     *                                NATIVES
     ****************************************************************************/
    public boolean isMouseOnClose() {
        return isMouseOnMaximizeBtn;
    }
    public void setMouseOnCloseBtn(boolean b){
        this.isMouseOnMaximizeBtn = b;
    }
    public boolean isMouseOnMinimize() {
        return isMouseOnMaximizeBtn;
    }
    public void setMouseOnMinBtn(boolean b){
        this.isMouseOnMaximizeBtn = b;
    }
    public boolean isMouseOnMaximize() {
        return isMouseOnMaximizeBtn;
    }

    public void setMouseOnMaxBtn(boolean b){
        this.isMouseOnMaximizeBtn = b;
    }

    public boolean isOnDragArea() {
        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = getX() + MouseDragArea.x;
        int y = getY() + MouseDragArea.y;
        return  p.x > x && p.x < x + MouseDragArea.width && p.y > y && p.y < y + MouseDragArea.height;
    }



    public void setMouseDragArea(Rectangle rect) {
        this.MouseDragArea = rect;
    }
}
