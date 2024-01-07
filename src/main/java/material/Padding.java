package material;

public class Padding {
    private int top;
    private int right;
    private int bottom;
    private int left;

    public Padding(int top, int right, int bottom, int left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public Padding(int uniform) {
        this(uniform,uniform,uniform,uniform);
    }

    public Padding(int vertical, int horizontal){
        this(vertical,horizontal,vertical,horizontal);
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getHorizontal(){
        return left + right;
    }
    public int getVertical(){
        return top + bottom;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Padding padding = (Padding) o;
        return top == padding.top && right == padding.right && bottom == padding.bottom && left == padding.left;
    }
}
