package io.github.lumine1909.custombiomecolors.nms;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public interface PacketHandler {

    Map<String, Long> createTimeCache = new HashMap<>();

    void inject();

    void uninject();

    void injectPlayer(Player player);

    void uninjectPlayer(Player player);

    default void updateCache(String id, long time) {
        createTimeCache.put(id, time);
    }
}