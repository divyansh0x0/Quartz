package material.theme.models;

import material.theme.enums.Elevation;
import org.jetbrains.annotations.Nullable;

public interface ElevationModel {
    public @Nullable Elevation getElevation();

    /**
     * ElevationColors of the panel. Null is used to set the panel transparent
     * (Note: setOpaque() should be true for it to work)
     */
    ElevationModel setElevation(@Nullable Elevation elevation);
}
