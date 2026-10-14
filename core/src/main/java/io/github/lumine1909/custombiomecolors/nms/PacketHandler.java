package io.github.lumine1909.custombiomecolors.nms;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.lumine1909.custombiomecolors.util.Reflection.*;

public interface PacketHandler {

    Map<String, Long> createTimeCache = new HashMap<>();
    String HANDLER_NAME = "cbc-handler";

    default void inject() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            injectPlayer(player);
        }
    }

    default void uninject() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            uninjectPlayer(player);
        }
    }

    default void injectPlayer(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        Channel channel = serverPlayer.connection.connection.channel;
        if (channel.pipeline().get(HANDLER_NAME) != null) {
            channel.pipeline().remove(HANDLER_NAME);
        }
        channel.pipeline().addBefore("packet_handler", HANDLER_NAME, getInterceptor(player));
    }

    default void uninjectPlayer(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        Channel channel = serverPlayer.connection.connection.channel;
        if (channel.pipeline().get(HANDLER_NAME) != null) {
            channel.pipeline().remove(HANDLER_NAME);
        }
    }

    default void updateCache(String id, long time) {
        createTimeCache.put(id, time);
    }

    Interceptor getInterceptor(Player player);

    abstract class Interceptor extends ChannelDuplexHandler {

        protected final ServerPlayer player;
        protected final long joinTime;
        protected long warnTime = 0;

        public Interceptor(Player player) {
            this.player = ((CraftPlayer) player).getHandle();
            joinTime = System.currentTimeMillis();
        }

        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof ClientboundChunksBiomesPacket(
                List<ClientboundChunksBiomesPacket.ChunkBiomeData> chunkBiomeDatas
            )) {
                ServerLevel level = player.level().getMinecraftWorld();
                List<ClientboundChunksBiomesPacket.ChunkBiomeData> dataList = new ArrayList<>(chunkBiomeDatas.size());
                for (ClientboundChunksBiomesPacket.ChunkBiomeData c : chunkBiomeDatas) {
                    FriendlyByteBuf writeBuf = new FriendlyByteBuf(Unpooled.buffer());
                    modifyBiomeData(c.getReadBuffer(), writeBuf, level.getSectionsCount());
                    ClientboundChunksBiomesPacket.ChunkBiomeData data = new ClientboundChunksBiomesPacket.ChunkBiomeData(c.pos(), ByteBufUtil.getBytes(writeBuf));
                    dataList.add(data);
                }
                msg = new ClientboundChunksBiomesPacket(dataList);
            } else if (msg instanceof ClientboundLevelChunkWithLightPacket packet) {
                ServerLevel level = (ServerLevel) player.level();
                ClientboundLevelChunkPacketData data = field$ClientboundLevelChunkWithLightPacket$chunkData.getUntyped(packet);
                FriendlyByteBuf writeBuf = new FriendlyByteBuf(Unpooled.buffer());
                modifyChunkData(data.getReadBuffer(), writeBuf, level.getSectionsCount());
                field$ClientboundLevelChunkPacketData$buffer.set(data, ByteBufUtil.getBytes(writeBuf));
            }
            super.write(ctx, msg, promise);
        }

        protected void warn() {
            if (System.currentTimeMillis() - warnTime > 30000) {
                warnTime = System.currentTimeMillis();
                player.getBukkitEntity().sendMessage(Component.text("[CustomBiomeColors] You are loading a chunk with un-synchronized biome, it will be default to plains, please re-join to get the real color!", NamedTextColor.RED));
            }
        }

        protected abstract MappedRegistry<Biome> registry();

        protected abstract int plainsId();

        protected BitStorage getStorageAndWritePalette(FriendlyByteBuf buf, LevelChunkSection levelChunkSection) {
            PalettedContainer<Holder<Biome>> container = (PalettedContainer<Holder<Biome>>) levelChunkSection.getBiomes();
            BitStorage storage = field$PalettedContainer$Data$storage.getUntyped(field$PalettedContainer$data.get(container));
            Object containerData = field$PalettedContainer$data.getUntyped(container);
            Palette<Holder<Biome>> palette = field$PalettedContainer$Data$palette.getUntyped(containerData);

            buf.writeByte(storage.getBits());
            switch (palette) {
                case SingleValuePalette<Holder<Biome>> single -> buf.writeVarInt(getModifiedId(single.valueFor(0)));
                case GlobalPalette<Holder<Biome>> global -> {}
                default -> {
                    int size = palette.getSize();
                    buf.writeVarInt(size);
                    for (int i = 0; i < size; i++) {
                        buf.writeVarInt(getModifiedId(palette.valueFor(i)));
                    }
                }
            }
            return storage;
        }

        protected abstract void writeBiomes(FriendlyByteBuf buf, LevelChunkSection levelChunkSection);

        protected int getModifiedId(Holder<Biome> origin) {
            long createTime = createTimeCache.getOrDefault(origin.getRegisteredName(), 0L);
            if (createTime > joinTime) {
                warn();
                return plainsId();
            }
            return registry().getId(origin.value());
        }

        protected abstract void modifyBiomeData(FriendlyByteBuf readBuf, FriendlyByteBuf writeBuf, int size);

        protected abstract void modifyChunkData(FriendlyByteBuf readBuf, FriendlyByteBuf writeBuf, int size);
    }
}