package material.component;

import java.awt.*;
import material.utils.Log;

public class MaterialImage extends MaterialComponent{
    private Image image;
    private int cornerRadius = 0;

    public MaterialImage(Image image, int cornerRadius) {
        this.image = image;
        this.cornerRadius = cornerRadius;
    }

    public MaterialImage(Image image) {
        this(image, 0);
    }

    public MaterialImage(){
        this(null);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(image,0,0,null);
    }

    @Override
    public void updateTheme() {

    }

    public Image getImage() {
        return image;
    }

    public MaterialImage setImage(Image image) {
        this.image = image;
        Log.info("image: " + image);
        repaint();
        return this;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public MaterialImage setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        repaint();
        return this;
    }
}
