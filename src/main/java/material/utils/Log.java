package material.utils;

public class Log {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    public static void info(Object msg) {
        System.out.println(((int)(System.nanoTime() * 1e-9)/3600) + "H | " + ANSI_BLUE + "[INFO] " + msg + ANSI_RESET);
    }

    public static void msg(Object msg) {
        System.out.println(ANSI_PURPLE + "[MESSAGE] " + msg + ANSI_RESET);
    }

    public static void error(Object msg) {
        System.err.println(ANSI_RED + "[ERROR] " + msg + ANSI_RESET);
        if (msg instanceof Exception) {
            ((Exception) msg).printStackTrace();
        }
    }

    public static void success(Object msg) {
        System.out.println(ANSI_GREEN + "[SUCCESS] " + msg + ANSI_RESET);
    }

    public static void formattedSuccess(String format, Object... args) {
        System.out.println(ANSI_BLUE + String.format("[SUCCESS] " + format, args) + ANSI_RESET);
    }

    public static void warn(Object msg) {
        System.out.println(ANSI_YELLOW + "[WARNING]" + msg + ANSI_RESET);
    }

    public static void formattedWarn(String format, Object... args) {
        System.out.println(ANSI_YELLOW + String.format("[SUCCESS] " + format, args) + ANSI_RESET);
    }

    public static void frames(int frames) {
        System.out.println(ANSI_CYAN + "[FRAMES]" + frames + ANSI_RESET);
    }

    public static void cache(String msg) {
        System.out.println(ANSI_PURPLE + "[CACHE]" + msg + ANSI_RESET);
    }
}
