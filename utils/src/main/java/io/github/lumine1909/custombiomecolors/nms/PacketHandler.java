package io.github.lumine1909.custombiomecolors.nms;

import io.netty.channel.ChannelHandlerContext;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public interface PacketHandler {

    Map<String, Long> createTimeCache = new HashMap<>();

    static void writeSafely(ChannelHandlerContext ctx, Object msg) {
        ctx.channel().eventLoop().execute(() -> {
            if (!ctx.channel().isActive()) {
                return;
            }
            ctx.writeAndFlush(msg, ctx.voidPromise());
        });
    }

    void inject();

    void uninject();

    void injectPlayer(Player player);

    void uninjectPlayer(Player player);

    default void updateCache(String id, long time) {
        createTimeCache.put(id, time);
    }
}