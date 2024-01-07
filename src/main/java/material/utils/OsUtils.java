package material.utils;

import material.utils.enums.OsType;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Locale;

public class OsUtils {
    private static final String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
    private static final String version = System.getProperty("os.version");
    private static final boolean isWindows = (os.contains("win"));
    private static final boolean isMac = (os.contains("mac"));
    private static final boolean isUnix = (os.contains("nix") || os.contains("nux") || os.contains("aix"));
    private static final boolean WINDOWS = os.startsWith("windows");
    private static final boolean WINDOWS_VISTA_OR_LATER = WINDOWS && versionNumberGreaterThanOrEqualTo(6.0f);
    private static final boolean WINDOWS_7_OR_LATER = WINDOWS && versionNumberGreaterThanOrEqualTo(6.1f);
    private static final boolean CUSTOM_WINDOW_SUPPORTED = OsUtils.isWindowsVistaOrLater();
    private static final Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

    static {
        Log.warn("OS: " + os + "| Version: " + version);
    }

    public static OsType getOsType() {
        if (isWindows)
            return OsType.WINDOWS;
        else if (isMac)
            return OsType.MAC;
        else if (isUnix)
            return OsType.LINUX;
        else
            return OsType.UNKNOWN;
    }

    public static boolean isWindowsVistaOrLater() {
        return WINDOWS_VISTA_OR_LATER;
    }

    public static boolean isWindows7OrLater() {
        return WINDOWS_7_OR_LATER;
    }

    public static boolean isCustomWindowSupported() {
        return CUSTOM_WINDOW_SUPPORTED;
    }

    public static int getRefreshRate() {
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        if (displayMode.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN)
            return 60;
        else
            return Math.max(displayMode.getRefreshRate(),120);
    }

    private static boolean versionNumberGreaterThanOrEqualTo(float value) {
//        try {
        boolean b = Float.parseFloat(version) >= value;
        ;
        return b;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//            return false;
//        }
    }

    public static boolean isUnix() {
        return isUnix;
    }

    public static @NotNull Dimension getScreenSize() {
        return screenRect.getSize();
    }

    public static Rectangle getScreenRect() {
        return screenRect;
    }

    public static String getOsName() {
        return os;
    }
}
