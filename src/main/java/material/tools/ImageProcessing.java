package material.tools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ImageProcessing {
    public static BufferedImage getNoise(Color bg, Color fg,int width, int height){
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bi.setRGB(x,y,chooseOneColorRandomly(bg,fg).getRGB());
            }
        }
        return bi;
    }

    private static Color chooseOneColorRandomly(Color... c){
        Random random = new Random();
        return c[random.nextInt(0,c.length)];
    }

}
