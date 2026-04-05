package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.ColorData;
import org.bukkit.Location;
import org.bukkit.World;

public interface ExtraDataHandler {

    default ColorData getDimensionColor(Location location) {
        return null;
    }

    default void modifyEnvironmentSampler(World world) {
    }
}
