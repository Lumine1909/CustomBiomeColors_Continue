package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.ColorData;
import org.bukkit.Location;

public interface DimensionDataAccessor {

    default ColorData getDimensionColor(Location location) {
        return null;
    }
}
