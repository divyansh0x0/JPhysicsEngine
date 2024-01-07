package material.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import material.utils.structures.LanguageCompatibleString;

import java.awt.*;
import java.awt.image.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class GraphicsUtils {
    private static final AtomicReference<HashMap<String, LanguageCompatibleString>> LANGUAGE_COMPATIBLE_STRING_HASH_MAP = new AtomicReference<>();

    static {
        LANGUAGE_COMPATIBLE_STRING_HASH_MAP.set(new HashMap<>());
    }

    public static GraphicsConfiguration getGraphicsConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice().getDefaultConfiguration();
    }

    public static String clipString(Graphics2D g2d, String string, int availableWidth) {
        FontMetrics fontMetrics = g2d.getFontMetrics();
        var rect = fontMetrics.getStringBounds(string, g2d);
        int ellipseWidth = fontMetrics.stringWidth(StringUtils.ELLIPSIS);
        if (availableWidth < rect.getWidth()) {
            StringBuilder sb = new StringBuilder();
            int cWidth = 0; //clippedWidth
            for (char c : string.toCharArray()) {
                int charWidth = fontMetrics.charWidth(c);
                if (cWidth + charWidth + ellipseWidth < availableWidth) {
                    sb.append(c);
                    cWidth += charWidth;
                } else {
                    break;
                }
            }
            return sb.toString().strip() + StringUtils.ELLIPSIS;
        } else return string;

    }

    public static LanguageCompatibleString getLanguageCompatibleString(String str, Font font) {
        if(str != null && !str.isEmpty()) {
            LanguageCompatibleString preloaded = LANGUAGE_COMPATIBLE_STRING_HASH_MAP.get().get(str);
            if (preloaded == null) {
                LanguageCompatibleString languageCompatibleString = new LanguageCompatibleString(str);
                StringBuilder sb = new StringBuilder();
                boolean isSbCompatible = font.canDisplay(str.charAt(0)); //flag to check if string builder contains compatible strings
                for (char c : str.toCharArray()) {
                    if (font.canDisplay(c)) {
                        if (sb.length() > 0 && !isSbCompatible) { // If the string builder was filled with incompatible strings then add it to the LanguageCompatibleString
                            languageCompatibleString.addString(sb.toString(), false);
                            sb.delete(0, sb.length()); //Empty string builder
                            isSbCompatible = true;
                        }
                        sb.append(c);
                    } else {
                        if (sb.length() > 0 && isSbCompatible) { // If the string builder was filled with compatible strings then add it to the LanguageCompatibleString
                            languageCompatibleString.addString(sb.toString(), true);
                            sb.delete(0, sb.length()); //then empty the builder
                            isSbCompatible = false;
                        }
                        sb.append(c);
                    }
                }

                if (sb.length() > 0) {
                    languageCompatibleString.addString(sb.toString(), isSbCompatible);
                }
                LANGUAGE_COMPATIBLE_STRING_HASH_MAP.getAcquire().put(str, languageCompatibleString);
                return languageCompatibleString;
            } else
                return preloaded;
        }
        return new LanguageCompatibleString("");
    }

    /**
     * Merges image into a 2x2 grid
     *
     * @param resultantSize size of the image to return
     * @param images        must be more than 3 otherwise first image is rescaled and returned;
     * @return 2x2 image if possible
     */

    public static BufferedImage imageMerger(int resultantSize, Image... images) {
        BufferedImage result = null;
        if (images != null) {
            result = new BufferedImage(resultantSize, resultantSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) result.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x, y;
//            Log.info("Images length: " + images.length);
            if (images.length >= 4) {
                x = 0;
                y = 0;
                int center = resultantSize / 2;
                int imageSize = resultantSize / 2;
                for (int i = 0; i < 4; i++) {
                    switch (i) {
                        case 1 -> {
                            x = center;
                            y = 0;
                        }
                        case 2 -> {
                            x = 0;
                            y = center;
                        }
                        case 3 -> {
                            x = center;
                            y = center;
                        }
                    }

                    g2d.drawImage(images[i], x, y, imageSize, imageSize, null);
                }
            } else {
                g2d.drawImage(images[0], 0, 0, resultantSize, resultantSize, null);
            }
        }
        return result;
    }

    public static BufferedImage createCompatibleTranslucentImage(int width,
                                                                 int height) {
        return getGraphicsConfiguration().createCompatibleImage(width, height,
                Transparency.TRANSLUCENT);
    }

    /**
     * <p>Returns an array of pixels, stored as integers, from a
     * <code>BufferedImage</code>. The pixels are grabbed from a rectangular
     * area defined by a location and two dimensions. Calling this method on
     * an image of type different from <code>BufferedImage.TYPE_INT_ARGB</code>
     * and <code>BufferedImage.TYPE_INT_RGB</code> will unmanage the image.</p>
     *
     * @param img    the source image
     * @param x      the x location at which to start grabbing pixels
     * @param y      the y location at which to start grabbing pixels
     * @param w      the width of the rectangle of pixels to grab
     * @param h      the height of the rectangle of pixels to grab
     * @param pixels a pre-allocated array of pixels of size w*h; can be null
     * @return <code>pixels</code> if non-null, a new array of integers
     * otherwise
     * @throws IllegalArgumentException is <code>pixels</code> is non-null and
     *                                  of length &lt; w*h
     */
    public static int[] getPixels(BufferedImage img,
                                  int x, int y, int w, int h, int[] pixels) {
        if (w == 0 || h == 0) {
            return new int[0];
        }

        if (pixels == null) {
            pixels = new int[w * h];
        } else if (pixels.length < w * h) {
            throw new IllegalArgumentException("pixels array must have a length" +
                    " >= w*h");
        }

        int imageType = img.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB ||
                imageType == BufferedImage.TYPE_INT_RGB) {
            Raster raster = img.getRaster();
            return (int[]) raster.getDataElements(x, y, w, h, pixels);
        }

        // Unmanages the image
        return img.getRGB(x, y, w, h, pixels, 0, w);
    }

    /**
     * <p>Writes a rectangular area of pixels in the destination
     * <code>BufferedImage</code>. Calling this method on
     * an image of type different from <code>BufferedImage.TYPE_INT_ARGB</code>
     * and <code>BufferedImage.TYPE_INT_RGB</code> will unmanage the image.</p>
     *
     * @param img    the destination image
     * @param x      the x location at which to start storing pixels
     * @param y      the y location at which to start storing pixels
     * @param w      the width of the rectangle of pixels to store
     * @param h      the height of the rectangle of pixels to store
     * @param pixels an array of pixels, stored as integers
     * @throws IllegalArgumentException is <code>pixels</code> is non-null and
     *                                  of length &lt; w*h
     */
    public static void setPixels(BufferedImage img,
                                 int x, int y, int w, int h, int[] pixels) {
        if (pixels == null || w == 0 || h == 0) {
            return;
        } else if (pixels.length < w * h) {
            throw new IllegalArgumentException("pixels array must have a length" +
                    " >= w*h");
        }

        int imageType = img.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB ||
                imageType == BufferedImage.TYPE_INT_RGB) {
            WritableRaster raster = img.getRaster();
            raster.setDataElements(x, y, w, h, pixels);
        } else {
            // Unmanages the image
            img.setRGB(x, y, w, h, pixels, 0, w);
        }
    }

    public static String truncateString(Graphics2D g2d, String str, double maxWidth) {
        FontMetrics fontMetrics = g2d.getFontMetrics();
        var rect = fontMetrics.getStringBounds(str, g2d);
        boolean lineBroken = false;
        if (maxWidth < rect.getWidth()) {
            StringBuilder sb = new StringBuilder();
            int cWidth = 0; //clippedWidth
            for (char c : str.toCharArray()) {
                if (lineBroken && Character.isWhitespace(c)) // Removing white spaces from being added in beginning of new line
                    continue;
                else if (lineBroken && !Character.isWhitespace(c)) {
                    lineBroken = false;
                }
                int charWidth = fontMetrics.charWidth(c);
                if (cWidth + charWidth <= maxWidth) {
                    sb.append(c);
                    cWidth += charWidth;
                } else { // line break
                    sb.append('\n');
                    cWidth = 0;
                    lineBroken = true;
                }
            }
            return sb.toString().strip() + StringUtils.ELLIPSIS;
        } else return str;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = dimg.createGraphics();
        setHighDefinitionHints(g2d);
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        img.flush();
        return dimg;
    }

    public static void setHighDefinitionHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_RESOLUTION_VARIANT, RenderingHints.VALUE_RESOLUTION_VARIANT_DPI_FIT);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    @Contract("_, _ -> param1")
    public static @NotNull VolatileImage blur(@NotNull VolatileImage image, int blurRadius) {
        // Create a compatible buffered image for blurring
        GraphicsConfiguration graphicsConfig = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bufferedImage = graphicsConfig.createCompatibleImage(image.getWidth(), image.getHeight());

        // Draw the volatile image onto the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // Get the data buffer of the buffered image
        DataBuffer dataBuffer = bufferedImage.getRaster().getDataBuffer();

        // Apply blurring by averaging pixel values
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                int redSum = 0;
                int greenSum = 0;
                int blueSum = 0;
                int pixelCount = 0;

                for (int blurY = -blurRadius; blurY <= blurRadius; blurY++) {
                    int newY = y + blurY;

                    if (newY >= 0 && newY < bufferedImage.getHeight()) {
                        for (int blurX = -blurRadius; blurX <= blurRadius; blurX++) {
                            int newX = x + blurX;

                            if (newX >= 0 && newX < bufferedImage.getWidth()) {
                                int pixel = dataBuffer.getElem(newY * bufferedImage.getWidth() + newX);

                                int red = (pixel >> 16) & 0xFF;
                                int green = (pixel >> 8) & 0xFF;
                                int blue = pixel & 0xFF;

                                redSum += red;
                                greenSum += green;
                                blueSum += blue;
                                pixelCount++;
                            }
                        }
                    }
                }

                int averagedRed = redSum / pixelCount;
                int averagedGreen = greenSum / pixelCount;
                int averagedBlue = blueSum / pixelCount;

                int blurredPixel = (averagedRed << 16) | (averagedGreen << 8) | averagedBlue;
                dataBuffer.setElem(y * bufferedImage.getWidth() + x, blurredPixel);
            }
        }

        // Draw the buffered image back onto the volatile image
        g2d = image.createGraphics();
        g2d.drawImage(bufferedImage, 0, 0, null);
        g2d.dispose();
        return image;
    }
}
