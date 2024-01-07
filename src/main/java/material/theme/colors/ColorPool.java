package material.theme.colors;

import java.awt.*;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ColorPool {
    private static ColorPool instance;
    private static final int INITIAL_POOL_SIZE = 50;
    private Deque<Color> colorPool;
    private Map<Integer, Color> colorCache;

    public ColorPool() {
        colorPool = createColorPool();
        colorCache = new HashMap<>();
    }

    private Deque<Color> createColorPool() {
        Deque<Color> pool = new LinkedList<>();
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(new Color(0, 0, 0));
        }
        return pool;
    }
    public Color acquireColor(int red, int green, int blue) {
        int rgb = (red << 16) | (green << 8) | blue;
        return acquireColor(red,green,blue,255);
    }

    public Color acquireColor(int argb, boolean isAlpha) {
        int alpha;
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;
        if(isAlpha) {
            alpha = (argb >> 24) & 0xFF;
           return acquireColor(red, green, blue, alpha);
        }
        else
            return acquireColor(red,green,blue);
    }
    public Color acquireColor(int red, int green, int blue, int alpha) {
        int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
        Color color = colorCache.get(argb);
        if (color == null) {
            if (colorPool.isEmpty()) {
                color = new Color(red, green, blue, alpha);
            } else {
                colorPool.pop();
                color = new Color(red, green, blue, alpha);
            }
            colorCache.put(argb, color);
        }
        return color;
    }


    public static ColorPool getInstance(){
        if(instance == null)
            instance = new ColorPool();
        return instance;
    }
}