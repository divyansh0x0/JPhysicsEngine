package material.constants;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Size extends Dimension {
    public Size(int width, int height) {
        super(width,height);
    }

    public Size(int common) {
        this(common, common);
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public static @NotNull Size getGoldenSize(int width) {
        return new Size(width, Math.round(width / 1.61803f));
    }

    public Size swapValues(){
        int temp = height;
        height = width;
        width = height;
        return this;
    }

    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    @Override
    public String toString() {
        return "[width:%d | height:%d]".formatted(width, height);
    }

    public int getWidthInt() {
        return width;
    }
    public int getHeightInt(){
        return height;
    }
}

