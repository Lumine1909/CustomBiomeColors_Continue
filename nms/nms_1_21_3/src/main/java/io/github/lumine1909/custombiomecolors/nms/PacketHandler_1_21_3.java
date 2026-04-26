package io.github.lumine1909.custombiomecolors.nms;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BitStorage;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.*;
import org.bukkit.entity.Player;

import static io.github.lumine1909.custombiomecolors.util.Reflection.*;

public class PacketHandler_1_21_3 implements PacketHandler {

    private static final MappedRegistry<Biome> REGISTRY = (MappedRegistry<Biome>) MinecraftServer.getServer().registryAccess().lookup(Registries.BIOME).orElseThrow();
    private static final int PLAINS_ID = REGISTRY.getId(REGISTRY.get(ResourceLocation.fromNamespaceAndPath("minecraft", "plains")).orElseThrow().value());

    @Override
    public Interceptor getInterceptor(Player player) {
        return new PacketInterceptor(player);
    }

    private static final class PacketInterceptor extends Interceptor {

        public PacketInterceptor(Player player) {
            super(player);
        }

        protected void modifyBiomeData(FriendlyByteBuf readBuf, FriendlyByteBuf writeBuf, int size) {
            for (int index = 0; index < size; index++) {
                LevelChunkSection section = new LevelChunkSection(
                    new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES, null),
                    new PalettedContainer<>(REGISTRY.asHolderIdMap(), REGISTRY.getOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES, null)
                );
                section.readBiomes(readBuf);
                writeBiomes(writeBuf, section);
            }
        }

        protected void modifyChunkData(FriendlyByteBuf readBuf, FriendlyByteBuf writeBuf, int size) {
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
            BitStorage storage = field$PalettedContainer$Data$storage.getUntyped(field$PalettedContainer$data.get(container));
            Object containerData = field$PalettedContainer$data.get(container);
            Palette<Holder<Biome>> palette = field$PalettedContainer$Data$palette.getUntyped(containerData);

            buf.writeByte(storage.getBits());
            switch (palette) {
                case SingleValuePalette<Holder<Biome>> single ->
                    buf.writeVarInt(getModifiedId(field$SingleValuePalette$value.getUntyped(single)));
                case LinearPalette<Holder<Biome>> linear -> {
                    Object[] array = field$LinearPalette$values.getUntyped(linear);
                    int size = linear.getSize();
                    buf.writeVarInt(size);
                    for (int i = 0; i < size; i++) {
                        buf.writeVarInt(getModifiedId((Holder<Biome>) array[i]));
                    }
                }
                case HashMapPalette<Holder<Biome>> hashMap -> {
                    CrudeIncrementalIntIdentityHashBiMap<Holder<Biome>> map = field$HashMapPalette$values.getUntyped(hashMap);
                    int size = hashMap.getSize();
                    buf.writeVarInt(size);
                    for (int i = 0; i < size; i++) {
                        buf.writeVarInt(getModifiedId(map.byId(i)));
                    }
                }
                default -> throw new IllegalStateException("Unknown value: " + palette);
            }
            buf.writeLongArray(storage.getRaw());
        }

        private int getModifiedId(Holder<Biome> origin) {
            long createTime = createTimeCache.getOrDefault(origin.getRegisteredName(), 0L);
            if (createTime > joinTime) {
                warn();
                return PLAINS_ID;
            }
            return REGISTRY.getId(origin.value());
        }
    }
}