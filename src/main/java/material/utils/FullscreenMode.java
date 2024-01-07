package material.utils;

import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

public class FullscreenMode {
    private static final String[] paths = {"images/cat.jpg", "images/mountains.jpg"};
    private static final Random random = new Random();
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static final HashMap<String, BufferedImage> SAVED_IMAGES = new HashMap<>();

    //Fill map with images so we don't have to read them again every time
    static {
        for(String path : paths){
            SAVED_IMAGES.put(path,getScaledImage(path));
        }
    }
    public static @Nullable BufferedImage getRandomImage() {
        int randomIndex = random.nextInt(0, SAVED_IMAGES.size());
        String path = paths[randomIndex];
        return SAVED_IMAGES.get(path);
    }

    public static @Nullable BufferedImage getScaledImage(String path){
        BufferedImage image = null;
        URL url = FullscreenMode.class.getClassLoader().getResource(path);
        if (url != null) {
            try {
                image = ImageIO.read(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (image != null) {
            image.getGraphics().drawImage(image.getScaledInstance(SCREEN_SIZE.width, SCREEN_SIZE.height, Image.SCALE_SMOOTH),0,0,null);
        }
        return image;
    }
}
