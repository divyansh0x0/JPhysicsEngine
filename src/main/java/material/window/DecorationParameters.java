package material.window;

public class DecorationParameters {
    private static final int titleBarHeight = 27;
    private static final int controlBoxWidth = 27 * 3;
    private static final int iconWidth = 27;
    private static final int maximizedWindowFrameThickness = 27;
    private static final int resizeBorderThickness = 8;
    private static final int borderThickness =1;

    public static int getMaximizedWindowFrameThickness() {
        return maximizedWindowFrameThickness;
    }

    public static int getResizeBorderThickness() {
        return resizeBorderThickness;
    }

    public static int getTitleBarHeight() {
        return titleBarHeight;
    }

    public static int getControlBoxWidth() {
        return controlBoxWidth;
    }

    public static int getIconWidth() {
        return iconWidth;
    }
}
