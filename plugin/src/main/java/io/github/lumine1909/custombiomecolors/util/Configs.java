package io.github.lumine1909.custombiomecolors.util;

import org.bukkit.plugin.Plugin;

public class Configs {

    public static boolean checkUpdate = false;
    public static boolean notifyUpdateOnJoin = false;

    public static void reload(Plugin plugin) {
        plugin.saveDefaultConfig();
        checkUpdate = plugin.getConfig().getBoolean("check-update");
        notifyUpdateOnJoin = plugin.getConfig().getBoolean("notify-update-on-join");
    }
}
