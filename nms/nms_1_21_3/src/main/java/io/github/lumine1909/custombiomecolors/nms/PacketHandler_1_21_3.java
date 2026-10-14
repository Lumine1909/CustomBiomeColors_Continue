package io.github.lumine1909.custombiomecolors.nms;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.bukkit.entity.Player;

import static io.github.lumine1909.custombiomecolors.util.Reflection.field$LevelChunkSection$nonEmptyBlockCount;

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

        @Override
        protected MappedRegistry<Biome> registry() {
            return REGISTRY;
        }

        @Override
        protected int plainsId() {
            return PLAINS_ID;
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

        protected void writeBiomes(FriendlyByteBuf buf, LevelChunkSection levelChunkSection) {
            buf.writeLongArray(getStorageAndWritePalette(buf, levelChunkSection).getRaw());
        }
    }
}