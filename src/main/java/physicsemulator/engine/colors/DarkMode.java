package physicsemulator.engine.colors;

import java.awt.*;

public class DarkMode implements VectorColorsModel{
    private static DarkMode instance;
    private DarkMode(){

    }
    public static DarkMode getInstance(){
        if(instance == null)
            instance = new DarkMode();
        return instance;
    }
    private final Color ForceColor = new Color(0xFF0000);
    private final Color AccelerationColor = new Color(0xD32BD3);
    private final Color MomentumColor = new Color(0xD39540);
    private final Color VelocityColor = new Color(0x234FE3);
    private final Color PositionColor = new Color(0x4AD02D);
    @Override
    public Color getForceColor() {
        return ForceColor;
    }

    @Override
    public Color getAccelerationColor() {
        return AccelerationColor;
    }

    @Override
    public Color getMomentumColor() {
        return MomentumColor;
    }

    @Override
    public Color getVelocityColor() {
        return VelocityColor;
    }

    @Override
    public Color getPositionColor() {
        return PositionColor;
    }
}
