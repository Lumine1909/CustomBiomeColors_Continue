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
            return Integer.parseInt(versions[0]) * 10000
                + (versions.length > 1 ? Integer.parseInt(versions[1]) : 0) * 100
                + (versions.length > 2 ? Integer.parseInt(versions[2]) : 0);
        } catch (Exception ignored) {
        }
        return -1;
    }
}