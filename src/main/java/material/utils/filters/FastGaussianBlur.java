package material.utils.filters;

import java.awt.image.*;
import material.utils.GraphicsUtils;

public class FastGaussianBlur extends AbstractFilter {
    private final int radius;

    /**
     * <p>Creates a new blur filter with a default radius of 3.</p>
     */
    public FastGaussianBlur() {
        this(3);
    }

    /**
     * <p>Creates a new blur filter with the specified radius. If the radius
     * is lower than 1, a radius of 1 will be used automatically.</p>
     *
     * @param radius the radius, in pixels, of the blur
     */
    public FastGaussianBlur(int radius) {
        if (radius < 1) {
            radius = 1;
        }

        this.radius = radius;
    }

    /**
     * <p>Returns the radius used by this filter, in pixels.</p>
     *
     * @return the radius of the blur
     */
    public int getRadius() {
        return radius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();

        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }

        int[] srcPixels = new int[width * height];
        int[] dstPixels = new int[width * height];

        GraphicsUtils.getPixels(src, 0, 0, width, height, srcPixels);
        // horizontal pass
        blur(srcPixels, dstPixels, width, height, radius);
        // vertical pass
        blur(dstPixels, srcPixels, height, width, radius);
        // the result is now stored in srcPixels due to the 2nd pass
        GraphicsUtils.setPixels(dst, 0, 0, width, height, srcPixels);

        return dst;
    }

    /**
     * <p>Blurs the source pixels into the destination pixels. The force of
     * the blur is specified by the radius which must be greater than 0.</p>
     * <p>The source and destination pixels arrays are expected to be in the
     * INT_ARGB format.</p>
     *
     * @param srcPixels the source pixels
     * @param dstPixels the destination pixels
     * @param width the width of the source picture
     * @param height the height of the source picture
     * @param radius the radius of the blur effect
     */
    static void blur(int[] srcPixels, int[] dstPixels,
                     int width, int height, int radius) {
        final int windowSize = radius * 2 + 1;
        final int radiusPlusOne = radius + 1;

        int sumAlpha;
        int sumRed;
        int sumGreen;
        int sumBlue;

        int srcIndex = 0;
        int dstIndex;
        int pixel;

        int[] sumLookupTable = new int[256 * windowSize];
        for (int i = 0; i < sumLookupTable.length; i++) {
            sumLookupTable[i] = i / windowSize;
        }

        int[] indexLookupTable = new int[radiusPlusOne];
        if (radius < width) {
            for (int i = 0; i < indexLookupTable.length; i++) {
                indexLookupTable[i] = i;
            }
        } else {
            for (int i = 0; i < width; i++) {
                indexLookupTable[i] = i;
            }
            for (int i = width; i < indexLookupTable.length; i++) {
                indexLookupTable[i] = width - 1;
            }
        }

        for (int y = 0; y < height; y++) {
            sumAlpha = sumRed = sumGreen = sumBlue = 0;
            dstIndex = y;

            pixel = srcPixels[srcIndex];
            sumAlpha += radiusPlusOne * ((pixel >> 24) & 0xFF);
            sumRed   += radiusPlusOne * ((pixel >> 16) & 0xFF);
            sumGreen += radiusPlusOne * ((pixel >>  8) & 0xFF);
            sumBlue  += radiusPlusOne * ( pixel        & 0xFF);

            for (int i = 1; i <= radius; i++) {
                pixel = srcPixels[srcIndex + indexLookupTable[i]];
                sumAlpha += (pixel >> 24) & 0xFF;
                sumRed   += (pixel >> 16) & 0xFF;
                sumGreen += (pixel >>  8) & 0xFF;
                sumBlue  +=  pixel        & 0xFF;
            }

            for  (int x = 0; x < width; x++) {
                dstPixels[dstIndex] = sumLookupTable[sumAlpha] << 24 |
                        sumLookupTable[sumRed]   << 16 |
                        sumLookupTable[sumGreen] <<  8 |
                        sumLookupTable[sumBlue];
                dstIndex += height;

                int nextPixelIndex = x + radiusPlusOne;
                if (nextPixelIndex >= width) {
                    nextPixelIndex = width - 1;
                }

                int previousPixelIndex = x - radius;
                if (previousPixelIndex < 0) {
                    previousPixelIndex = 0;
                }

                int nextPixel = srcPixels[srcIndex + nextPixelIndex];
                int previousPixel = srcPixels[srcIndex + previousPixelIndex];

                sumAlpha += (nextPixel     >> 24) & 0xFF;
                sumAlpha -= (previousPixel >> 24) & 0xFF;

                sumRed += (nextPixel     >> 16) & 0xFF;
                sumRed -= (previousPixel >> 16) & 0xFF;

                sumGreen += (nextPixel     >> 8) & 0xFF;
                sumGreen -= (previousPixel >> 8) & 0xFF;

                sumBlue += nextPixel & 0xFF;
                sumBlue -= previousPixel & 0xFF;
            }

            srcIndex += width;
        }
    }
}
