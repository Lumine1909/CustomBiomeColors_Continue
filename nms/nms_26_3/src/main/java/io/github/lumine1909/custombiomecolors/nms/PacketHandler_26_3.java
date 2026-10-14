package io.github.lumine1909.custombiomecolors.nms;

import ca.spottedleaf.moonrise.patches.fast_palette.FastHashMapPalette;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.*;
import org.bukkit.entity.Player;

import static io.github.lumine1909.custombiomecolors.util.Reflection.*;

public class PacketHandler_26_3 implements PacketHandler {

    private static final MappedRegistry<Biome> REGISTRY = (MappedRegistry<Biome>) MinecraftServer.getServer().registryAccess().lookup(Registries.BIOME).orElseThrow();
    private static final int PLAINS_ID = REGISTRY.getId(REGISTRY.get(Identifier.fromNamespaceAndPath("minecraft", "plains")).orElseThrow().value());
    private static final PalettedContainerFactory CONTAINER_FACTORY = PalettedContainerFactory.create(MinecraftServer.getServer().registryAccess());

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
                LevelChunkSection section = new LevelChunkSection(CONTAINER_FACTORY.createForBlockStates(), CONTAINER_FACTORY.createForBiomes());
                section.readBiomes(readBuf);
                writeBiomes(writeBuf, section);
            }
        }

        protected void modifyChunkData(FriendlyByteBuf readBuf, FriendlyByteBuf writeBuf, int size) {
            for (int index = 0; index < size; index++) {
                LevelChunkSection section = new LevelChunkSection(CONTAINER_FACTORY.createForBlockStates(), CONTAINER_FACTORY.createForBiomes());
                section.read(readBuf);
                writeBuf.writeShort(field$LevelChunkSection$nonEmptyBlockCount.get(section));
                writeBuf.writeShort(field$LevelChunkSection$fluidCount.get(section));
                section.getStates().write(writeBuf, null, index);
                writeBiomes(writeBuf, section);
            }
        }

        protected void writeBiomes(FriendlyByteBuf buf, LevelChunkSection levelChunkSection) {
            PalettedContainer<Holder<Biome>> container = (PalettedContainer<Holder<Biome>>) levelChunkSection.getBiomes();
            BitStorage storage = field$PalettedContainer$Data$storage.getUntyped(field$PalettedContainer$data.get(container));
            PalettedContainer.Data<Holder<Biome>> containerData = field$PalettedContainer$data.getUntyped(container);
            Palette<Holder<Biome>> palette = field$PalettedContainer$Data$palette.getUntyped(containerData);

            buf.writeByte(storage.getBits());
            switch (palette) {
                case SingleValuePalette<Holder<Biome>> single -> buf.writeVarInt(getModifiedId(single.valueFor(0)));
                case GlobalPalette<Holder<Biome>> global -> {}
                case FastHashMapPalette<Holder<Biome>> fastHashMap -> {
                    Holder<Biome>[] idToValue = field$FastHashmapPalette$idToValue.getUntyped(fastHashMap);
                    int bits = field$FastHashmapPalette$bits.getInt(fastHashMap);
                    if (bits == 0) {
                        buf.writeVarInt(getModifiedId(idToValue[0]));
                    } else {
                        buf.writeVarInt(idToValue.length);
                        for (Holder<Biome> biomeHolder : idToValue) {
                            buf.writeVarInt(getModifiedId(biomeHolder));
                        }
                    }
                }
                default -> {
                    int size = palette.getSize();
                    buf.writeVarInt(size);
                    for (int i = 0; i < size; i++) {
                        buf.writeVarInt(getModifiedId(palette.valueFor(i)));
                    }
                }
            }
            buf.writeFixedSizeLongArray(storage.getRaw());
        }
    }
}