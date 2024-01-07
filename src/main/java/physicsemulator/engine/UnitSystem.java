package physicsemulator.engine;

import physicsemulator.utils.Vector2D;

public enum UnitSystem {
    /**
     * 1unit = 1kilogram, 1 meter
     */
    SI,
    /**
     * 1unit = 1pixel
     */
    DEFAULT;

    private static double kilogram = 1000f;
    private static double meter=10f;//10 pixels
    public double convertScreenPixelToUnit(double pixels, UnitType unitType){
        if(this.equals(SI)){
            return switch (unitType){
                case MASS -> pixels * kilogram;
                case DISTANCE -> pixels * meter;
                default -> pixels;
            };
        }
        return pixels;
    }

    public Vector2D converToThisUnitSystem(Vector2D pixels, UnitType unitType){
        if(this.equals(SI)){
            switch (unitType){
                case MASS -> pixels.pointTo(pixels.getX() * kilogram, pixels.getY() * kilogram);
                case DISTANCE -> pixels.pointTo(pixels.getX() * meter, pixels.getY() * meter);
            };
        }
        return pixels;
    }
}
