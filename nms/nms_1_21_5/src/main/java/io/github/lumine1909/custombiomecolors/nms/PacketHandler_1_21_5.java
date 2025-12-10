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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.BitStorage;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static io.github.lumine1909.custombiomecolors.util.Reflection.*;

public class PacketHandler_1_21_5 implements PacketHandler {

    private static final MappedRegistry<Biome> REGISTRY = (MappedRegistry<Biome>) MinecraftServer.getServer().registryAccess().lookup(Registries.BIOME).orElseThrow();
    private static final int PLAINS_ID = REGISTRY.getId(REGISTRY.get(ResourceLocation.fromNamespaceAndPath("minecraft", "plains")).orElseThrow().value());

    @Override
    public void inject() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            injectPlayer(player);
        }
    }

    @Override
    public void uninject() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            uninjectPlayer(player);
        }
    }

    @Override
    public void injectPlayer(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        Channel channel = serverPlayer.connection.connection.channel;
        if (channel.pipeline().get(HANDLER_NAME) != null) {
            channel.pipeline().remove(HANDLER_NAME);
        }
        channel.pipeline().addBefore("packet_handler", HANDLER_NAME, new PacketInterceptor(player));
    }

    @Override
    public void uninjectPlayer(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        Channel channel = serverPlayer.connection.connection.channel;
        if (channel.pipeline().get(HANDLER_NAME) != null) {
            channel.pipeline().remove(HANDLER_NAME);
        }
    }

    private static final class PacketInterceptor extends ChannelDuplexHandler {

        private final ServerPlayer player;
        private final long joinTime;
        private long warnTime = 0;

        public PacketInterceptor(Player player) {
            this.player = ((CraftPlayer) player).getHandle();
            joinTime = System.currentTimeMillis();
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            if (msg instanceof ClientboundChunksBiomesPacket(
                List<ClientboundChunksBiomesPacket.ChunkBiomeData> chunkBiomeData
            )) {
                ServerLevel level = player.level().getMinecraftWorld();
                List<ClientboundChunksBiomesPacket.ChunkBiomeData> dataList = new ArrayList<>(chunkBiomeData.size());
                chunkBiomeData.forEach(c -> {
                    FriendlyByteBuf writeBuf = new FriendlyByteBuf(Unpooled.buffer());
                    modifyBiomeData(writeBuf, c.getReadBuffer(), level.getSectionsCount());
                    ClientboundChunksBiomesPacket.ChunkBiomeData data = new ClientboundChunksBiomesPacket.ChunkBiomeData(c.pos(), ByteBufUtil.getBytes(writeBuf));
                    dataList.add(data);
                });
                msg = new ClientboundChunksBiomesPacket(dataList);
            } else if (msg instanceof ClientboundLevelChunkWithLightPacket packet) {
                ServerLevel level = player.level().getMinecraftWorld();
                ClientboundLevelChunkPacketData data = packet.getChunkData();
                FriendlyByteBuf writeBuf = new FriendlyByteBuf(Unpooled.buffer());
                modifyChunkData(data.getReadBuffer(), writeBuf, level.getSectionsCount());
                field$ClientboundLevelChunkPacketData$buffer.set(data, ByteBufUtil.getBytes(writeBuf));
            }
            super.write(ctx, msg, promise);
        }

        private void modifyBiomeData(FriendlyByteBuf readBuf, FriendlyByteBuf writeBuf, int size) {
            for (int index = 0; index < size; index++) {
                LevelChunkSection section = new LevelChunkSection(
                    new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES, null),
                    new PalettedContainer<>(REGISTRY.asHolderIdMap(), REGISTRY.getOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES, null)
                );
                section.readBiomes(readBuf);
                writeBiomes(writeBuf, section);
            }
        }

        private void modifyChunkData(FriendlyByteBuf readBuf, FriendlyByteBuf writeBuf, int size) {
            for (int index = 0; index < size; index++) {
                LevelChunkSection section = new LevelChunkSection(
                    new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES, null),
                    new PalettedContainer<>(REGISTRY.asHolderIdMap(), REGISTRY.getOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES, null)
                );
                section.read(readBuf);
                writeBuf.writeShort(field$LevelChunkSection$nonEmptyBlockCount.get(section));
                section.states.write(writeBuf, null, index);
                writeBiomes(writeBuf, section);
            }
        }

        @SuppressWarnings({"DataFlowIssue", "unchecked"})
        private void writeBiomes(FriendlyByteBuf buf, LevelChunkSection levelChunkSection) {
            PalettedContainer<Holder<Biome>> container = (PalettedContainer<Holder<Biome>>) levelChunkSection.getBiomes();
            BitStorage storage = field$PalettedContainer$Data$storage.getUnsafe(field$PalettedContainer$data.get(container));
            Object containerData = field$PalettedContainer$data.get(container);
            Palette<Holder<Biome>> palette = field$PalettedContainer$Data$palette.getUnsafe(containerData);

            buf.writeByte(storage.getBits());
            if (palette instanceof SingleValuePalette<Holder<Biome>> single) {
                buf.writeVarInt(getModifiedId(field$SingleValuePalette$value.getUnsafe(single)));
            } else if (palette instanceof LinearPalette<Holder<Biome>> linear) {
                Object[] array = field$LinearPalette$values.getUnsafe(linear);
                buf.writeVarInt(linear.getSize());
                for (int i = 0; i < linear.getSize(); i++) {
                    buf.writeVarInt(getModifiedId((Holder<Biome>) array[i]));
                }
            } else if (palette instanceof HashMapPalette<Holder<Biome>> hashMap) {
                CrudeIncrementalIntIdentityHashBiMap<Holder<Biome>> map = field$HashMapPalette$values.getUnsafe(hashMap);
                buf.writeVarInt(hashMap.getSize());
                for (int i = 0; i < hashMap.getSize(); i++) {
                    buf.writeVarInt(getModifiedId(map.byId(i)));
                }
            }
            buf.writeFixedSizeLongArray(storage.getRaw());
        }

        private int getModifiedId(Holder<Biome> origin) {
            long createTime = createTimeCache.getOrDefault(origin.getRegisteredName(), 0L);
            if (createTime > joinTime) {
                warn();
                return PLAINS_ID;
            }
            return REGISTRY.getId(origin.value());
        }

        private void warn() {
            if (System.currentTimeMillis() - warnTime > 30000) {
                warnTime = System.currentTimeMillis();
                player.getBukkitEntity().sendMessage(Component.text("[CustomBiomeColors] You are loading a chunk with un-synchronized biome, it will be default to plains, please re-join to get the real color!", NamedTextColor.RED));
            }
        }
    }
}