package io.github.lumine1909.custombiomecolors.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class FoliaUtil {

    public static final boolean IS_FOLIA;

    static {
        boolean isFolia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException ignored) {
        }
        IS_FOLIA = isFolia;
    }

    public static void runDelayed(Plugin plugin, Runnable runnable, long delay) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), delay);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }
}