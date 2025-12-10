package io.github.lumine1909.custombiomecolors.util;

import org.bukkit.Bukkit;

public class VersionUtil {

    private static int cachedVersion = -1;

    public static int obtainVersion() {
        if (cachedVersion == -1) {
            cachedVersion = obtainVersion(Bukkit.getMinecraftVersion());
        }
        return cachedVersion;
    }

    public static int obtainVersion(String version) {
        try {
            String[] versions = version.split("\\.");
            if (versions.length == 2) {
                return Integer.parseInt(versions[1]) * 100;
            } else if (versions.length == 3) {
                return Integer.parseInt(versions[1]) * 100 + Integer.parseInt(versions[2]);
            }
        } catch (Exception ignored) {
        }
        return -1;
    }
}