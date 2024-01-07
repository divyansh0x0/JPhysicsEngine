package material.tools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

public class ShadowGenerator {
    private static final double fadeFactor = 2;
    public static BufferedImage generateShadow(int width, int height, Direction direction, Color shadowColor) {
        // Determine the size of the buffer image
        int bufferWidth = width * 5;
        int bufferHeight = height * 5;

        // Create a buffer image to generate the shadow
        BufferedImage bufferImage = new BufferedImage(bufferWidth, bufferHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferImage.createGraphics();

        // Set the background color to transparent
        g2d.setBackground(new Color(0, 0, 0, 0));
        g2d.clearRect(0, 0, bufferWidth, bufferHeight);

        // Determine the shadow length based on the smaller dimension (width or height)
        int shadowLength = Math.min(bufferWidth, bufferHeight);

        // Create a gradient paint for the shadow
        GradientPaint gradient;
        switch (direction) {
            case RIGHT:
                gradient = new GradientPaint(bufferWidth, bufferHeight, new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), 0),
                        bufferWidth - shadowLength, bufferHeight, shadowColor);
                break;
            case LEFT:
                gradient = new GradientPaint(0, bufferHeight, new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), 0),
                        shadowLength, bufferHeight, shadowColor);
                break;
            case UP:
                gradient = new GradientPaint(bufferWidth, 0, new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), 0),
                        bufferWidth, shadowLength, shadowColor);
                break;
            case DOWN:
                gradient = new GradientPaint(bufferWidth, bufferHeight, new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), 0),
                        bufferWidth, bufferHeight - shadowLength, shadowColor);
                break;
            default:
                throw new IllegalArgumentException("Invalid direction.");
        }

        // Fill the shadow area with the gradient paint
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, bufferWidth, bufferHeight);

        g2d.dispose();

        // Scale down the buffer image to the desired size
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D scaledGraphics = scaledImage.createGraphics();
        scaledGraphics.drawImage(bufferImage, 0, 0, width, height, null);
        scaledGraphics.dispose();

        // Apply the blur effect to the shadow
        BufferedImage blurredImage = applyBlur(scaledImage, 15);

        return blurredImage;
    }
    private static BufferedImage applyBlur(BufferedImage image, int radius) {
        float[] kernelData = new float[radius * radius];
        Arrays.fill(kernelData, 1.0f / (radius * radius));
        Kernel kernel = new Kernel(radius, radius, kernelData);
        ConvolveOp convolveOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return convolveOp.filter(image, null);
    }

    public enum Direction {
        RIGHT, LEFT, UP, DOWN
    }
}