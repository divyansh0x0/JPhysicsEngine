package material.constants;

public class Point2D {
    double x;
    double y;
    public Point2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setCoordinates(double x, double y) {
        setX(x);
        setY(y);
    }
}
