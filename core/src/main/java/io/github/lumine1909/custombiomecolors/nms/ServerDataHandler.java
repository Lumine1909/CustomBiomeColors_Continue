package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.Collection;
import java.util.IdentityHashMap;

import static io.github.lumine1909.custombiomecolors.util.Reflection.*;

public interface ServerDataHandler<Biome, Holder, ResourceKey> extends DimensionDataAccessor {

    BiomeAccessor<Biome, Holder, ResourceKey> getBiomeFromKey(BiomeKey biomeKey);

    BiomeAccessor<Biome, Holder, ResourceKey> wrapToAccessor(Holder biomeBase);

    boolean hasBiome(BiomeKey biomeKey);

    BiomeAccessor<Biome, Holder, ResourceKey> createCustomBiome(BiomeData biomeData);

    @SuppressWarnings("unchecked")
    default void setBiomeAt(Location location, BiomeAccessor<Biome, Holder, ResourceKey> biomeAccessor) {
        BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Level nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

        net.minecraft.world.level.chunk.LevelChunk chunk = nmsWorld.getChunkAt(blockPosition);
        chunk.setBiome(location.getBlockX() >> 2, location.getBlockY() >> 2, location.getBlockZ() >> 2, (net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome>) biomeAccessor.getBiomeHolder());
    }

    @SuppressWarnings("unchecked")
    default Holder getBiomeAt(Location location) {
        BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Level nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

        net.minecraft.world.level.chunk.LevelChunk chunk = nmsWorld.getChunkAt(blockPosition);
        return (Holder) chunk.getNoiseBiome(location.getBlockX() >> 2, location.getBlockY() >> 2, location.getBlockZ() >> 2);
    }

    MappedRegistry<Biome> getRegistry();

    Collection<?> getTagList(Holder original);

    @SuppressWarnings("unchecked")
    default Holder registerBiome(Holder original, Biome biome, ResourceKey resourceKey) {
        try {
            field$MappedRegistry$frozen.set(getRegistry(), false);
            field$MappedRegistry$unregisteredIntrusiveHolders.set(getRegistry(), new IdentityHashMap<>());

            getRegistry().createIntrusiveHolder(biome);
            Holder holder = (Holder) getRegistry().register((net.minecraft.resources.ResourceKey<Biome>) resourceKey, biome, RegistrationInfo.BUILT_IN);
            method$Holder$bindTags.invoke(holder, getTagList(original));

            field$MappedRegistry$unregisteredIntrusiveHolders.set(getRegistry(), null);
            field$MappedRegistry$frozen.set(getRegistry(), true);

            return holder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}