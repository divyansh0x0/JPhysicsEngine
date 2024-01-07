package material.theme.models;

import material.containers.MaterialPanel;
import material.theme.enums.ElevationDP;
import org.jetbrains.annotations.Nullable;

public interface ElevationDPModel {
    public @Nullable ElevationDP getElevationDP();

    /**
     * ElevationColors of the panel. Null is used to set the panel transparent
     * (Note: setOpaque() should be true for it to work)
     */
    ElevationDPModel setElevationDP(@Nullable ElevationDP elevationDP);
}
