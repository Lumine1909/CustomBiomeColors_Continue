package io.github.lumine1909.custombiomecolors.util;

import io.github.lumine1909.custombiomecolors.nms.PacketHandler;
import io.github.lumine1909.custombiomecolors.nms.ServerDataHandler;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NmsLoader {

    private static final NavigableMap<Integer, String> HANDLERS = new TreeMap<>() {{
        put(12005, "io.github.lumine1909.custombiomecolors.nms.{}_1_20_5");
        put(12100, "io.github.lumine1909.custombiomecolors.nms.{}_1_21");
        put(12103, "io.github.lumine1909.custombiomecolors.nms.{}_1_21_3");
        put(12104, "io.github.lumine1909.custombiomecolors.nms.{}_1_21_4");
        put(12105, "io.github.lumine1909.custombiomecolors.nms.{}_1_21_5");
        put(12109, "io.github.lumine1909.custombiomecolors.nms.{}_1_21_9");
        put(12111, "io.github.lumine1909.custombiomecolors.nms.{}_1_21_11");
        put(260000, "io.github.lumine1909.custombiomecolors.nms.{}_26_1");
        put(260300, "io.github.lumine1909.custombiomecolors.nms.{}_26_3");
    }};

    public static ServerDataHandler<?, ?, ?> loadDataHandler(int version) {
        Map.Entry<Integer, String> entry = HANDLERS.floorEntry(version);
        if (entry == null) {
            throw new RuntimeException("Unsupported version: " + version);
        }
        try {
            Class<?> clazz = Class.forName(entry.getValue().replace("{}", "ServerDataHandler"));
            return (ServerDataHandler<?, ?, ?>) clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to load NMS implementation for version " + version, e);
        }
    }

    public static PacketHandler loadPacketHandler(int version) {
        Map.Entry<Integer, String> entry = HANDLERS.floorEntry(version);
        if (entry == null) {
            throw new RuntimeException("Unsupported version: " + version);
        }
        try {
            Class<?> clazz = Class.forName(entry.getValue().replace("{}", "PacketHandler"));
            return (PacketHandler) clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to load NMS implementation for version " + version, e);
        }
    }
}
