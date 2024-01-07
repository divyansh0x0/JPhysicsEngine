package material.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OutputUtils {
    private static boolean isFileWritten = false;

    public static synchronized void write(String str) {
        try {
            File file = new File("output.txt");
            if (file.exists()) {
                if (file.canWrite()) {
                    BufferedWriter fr = new BufferedWriter(new FileWriter(file, true));
                    if (!isFileWritten) {
                        fr.write(str);
                        isFileWritten = true;
                    } else
                        fr.append(str).write('\n');
                    fr.close();
                }
            } else {
                if (file.createNewFile()) {
                    write(str);
                } else
                    throw new IOException("Failed to create output.txt");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
