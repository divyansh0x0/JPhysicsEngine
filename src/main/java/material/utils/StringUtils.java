package material.utils;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class StringUtils {
    public static final String ELLIPSIS = "…";
    public static @NotNull String formatTime(@NotNull Duration d) {
        long m = d.toMillis();
        long sec = (m / 1000) % 60;
        long min = (m / 1000) / 60;
        return "%02d:%02d".formatted(min, sec);
    }

    /**
     * @return A new string in which first letter of each word is in uppercase;
     */
    public static @NotNull String toHeading(@NotNull String str) {
        str = str.toLowerCase();
        StringBuilder sb = new StringBuilder();
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];
            if (Character.isAlphabetic(c) && (i == 0 || Character.isWhitespace(arr[i - 1])))
                c = Character.toUpperCase(c);

            sb.append(c);
        }
        return sb.toString();
    }

    public static String removeFileExtension(String filename, boolean removeAllExtensions) {
        if (filename == null || filename.isEmpty()) {
            return filename;
        }

        String extPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");
        return filename.replaceAll(extPattern, "");
    }

    public static String getUTF_8String(String str){
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String getFileName(String folderPath) {
        char[] cArr = folderPath.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i = cArr.length-1; i >= 0; i--){
            if(cArr[i] == '\\')
                break;
            else
                sb.insert(0,cArr[i]);
        }
        return sb.toString();
    }


}
