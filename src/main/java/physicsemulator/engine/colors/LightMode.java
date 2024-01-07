package physicsemulator.engine.colors;

import java.awt.*;

public class LightMode implements VectorColorsModel {
    private static LightMode instance;
    private LightMode(){

    }
    public static LightMode getInstance(){
        if(instance == null)
            instance = new LightMode();
        return instance;
    }
    private Color ForceColor = new Color(0xA80000);
    private Color AccelerationColor = new Color(0x7C007C);
    private Color MomentumColor = new Color(0xba6600);
    private Color VelocityColor = new Color(0x00249F);
    private Color PositionColor = new Color(0x188C00);
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
        return null;
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
